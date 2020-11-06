package xyz.yhsj.ktor.routes

import io.ktor.routing.*
import org.koin.ktor.ext.inject
import xyz.yhsj.ktor.entity.company.SysCompany
import xyz.yhsj.ktor.ext.postExt
import xyz.yhsj.ktor.service.CompanyService

/**
 * 公司
 */
fun Route.companyRoutes() {
    val companyService by inject<CompanyService>()

    route("/company") {
        postExt<SysCompany>("/getCompany") { sysCompany, appSession ->
            companyService.getCompany(sysCompany, appSession)
        }


    }


}