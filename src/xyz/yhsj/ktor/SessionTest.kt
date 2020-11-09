package xyz.yhsj.ktor

import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.utils.io.*
import xyz.yhsj.ktor.auth.AppSession
import java.io.File


@KtorExperimentalAPI
suspend fun main() {
    val serializer by lazy { defaultSessionSerializer<AppSession>() }

    val directory = ".sessions"

    val listId = getSession(directory)

    listId.map {
        directorySessionStorage(File(directory)).read(it) { channel ->
            val text = channel.readUTF8Line()
                ?: throw IllegalStateException("Failed to read stored session from $channel")
            serializer.deserialize(text)
        }
    }.forEach {


        println(it.getUser()?.userName)
    }

//    try {
//        directorySessionStorage(File(directory)).invalidate(listId.first())
//    } catch (e: Exception) {
//        println("删除失败:${e.message}")
//    }
    
}

fun getSession(directory: String): List<String> {
    val fileTree: FileTreeWalk = File(directory).walk()
    return fileTree
        .filter { it.isFile }
        .filter { it.extension == "dat" }
        .map {
            println(it.lastModified())
            it.path.replace(directory, "").replace("\\", "").replace(".dat", "")
        }
        .toList()
//        .forEach {
//            println(it.name)
//            println()
//        }
}
