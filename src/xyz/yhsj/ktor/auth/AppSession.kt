package xyz.yhsj.ktor.auth

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.sessions.*
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.entity.user.SysUser
import xyz.yhsj.ktor.ext.fromJson
import xyz.yhsj.ktor.ext.json
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*


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


fun Authentication.Configuration.sessionCheck() {
    session<AppSession> {
        challenge {
            call.respond(HttpStatusCode.OK, CommonResp.login())
        }
        validate { session ->
            //这里返回null就会调用challenge
            println(">>>>>>>>>>>${session.getUser()?.userName}")
            session
        }
        skipWhen { call ->
            val skipPath = arrayListOf("/user/login", "/user/register")
            call.request.path() in skipPath
        }
    }

}

