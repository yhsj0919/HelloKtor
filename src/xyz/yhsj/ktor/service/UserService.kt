package xyz.yhsj.ktor.service

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.group
import org.litote.kmongo.sum
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.entity.User
import xyz.yhsj.ktor.ext.getCollection
import java.math.BigDecimal

class UserService(private val db: CoroutineClient) {

    suspend fun getUsers(session: AppSession): List<User> {
        val userCollection = db.getCollection<User>(session.name!!)

        val user = (0..5).map {
            User(
                userName = "request.userName",
                password = "request.password",
                money = BigDecimal.valueOf((Math.random() * 10000).toInt() / 100.0)
            )
        }
        userCollection
            .insertMany(user)

        return userCollection
            .find()
            .toList()
    }


    suspend fun sumBy(session: AppSession): BigDecimal {
        val userCollection = db.getCollection<User>(session.name!!)
        val result = userCollection.aggregate<Result>(
            group(
                User::userName,
                Result::count sum User::money,
            ),
        )


        return result.first()?.count ?: BigDecimal.valueOf(0)
    }


}

data class Result(var count: BigDecimal = BigDecimal.valueOf(0))