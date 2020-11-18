package xyz.yhsj.ktor.auth

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.sessions.*
import io.ktor.util.*
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.entity.user.SysUser
import xyz.yhsj.ktor.ext.sessionOrNull
import java.util.*


class AppSession(var user: SysUser? = null, var time: Long = Date().time) : Principal

fun Sessions.Configuration.setSession() {
    //这里使用了redis管理session
    //替换掉了原有的directorySessionStorage
    cookie<AppSession>("App_SESSION", RedisSessionStorage(timeOut = 24 * 60 * 60)) {
        cookie.extensions["SameSite"] = "lax"
        //使用Gson 序列化session
        serializer = GsonSessionSerializer(type)
    }
}

/**
 * session校验
 * 下面两种校验方式只是为了验证功能
 */
fun Authentication.Configuration.sessionCheck() {
    //admin校验
    session<AppSession>(name = "admin") {
        challenge {
            val session = call.sessionOrNull<AppSession>()
            if (session != null && session.user?.companyId == null) {
                call.respond(HttpStatusCode.OK, CommonResp.error(msg = "你可能走错地方了~"))
            } else {
                call.respond(HttpStatusCode.OK, CommonResp.login())
            }
        }
        validate { session ->
            if (session.user?.type != -1) {
                null
            } else {
                //这里返回null就会调用challenge
                session
            }
        }
    }
    //基础校验
    session<AppSession>(name = "basic") {
        challenge {
            val session = call.sessionOrNull<AppSession>()
            if (session != null && session.user?.companyId == null) {
                call.respond(HttpStatusCode.OK, CommonResp.error(msg = "换个普通账号来吧"))
            } else {
                call.respond(HttpStatusCode.OK, CommonResp.login())
            }
        }
        validate { session ->
            return@validate if (session.user?.companyId == null) {
                null
            } else {
                //这里返回null就会调用challenge
                session
            }
        }
        skipWhen { call ->
            val skipPath = arrayListOf("/login")
            call.request.path() in skipPath
        }
    }

}


