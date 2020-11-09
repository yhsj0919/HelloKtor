package xyz.yhsj.ktor.routes

import io.ktor.routing.*
import org.koin.ktor.ext.inject
import xyz.yhsj.ktor.entity.company.SysCompany
import xyz.yhsj.ktor.ext.postExt
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


    }


}