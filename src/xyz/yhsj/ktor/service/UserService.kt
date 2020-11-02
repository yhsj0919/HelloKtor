package xyz.yhsj.ktor.service

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.commitTransactionAndAwait
import org.litote.kmongo.eq
import org.litote.kmongo.newId
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.dbName
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.entity.user.SysPassword
import xyz.yhsj.ktor.entity.user.SysUser
import xyz.yhsj.ktor.ext.getCollection
import java.math.BigDecimal
import java.util.*


class UserService(private val db: CoroutineClient) {
    val userDB by lazy { db.getCollection<SysUser>(dbName) }
    val pwdDB by lazy { db.getCollection<SysPassword>(dbName) }

    /**
     * 登录
     */
    suspend fun login(params: SysUser): CommonResp {
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

    /**
     * 注册
     */
    suspend fun register(params: SysUser): Any {
        val user = params.copy(id = newId(), deleted = 0, type = 0)
        userDB.createIndex(Indexes.ascending("userName"), IndexOptions().unique(true))
        userDB.insertOne(user)
        pwdDB.insertOne(SysPassword(user = user.id, password = user.password))
        return CommonResp.success()
    }


    suspend fun getUsers(): Any {

        val users = userDB.find().toList()

        return CommonResp.success(data = users)
    }


    suspend fun sumBy(session: AppSession): BigDecimal {
//        val userCollection = db.getCollection<SysUser>(session.name!!)
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