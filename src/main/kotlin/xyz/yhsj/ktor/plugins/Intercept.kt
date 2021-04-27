package xyz.yhsj.ktor.plugins

import io.ktor.application.*
import io.ktor.request.*

/**
 * 拦截器
 */
fun Application.configureIntercept() {
    //拦截器
    intercept(ApplicationCallPipeline.Call) {
        println("host:" + call.request.host() + ":" + call.request.port() + call.request.path())
        //可以根据随机数拦截每一次请求，防止该cookie跳跃请求
//        println(">>>>>>请求随机数>>>>>>>" + call.request.cookies.rawCookies["random"])
//        val random = (Math.random() * 100000).toInt()
//        call.response.cookies.append("random", random.toString())
//        println(">>>>>>>>返回随机数>>>>>>>>>>>>$random")
    }
}

