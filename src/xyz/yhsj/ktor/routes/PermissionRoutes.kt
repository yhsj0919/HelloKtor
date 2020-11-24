package xyz.yhsj.ktor.routes

import io.ktor.routing.*
import io.lettuce.core.SetArgs
import io.lettuce.core.TimeoutOptions
import io.lettuce.core.codec.StringCodec
import org.koin.ktor.ext.inject
import xyz.yhsj.ktor.entity.company.SysCompany
import xyz.yhsj.ktor.entity.permission.SysPermission
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.ext.postExt
import xyz.yhsj.ktor.redis.Redis
import xyz.yhsj.ktor.service.CompanyService
import xyz.yhsj.ktor.service.PermissionService
import xyz.yhsj.ktor.validator.ValidationGroup

/**
 * 公司
 */
fun Route.permissionRoutes() {
    val service by inject<PermissionService>()

    route("/permission") {
        //获取
        postExt<SysPermission>("/getPermission") { params, appSession ->
            service.getPermission(params, appSession)
        }
        //添加
        postExt<SysPermission>("/addPermission", ValidationGroup.Add::class.java) { params, appSession ->
            service.addPermission(params, appSession)
        }

    }


}