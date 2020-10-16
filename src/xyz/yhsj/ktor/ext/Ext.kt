package xyz.yhsj.ktor.ext

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.sessions.*
import org.litote.kmongo.coroutine.CoroutineClient
import xyz.yhsj.ktor.entity.RespResult
import kotlin.reflect.full.createInstance

inline fun <reified T : Any> ApplicationCall.session(): T {
    return this.sessions.get<T>() ?: new()
}

inline fun <reified T : Any> ApplicationCall.setSession(value: T) {
    return this.sessions.set(value)
}

inline fun <reified T> ApplicationCall.sessionOrNull(): T? {
    return this.sessions.get<T>()
}

inline fun <reified T : Any> new(vararg params: Any) =
    T::class.java.getDeclaredConstructor(*params.map { it::class.java }.toTypedArray()).apply { isAccessible = true }
        .newInstance(*params)

//inline fun <reified T : Any> new(vararg params: Any): T {
//    val clz = T::class
//    return clz.createInstance()
//}


suspend fun ApplicationCall.success(message: Any? = null) {
    if (message == null) {
        this.respond(HttpStatusCode.OK)
    } else {
        this.respond(HttpStatusCode.OK, RespResult.success(message))
    }
}

inline fun <reified T : Any> CoroutineClient.getCollection(dbName: String) = this.getDatabase(dbName).getCollection<T>()



