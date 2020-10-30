package xyz.yhsj.ktor

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.sessions.*
import org.koin.ktor.ext.Koin
import org.litote.kmongo.Id
import org.slf4j.event.Level
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.auth.sessionCheck
import xyz.yhsj.ktor.ext.IdSerializer
import xyz.yhsj.ktor.koin.koinModule
import xyz.yhsj.ktor.routes.commonRoutes
import xyz.yhsj.ktor.routes.userRoutes
import xyz.yhsj.ktor.status.statusPage
import java.io.File
import java.lang.reflect.Modifier
import java.text.DateFormat


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

//    if (!testing) {
//        install(HttpsRedirect) {
//            // The port to redirect to. By default 443, the default HTTPS port.
//            sslPort = 443
//            // 301 Moved Permanently, or 302 Found redirect.
//            permanentRedirect = true
//        }
//    }

    //Cookie支持
    install(Sessions) {
        cookie<AppSession>("App_SESSION", directorySessionStorage(File(".sessions"))) {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    //请求头
    install(AutoHeadResponse)
    //日志
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    //跨域
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost()
    }
    //关机地址
    install(ShutDownUrl.ApplicationCallFeature) {
        // The URL that will be intercepted (you can also use the application.conf's ktor.deployment.shutdown.url key)
        shutDownUrl = "/ktor/application/shutdown"
        // A function that will be executed to get the exit code of the process
        exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
    }

    //序列化
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
            //序列化空值
//            serializeNulls()
            //忽略修饰符，这里仅忽略static类型的
            excludeFieldsWithModifiers(Modifier.STATIC)
            //序列化Id
            registerTypeHierarchyAdapter(Id::class.java, IdSerializer())
        }
    }
    //模块依赖注入
    install(Koin) {
        modules(koinModule)
    }

    //404,500,异常处理
    install(StatusPages) {
        statusPage()
    }
    //session校验
    install(Authentication) {
        sessionCheck()
    }
    //拦截器
//    intercept(ApplicationCallPipeline.Call) {
//        val session = call.sessions.get<MySession>() ?: MySession()
//        println(">>>>>>>>>>>>>>>>>>>>" + session.name)
//    }


    routing {
        //这个是带权限验证的
        authenticate {
            userRoutes()
        }
        //下面的不含权限验证
        commonRoutes()
    }
}





