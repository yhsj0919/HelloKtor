package xyz.yhsj.ktor.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.dbName
import xyz.yhsj.ktor.entity.RegisterRequest
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.entity.user.SysUser
import xyz.yhsj.ktor.ext.*
import xyz.yhsj.ktor.service.UserService
import xyz.yhsj.ktor.validator.ValidationGroup

fun Route.userRoutes() {
    val userService by inject<UserService>()
    val logger: Logger = LoggerFactory.getLogger("UserController")
    val client: CoroutineClient by inject()
    val dataBase: CoroutineDatabase by lazy {
        client.getDatabase(dbName)
    }

    route("/user") {
        /**
         * 登录
         */
        post<SysUser>("/login") { user ->
            call.success {
                user.validated(ValidationGroup.Login::class.java) {
                    userService.login(it)
                }
            }
        }

        /**
         * 列表
         */
        get("/list") {

            val users = userService.getUsers()
            call.success { users }
        }

        get("/sum") {
            val session = call.session<AppSession>()

            val myDBName = session.name ?: "DB" + (Math.random() * 10).toInt()
            session.name = myDBName
            session.count += 1
            call.setSession(session)

            logger.error(session.name)

            val users = userService.sumBy(session)
            call.success { users }
        }


        post<RegisterRequest>("/register") { request ->
            val user = SysUser(
                userName = request.userName,
                password = request.password
            )

            user.validated() {

            }
            dataBase
                .getCollection<SysUser>()
                .insertOne(user)
            call.success { HttpStatusCode.OK }
        }
    }
}