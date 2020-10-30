package xyz.yhsj.ktor.service

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import org.litote.kmongo.group
import org.litote.kmongo.sum
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.dbName
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.entity.user.SysPassword
import xyz.yhsj.ktor.entity.user.SysUser
import xyz.yhsj.ktor.ext.getCollection
import java.math.BigDecimal
import java.util.*
import com.mongodb.client.model.Indexes

import com.mongodb.client.model.IndexOptions


class UserService(private val db: CoroutineClient) {
    val userDB by lazy { db.getCollection<SysUser>(dbName) }
    val pwdDB by lazy { db.getCollection<SysPassword>(dbName) }

    suspend fun login(params: SysUser): Any {
        val user = userDB.find(
            SysUser::userName eq params.userName,
            SysUser::deleted eq 0
        ).first() ?: return CommonResp.error(msg = "用户不存在")

        pwdDB.find(
            SysPassword::user eq user.id,
            SysPassword::password eq params.password
        ).first() ?: return CommonResp.error(msg = "密码错误")


        return CommonResp.success(data = user)

    }


    suspend fun getUsers(): Any {
        val user = SysUser(
            userName = "测试" + UUID.randomUUID(),
            password = "123456",
        )
        val userId = userDB
            .insertOne(user)

        val indexOptions = IndexOptions().unique(true)
        userDB.createIndex(Indexes.ascending("userName"), indexOptions)

        if (userId.wasAcknowledged()) {
            pwdDB.insertOne(SysPassword(password = "123456", user = user.id))
        }

        return CommonResp.success()
    }


    suspend fun sumBy(session: AppSession): BigDecimal {
        val userCollection = db.getCollection<SysUser>(session.name!!)
//        val result = userCollection.aggregate<Result>(
//            group(
//                SysUser::userName,
//                Result::count sum SysUser::money,
//            ),
//        )


        return BigDecimal.valueOf(0)
    }


}

data class Result(var count: BigDecimal = BigDecimal.valueOf(0))