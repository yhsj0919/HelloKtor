package xyz.yhsj.ktor.auth


import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.concurrent.timer

val scope = MainScope()

@InternalAPI
fun appSessionStorage(rootDir: File, cached: Boolean = true, timeOut: Long = 60000): SessionStorage {
    val logger: Logger = LoggerFactory.getLogger("appSessionStorage")

    val storage = when (cached) {
        true -> CacheStorage(DirectoryStorage(rootDir), 6000)
        false -> DirectoryStorage(rootDir)
    }
    if (timeOut > 0) {
        logger.info("尝试开启携程清理session文件")
        scope.launch(Dispatchers.IO) {
            //测试循环任务
            timer(startAt = Date(), period = 10 * 1000, action = {
                //尝试清理Session
                getSession(rootDir.path)
                    .filter {
                        Date().time - it.lastModified > timeOut
                    }
                    .forEach {
                        scope.launch(Dispatchers.IO) {
                            logger.info("清理Session:${it.id}")
                            storage.invalidate(it.id)
                        }
                    }
            })

        }
    }

    return storage
}

class DirectoryStorage(private val dir: File) : SessionStorage, Closeable {
    init {
        dir.mkdirsOrFail()
    }

    override fun close() {
    }

    override suspend fun write(id: String, provider: suspend (ByteWriteChannel) -> Unit) {
        requireId(id)
        val file = fileOf(id)

        file.parentFile?.mkdirsOrFail()
        coroutineScope {
            provider(file.writeChannel(coroutineContext = coroutineContext))
        }
    }

    override suspend fun <R> read(id: String, consumer: suspend (ByteReadChannel) -> R): R {
        requireId(id)
        try {
            val file = fileOf(id)

            file.parentFile?.mkdirsOrFail()
            return consumer(file.readChannel())
        } catch (notFound: FileNotFoundException) {
            throw NoSuchElementException("No session data found for id $id")
        }
    }

    override suspend fun invalidate(id: String) {
        requireId(id)
        try {
            val file = fileOf(id)
            file.delete()
            file.parentFile?.deleteParentsWhileEmpty(dir)
        } catch (notFound: FileNotFoundException) {
            throw NoSuchElementException("No session data found for id $id")
        }
    }

    private fun fileOf(id: String) = File(dir, split(id).joinToString(File.separator, postfix = ".dat"))
    private fun split(id: String) = id.windowedSequence(size = 2, step = 2, partialWindows = true)

    private fun requireId(id: String) {
        if (id.isEmpty()) {
            throw IllegalArgumentException("Session id is empty")
        }
        if (id.indexOfAny(listOf("..", "/", "\\", "!", "?", ">", "<", "\u0000")) != -1) {
            throw IllegalArgumentException("Bad session id $id")
        }
    }
}

private fun File.mkdirsOrFail() {
    if (!this.mkdirs() && !this.exists()) {
        throw IOException("Couldn't create directory $this")
    }
    if (!this.isDirectory) {
        throw IOException("Path is not a directory: $this")
    }
}

private tailrec fun File.deleteParentsWhileEmpty(mostTop: File) {
    if (this != mostTop && isDirectory && exists() && list().isNullOrEmpty()) {
        if (!delete() && exists()) {
            throw IOException("Failed to delete dir $this")
        }
        parentFile.deleteParentsWhileEmpty(mostTop)
    }
}

fun getSession(directory: String): List<SessionInfo> {
    val fileTree: FileTreeWalk = File(directory).walk()
    return fileTree
        .filter { it.isFile }
        .filter { it.extension == "dat" }
        .map {
            SessionInfo(
                lastModified = it.lastModified(),
                id = it.path.replace(directory, "").replace("\\", "").replace(".dat", "")
            )
        }
        .toList()
}

data class SessionInfo(var lastModified: Long = 0, var id: String)

