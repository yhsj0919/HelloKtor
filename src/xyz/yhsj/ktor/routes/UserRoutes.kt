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
import xyz.yhsj.ktor.entity.User
import xyz.yhsj.ktor.ext.session
import xyz.yhsj.ktor.ext.setSession
import xyz.yhsj.ktor.ext.success
import xyz.yhsj.ktor.service.UserService

fun Route.userRoutes() {
    val userService by inject<UserService>()
    val logger: Logger = LoggerFactory.getLogger("UserController")
    val client: CoroutineClient by inject()
    val dataBase: CoroutineDatabase by lazy {
        client.getDatabase(dbName)
    }
    route("/users") {
        get { call.success(mapOf("name" to "users")) }
        get("/list") {
            val session = call.session<AppSession>()

            val myDBName = session.name ?: "DB" + (Math.random() * 10).toInt()
            session.name = myDBName
            session.count += 1
            call.setSession(session)

            logger.error(session.name)

            val users = userService.getUsers(session)
            call.success(users)
        }

        post<RegisterRequest>("/register") { request ->
            val user = User(
                userName = request.userName,
                password = request.password
            )
            dataBase
                .getCollection<User>()
                .insertOne(user)
            call.success(HttpStatusCode.OK)
        }
    }
}