package xyz.yhsj.ktor.status

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

fun StatusPages.Configuration.statusPage() {
    status(HttpStatusCode.NotFound) {
        call.respond(HttpStatusCode.OK, mapOf("msg" to "404"))
    }
    status(HttpStatusCode.InternalServerError) {
        call.respond(HttpStatusCode.OK, mapOf("msg" to "500"))
    }
    exception<Throwable> {
        println("出现错误" + it.message)
        call.respond(HttpStatusCode.OK, mapOf("msg" to "error :${it.message}"))
    }
}