package xyz.yhsj.ktor.routes

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.ext.session

fun Route.commonRoutes() {
    get("/") {
        call.respondRedirect("index.html")
//        call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
    }

    // Static feature. Try to access `/static/ktor_logo.svg`
    static("/") {
        resources("static")
    }

    get("/session/increment") {
        val session = call.session<AppSession>()
//        call.setSession(session.copy(count = session.count + 1))
        call.respondText("Counter is ${session.count}. Refresh to increment.")
    }

    get("/json/gson") {
        call.respond(mapOf("hello" to "world"))
    }
}