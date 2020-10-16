package xyz.yhsj.ktor.service

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.litote.kmongo.coroutine.CoroutineClient
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.entity.User
import xyz.yhsj.ktor.ext.getCollection

class UserService : KoinComponent {
    val db: CoroutineClient by inject()

    suspend fun getUsers(session: AppSession): List<User> {
        val userCollection = db.getCollection<User>(session.name!!)

        val user = User(
            userName = "request.userName",
            password = "request.password"
        )
        userCollection
            .insertOne(user)

        return userCollection
            .find()
            .toList()
    }


}