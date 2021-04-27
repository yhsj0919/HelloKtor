package xyz.yhsj.ktor.plugins

import io.ktor.http.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureHTTP() {


    //跨域
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()
    }

//    install(HttpsRedirect) {
//        // The port to redirect to. By default 443, the default HTTPS port.
//        sslPort = 443
//        // 301 Moved Permanently, or 302 Found redirect.
//        permanentRedirect = true
//    }


}
