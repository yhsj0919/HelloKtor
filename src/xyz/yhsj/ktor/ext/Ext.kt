package xyz.yhsj.ktor.ext

import com.google.gson.GsonBuilder
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.pipeline.*
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineClient
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.validator.ValidationUtils
import java.lang.reflect.Modifier
import java.text.DateFormat

val gson = GsonBuilder().setDateFormat(DateFormat.LONG).setPrettyPrinting().excludeFieldsWithModifiers(Modifier.STATIC)
    .registerTypeHierarchyAdapter(Id::class.java, IdSerializer()).create()

//json序列化
fun Any?.json(): String =
    if (this != null) {
        gson.toJson(this)
    } else {
        ""
    }

//json反序列化
inline fun <reified T> fromJson(json: String) = gson.fromJson(json, T::class.java)

/**
 * 获取sessionId
 */
fun ApplicationCall.sessionId(key: String = "App_SESSION"): String? {
    return this.request.cookies[key]
}

/**
 * 获取session
 */
inline fun <reified T : Any> ApplicationCall.session(): T {
    return this.sessions.get<T>() ?: new()
}

/**
 * 获取session
 */
inline fun <reified T : Any> ApplicationCall.setSession(value: T) {
    return this.sessions.set(value)
}

/**
 * 获取session
 */
inline fun <reified T> ApplicationCall.sessionOrNull(): T? {
    return this.sessions.get<T>()
}

/**
 * 根据泛型新建对象
 */
inline fun <reified T : Any> new(vararg params: Any) =
    T::class.java.getDeclaredConstructor(*params.map { it::class.java }.toTypedArray()).apply { isAccessible = true }
        .newInstance(*params)

//inline fun <reified T : Any> new(vararg params: Any): T {
//    val clz = T::class
//    return clz.createInstance()
//}

/**
 * 成功扩展
 */
suspend fun ApplicationCall.success(block: suspend () -> Any?) {
    val data = block()
    if (data == null) {
        this.respond(HttpStatusCode.OK, CommonResp.empty())
    } else {
        this.respond(HttpStatusCode.OK, data)
    }
}

/**
 * 获取数据库，数据表
 */
inline fun <reified T : Any> CoroutineClient.getCollection(dbName: String) = this.getDatabase(dbName).getCollection<T>()

/**
 * 数据校验
 */
suspend fun <T : Any> T.validated(vararg groups: Class<*>, block: suspend (data: T) -> Any): Any? {
    val result = ValidationUtils.validateEntity(this, *groups)
    return if (result.hasErrors) {
        CommonResp.error(msg = result.errorMsg?.values?.first() ?: "未知参数错误")
    } else {
        block(this)
    }
}

/**
 * post扩展
 * @validatedGroups 校验的数组，不传则不校验
 */
@ContextDsl
@JvmName("postTyped")
inline fun <reified R : Any> Route.postExt(
    path: String,
    vararg validatedGroups: Class<*>,
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R, session: AppSession) -> Any
): Route {
    return route(path, HttpMethod.Post) {
        handle {
            //这里初始化一个空参数，
            val data: R = call.receiveOrNull() ?: new()
            call.success {
                data.validated(*validatedGroups) {
                    //这里session不为空，前面有校验，需要session的校验通过才会来到这里，不需要session的不关注这个属性
                    body(data, call.session())
                }
            }
        }
    }
}

/**
 * post空参数扩展
 */
@ContextDsl
@JvmName("postTyped")
inline fun Route.postExt(
    path: String,
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(session: AppSession) -> Any
): Route {
    return route(path, HttpMethod.Post) {
        handle {
            call.success {
                //这里session不为空，前面有校验，需要session的校验通过才会来到这里，不需要session的不关注这个属性
                body(call.session())
            }
        }
    }
}