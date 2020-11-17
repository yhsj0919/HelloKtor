package xyz.yhsj.ktor.routes

import io.ktor.routing.*
import io.lettuce.core.SetArgs
import io.lettuce.core.TimeoutOptions
import io.lettuce.core.codec.StringCodec
import org.koin.ktor.ext.inject
import xyz.yhsj.ktor.entity.company.SysCompany
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.ext.postExt
import xyz.yhsj.ktor.redis.Redis
import xyz.yhsj.ktor.service.CompanyService
import xyz.yhsj.ktor.validator.ValidationGroup

/**
 * 公司
 */
fun Route.companyRoutes() {
    val companyService by inject<CompanyService>()

    route("/company") {
        //获取
        postExt<SysCompany>("/getCompany") { sysCompany, appSession ->
            companyService.getCompany(sysCompany, appSession)
        }
        //添加
        postExt<SysCompany>("/addCompany", ValidationGroup.Add::class.java) { sysCompany, appSession ->
            companyService.addCompany(sysCompany, appSession)
        }

        postExt("/redis") {
            val client = Redis.newClient(StringCodec.UTF8)
            println(client.set("testKey", "测试数据", SetArgs.Builder.ex(10)))
            println(client.get("testKey"))

            client.keys("session_*").forEach {
                println(">>>>>>>>>>:$it")
            }
            CommonResp.success()
        }

    }


}