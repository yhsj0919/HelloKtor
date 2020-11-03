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

    route("/user") {
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
        /**
         * 注册
         */
        postExt<SysUser>("/register", ValidationGroup.Add::class.java) { user, _ ->
            userService.register(user)
        }

        post<SysUser>("/register") { user ->
            call.success {
                user.validated(ValidationGroup.Add::class.java) {
                    userService.register(user)
                }
            }
        }


        /**
         * 列表
         */
        postExt("/list") { session ->
            println(session.getUser()?.userName)
            println(session.time)
            userService.getUsers()
        }

        postExt("/sum") {
            userService.sumBy(it)
        }


    }
}