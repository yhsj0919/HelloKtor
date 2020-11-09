package xyz.yhsj.ktor.routes

import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.entity.user.SysUser
import xyz.yhsj.ktor.ext.*
import xyz.yhsj.ktor.service.UserService
import xyz.yhsj.ktor.validator.ValidationGroup

fun Route.userRoutes() {
    val userService by inject<UserService>()
//    val logger: Logger = LoggerFactory.getLogger("userRoutes")
    /**
     * 登录
     */
    postExt<SysUser>("/login", ValidationGroup.Login::class.java) { user, _ ->
        val rasp = userService.login(user)
        if (rasp.isSuccess()) {
            call.setSession(AppSession(user = (rasp.data as SysUser?)?.json()))
        }
        rasp
    }

    route("/user") {

        /**
         * 注册
         */
        postExt<SysUser>("/register", ValidationGroup.Add::class.java) { user, session ->
            userService.register(user,session)
        }

        /**
         * 列表
         */
        postExt("/list") { session ->
            userService.getUsers()
        }

        postExt("/sum") {
            userService.sumBy(it)
        }


    }
}