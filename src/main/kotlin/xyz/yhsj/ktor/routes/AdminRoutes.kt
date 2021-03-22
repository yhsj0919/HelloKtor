package xyz.yhsj.ktor.routes

import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.entity.permission.SysPermission
import xyz.yhsj.ktor.entity.user.SysUser
import xyz.yhsj.ktor.ext.postExt
import xyz.yhsj.ktor.ext.setSession
import xyz.yhsj.ktor.service.AdminService
import xyz.yhsj.ktor.service.PermissionService
import xyz.yhsj.ktor.validator.ValidationGroup

fun Route.adminRoutes() {
    val adminService by inject<AdminService>()
    val permissionService by inject<PermissionService>()
    route("/admin") {
        /**
         * 登录
         */
        postExt<SysUser>("/login", ValidationGroup.Login::class.java) { user, _ ->
            val rasp = adminService.login(user)
            if (rasp.isSuccess()) {
                call.setSession(AppSession(user = (rasp.data as SysUser?)))
            }
            rasp
        }
        /**
         * 获取权限
         */
        postExt<SysPermission>("/getPermission") { params, appSession ->
            permissionService.getPermission(params, appSession)
        }
        /**
         * 添加权限
         */
        postExt<SysPermission>("/addPermission", ValidationGroup.Add::class.java) { params, appSession ->
            permissionService.addPermission(params, appSession)
        }
    }
}