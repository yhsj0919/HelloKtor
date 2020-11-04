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
    cookie<AppSession>("App_SESSION", appSessionStorage(File(SessionDirectory), timeOut = 10 * 60 * 1000)) {
        cookie.extensions["SameSite"] = "lax"
    }
}


fun Authentication.Configuration.sessionCheck() {
    session<AppSession> {
        challenge {
            call.respond(HttpStatusCode.OK, CommonResp.login())
        }
        validate { session ->
            //这里返回null就会调用challenge
            session
        }
        skipWhen { call ->
            val skipPath = arrayListOf("/user/login", "/user/register")
            call.request.path() in skipPath
        }
    }
}


