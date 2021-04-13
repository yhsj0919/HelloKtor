package xyz.yhsj.ktor.routes

import io.ktor.routing.*
import org.koin.ktor.ext.inject
import xyz.yhsj.ktor.entity.permission.SysPermission
import xyz.yhsj.ktor.ext.postExt
import xyz.yhsj.ktor.service.PermissionService
import xyz.yhsj.ktor.service.UserService
import xyz.yhsj.ktor.validator.ValidationGroup

fun Route.permissionRoute() {
    val userService by inject<UserService>()
    val permissionService by inject<PermissionService>()
    route("/permission") {

        /**
         * 获取权限
         */
        postExt<SysPermission>("/get") { params, appSession ->
            permissionService.getPermission(params, appSession)
        }
        /**
         * 添加权限
         */
        postExt<SysPermission>("/add", ValidationGroup.Add::class.java) { params, appSession ->
            permissionService.addPermission(params, appSession)
        }
    }
}