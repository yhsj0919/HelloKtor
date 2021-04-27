package xyz.yhsj.ktor.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.features.*
import io.ktor.content.*
import io.ktor.http.content.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.request.*
import xyz.yhsj.ktor.routes.commonRoute
import xyz.yhsj.ktor.routes.companyRoute
import xyz.yhsj.ktor.routes.permissionRoute
import xyz.yhsj.ktor.routes.userRoute
import xyz.yhsj.ktor.status.statusPage

fun Application.configureRouting() {
    //请求头
    install(AutoHeadResponse)

    //404,500,异常处理
    install(StatusPages) {
        statusPage()
    }

    routing {
        //这个是带权限验证的，可以校验不同的权限
        authenticate("admin") {
            permissionRoute()
            companyRoute()
        }
        authenticate("basic") {
            userRoute()
        }
        //下面的不含权限验证
        commonRoute()
    }
}
