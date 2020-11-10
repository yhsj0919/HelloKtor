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
import xyz.yhsj.ktor.ext.fromJson
import xyz.yhsj.ktor.ext.json
import xyz.yhsj.ktor.ext.sessionOrNull
import java.io.File
import java.util.*


const val SessionDirectory = ".sessions"

class AppSession(var user: String? = null, var time: Long = Date().time) : Principal {
    fun setUser(user: SysUser?): AppSession {
        this.user = user.json()
        return this
    }

    fun getUser(): SysUser? {
        return if (this.user.isNullOrEmpty()) {
            null
        } else {
            fromJson(this.user!!)
        }
    }
}

@InternalAPI
fun Sessions.Configuration.setSession() {
    //这里开启了一个携程，尝试删除过期的session文件
    //替换掉了原有的directorySessionStorage
    cookie<AppSession>("App_SESSION", appSessionStorage(File(SessionDirectory), timeOut = 60 * 60 * 1000)) {
        cookie.extensions["SameSite"] = "lax"
    }
}


fun Authentication.Configuration.sessionCheck() {
    session<AppSession>(name = "admin") {
        challenge {
            val session = call.sessionOrNull<AppSession>()
            if (session != null && session.getUser()?.companyId == null) {
                call.respond(HttpStatusCode.OK, CommonResp.error(msg = "你可能走错地方了~"))
            } else {
                call.respond(HttpStatusCode.OK, CommonResp.login())
            }
        }
        validate { session ->
            if (session.getUser()?.type != -1) {
                null
            } else {
                //这里返回null就会调用challenge
                session
            }
        }
    }

    session<AppSession>(name = "basic") {
        challenge {
            val session = call.sessionOrNull<AppSession>()
            if (session != null && session.getUser()?.companyId == null) {
                call.respond(HttpStatusCode.OK, CommonResp.error(msg = "换个普通账号来吧"))
            } else {
                call.respond(HttpStatusCode.OK, CommonResp.login())
            }
        }
        validate { session ->
            return@validate if (session.getUser()?.companyId == null) {
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


