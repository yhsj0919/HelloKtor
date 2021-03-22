package xyz.yhsj.ktor.redis

import io.ktor.application.*
import io.ktor.util.*
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.reactive.RedisReactiveCommands
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.codec.RedisCodec
import java.nio.ByteBuffer
import java.nio.charset.Charset

class Redis(private val conf: RedisConfiguration) {
    /**
     * configuration of redis ref[lettuce](https://github.com/lettuce-io/lettuce-core)
     *  @author https://github.com/ZenLiuCN/ktor-redis
     * @property url String
     * @constructor
     */
    data class RedisConfiguration(
        var url: String
    )

    fun newClient(): RedisClient = RedisClient.create(conf.url)

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, RedisConfiguration, Redis> {
        override val key: AttributeKey<Redis> = AttributeKey("Redis")
        private lateinit var tmpRedis: Redis
        private val redis get() = tmpRedis
        private val client by lazy { redis.newClient() }
        fun <K : Any, V : Any> newConnection(
            keyEncoder: (K) -> ByteBuffer,
            keyDecoder: (ByteBuffer) -> K,
            valueEncoder: (V) -> ByteBuffer,
            valueDecoder: (ByteBuffer) -> V
        ): StatefulRedisConnection<K, V> = client.connect(
            createCodec<K, V>(
                keyEncoder,
                keyDecoder,
                valueEncoder,
                valueDecoder
            )
        )

        private fun <K : Any, V : Any> newConnection(
            codec: RedisCodec<K, V>
        ): StatefulRedisConnection<K, V> = client.connect(codec)

        fun <K : Any, V : Any> newAsyncClient(codec: RedisCodec<K, V>): RedisAsyncCommands<K, V> =
            newConnection(codec).async()

        fun <K : Any, V : Any> newSyncClient(codec: RedisCodec<K, V>): RedisCommands<K, V> = newConnection(codec).sync()
        fun <K : Any, V : Any> newClient(codec: RedisCodec<K, V>): RedisCommands<K, V> =
            newConnection(codec).sync()

        fun <K : Any, V : Any> newReactiveClient(codec: RedisCodec<K, V>): RedisReactiveCommands<K, V> =
            newConnection(codec).reactive()

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: RedisConfiguration.() -> Unit
        ): Redis {
            return Redis(RedisConfiguration("").apply(configure)).apply {
                this@Feature.tmpRedis = this
            }
        }


        /**
         * create RedisCodec by lambda
         * @param k2b (K) -> ByteBuffer
         * @param b2k (ByteBuffer) -> K
         * @param v2b (V) -> ByteBuffer
         * @param b2v (ByteBuffer) -> V
         * @return RedisCodec<K, V>
         */
        private fun <K : Any, V : Any> createCodec(
            k2b: (K) -> ByteBuffer,
            b2k: (ByteBuffer) -> K,
            v2b: (V) -> ByteBuffer,
            b2v: (ByteBuffer) -> V
        ) = object : RedisCodec<K, V> {
            override fun decodeKey(bytes: ByteBuffer): K = b2k.invoke(bytes)
            override fun encodeValue(value: V): ByteBuffer = v2b.invoke(value)
            override fun encodeKey(key: K): ByteBuffer = k2b.invoke(key)
            override fun decodeValue(bytes: ByteBuffer): V = b2v.invoke(bytes)
        }

        fun ByteBuffer.decodeString(charset: Charset = Charsets.UTF_8): String {
            return charset.decode(this).toString()
        }

    }
}