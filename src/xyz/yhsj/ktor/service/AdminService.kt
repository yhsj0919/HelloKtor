package xyz.yhsj.ktor.service

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.dbName
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.entity.user.SysPassword
import xyz.yhsj.ktor.entity.user.SysUser
import xyz.yhsj.ktor.ext.getCollection
import java.math.BigDecimal


class AdminService(private val db: CoroutineClient) {
    private val userDB by lazy { db.getCollection<SysUser>(dbName) }
    private val pwdDB by lazy { db.getCollection<SysPassword>(dbName) }

    /**
     * 登录
     */
    suspend fun login(params: SysUser): CommonResp {

        val count = userDB.countDocuments()
        if (count == 0L) {
            userDB.createIndex(Indexes.ascending("userName"), IndexOptions().unique(true))
            val newUser = SysUser(userName = "18612345678", nickName = "超级管理员", type = -1)
            userDB.insertOne(newUser)
            pwdDB.insertOne(SysPassword(user = newUser.id, password = "admin#@."))
            return CommonResp.error(msg = "已创建默认账号，请重新登录")

        } else {
            val user = userDB.find(
                SysUser::userName eq params.userName,
                SysUser::deleted eq 0
            ).first() ?: return CommonResp.error(msg = "用户不存在")

            pwdDB.find(
                SysPassword::user eq user.id,
                SysPassword::password eq params.passWord
            ).first() ?: return CommonResp.error(msg = "密码错误")

            return CommonResp.success(data = user)
        }

    }


}

