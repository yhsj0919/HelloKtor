package xyz.yhsj.ktor.ext

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.sessions.*
import org.litote.kmongo.coroutine.CoroutineClient
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.validator.ValidationUtils


fun Any?.json(): String =
    if (this != null) {
        Gson().toJson(this)
    } else {
        ""
    }

fun ApplicationCall.sessionId(key: String = "App_SESSION"): String? {
    return this.request.cookies[key]
}

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


suspend fun ApplicationCall.success(data: Any? = null) {
    if (data == null) {
        this.respond(HttpStatusCode.OK, CommonResp.empty())
    } else {
        this.respond(HttpStatusCode.OK, CommonResp.success(data = data))
    }
}

inline fun <reified T : Any> CoroutineClient.getCollection(dbName: String) = this.getDatabase(dbName).getCollection<T>()


fun Any.validated(vararg groups: Class<*>, block: () -> Any): Any? {
    val result = ValidationUtils.validateEntity(this, *groups)

    return if (result.hasErrors) {
        result.errorMsg?.values?.first()
    } else {
        block()
    }
}
