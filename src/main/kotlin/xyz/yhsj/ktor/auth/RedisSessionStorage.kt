package xyz.yhsj.ktor.auth


import io.ktor.sessions.*
import io.ktor.utils.io.*
import io.lettuce.core.SetArgs
import io.lettuce.core.codec.StringCodec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import xyz.yhsj.ktor.redis.Redis
import java.io.FileNotFoundException
import java.util.*


/**
 * @param timeOut 生命周期，秒
 */
internal class RedisSessionStorage(private val expandKey: String = "session_", private val timeOut: Long = 60 * 60) :
    SessionStorage {
    private val client by lazy { Redis.newClient(StringCodec.UTF8) }
    override suspend fun write(id: String, provider: suspend (ByteWriteChannel) -> Unit) {
        coroutineScope {
            val channel = writer(Dispatchers.Unconfined, autoFlush = true) {
                provider(channel)
            }.channel
            client.set("$expandKey$id", channel.readUTF8Line(), SetArgs.Builder.ex(timeOut))
        }
    }

    override suspend fun <R> read(id: String, consumer: suspend (ByteReadChannel) -> R): R {
        return client.get("$expandKey$id")?.let { data -> consumer(ByteReadChannel(data)) }
            ?: throw NoSuchElementException("Session $id not found")
    }

    override suspend fun invalidate(id: String) {
        try {
            client.del("$expandKey$id")
        } catch (notFound: FileNotFoundException) {
            throw NoSuchElementException("No session data found for id $id")
        }
    }
}

