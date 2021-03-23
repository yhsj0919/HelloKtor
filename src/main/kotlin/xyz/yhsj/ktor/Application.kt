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
import xyz.yhsj.ktor.auth.sessionCheck
import xyz.yhsj.ktor.auth.setSession
import xyz.yhsj.ktor.ext.IdSerializer
import xyz.yhsj.ktor.koin.koinModule
import xyz.yhsj.ktor.redis.Redis
import xyz.yhsj.ktor.routes.*
import xyz.yhsj.ktor.status.statusPage
import java.lang.reflect.Modifier
import java.text.DateFormat

//配置文件初始化
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

//代码初始化
//fun main(args: Array<String>) {
//    val env = applicationEngineEnvironment {
//        module {
//            module()
//        }
//这里可以配置监听多个端口，然后根据端口请求不同的接口
//        // Private API
//        connector {
//            host = "127.0.0.1"
//            port = 9090
//        }
//        // Public API
//        connector {
//            host = "0.0.0.0"
//            port = 8080
//        }
//
//        sslConnector(keyStore = keyStore, keyAlias = "mykey", keyStorePassword = { "changeit".toCharArray() }, privateKeyPassword = { "changeit".toCharArray() }) {
//            port = 9091
//            keyStorePath = keyStoreFile.absoluteFile
//        }
//
//    }
//    embeddedServer(Netty, env).apply {
//        start(wait = true)
//    }
//}


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
    install(Redis) {
        // configruation for redis to connect
        url = "redis://127.0.0.1:6379/0?timeout=15s"
    }

    //Cookie支持
    install(Sessions) {
        setSession()
    }

    //请求头
    install(AutoHeadResponse)
    //日志
    install(CallLogging) {
        level = Level.DEBUG
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
        allowNonSimpleContentTypes=true
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
    intercept(ApplicationCallPipeline.Call) {
        println("host:" + call.request.host() + ":" + call.request.port() + call.request.path())
        //可以根据随机数拦截每一次请求，防止该cookie跳跃请求
//        println(">>>>>>请求随机数>>>>>>>" + call.request.cookies.rawCookies["random"])
//        val random = (Math.random() * 100000).toInt()
//        call.response.cookies.append("random", random.toString())
//        println(">>>>>>>>返回随机数>>>>>>>>>>>>$random")
    }


    routing {
        //这个是带权限验证的，可以校验不同的权限
        authenticate("admin") {
            adminRoute()
            companyRoute()
        }
        authenticate("basic") {
            userRoute()
        }
        //下面的不含权限验证
        commonRoute()
    }


}





