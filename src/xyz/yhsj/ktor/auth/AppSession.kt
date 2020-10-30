package xyz.yhsj.ktor.auth

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.sessions.*
import xyz.yhsj.ktor.entity.resp.CommonResp
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


data class AppSession(var count: Int = 0, var name: String? = null) : Principal


fun Authentication.Configuration.sessionCheck() {
    session<AppSession> {
        challenge {
            call.respond(HttpStatusCode.OK, CommonResp.login())
        }
        validate { session ->
            //这里返回null就会调用challenge
            println(">>>>>>>>>>>${session.name}")
            session
        }
        skipWhen { call ->
            val skipPath = arrayListOf("/user/login","/user/list")
            call.request.path() in skipPath
        }
    }

}

