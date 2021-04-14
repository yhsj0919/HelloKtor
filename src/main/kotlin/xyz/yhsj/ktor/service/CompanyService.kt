package xyz.yhsj.ktor.service

import com.mongodb.client.model.UnwindOptions
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.aggregate
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.dbName
import xyz.yhsj.ktor.entity.company.SysCompany
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.entity.resp.PageUtil
import xyz.yhsj.ktor.entity.user.SysPassword
import xyz.yhsj.ktor.entity.user.SysUser
import xyz.yhsj.ktor.ext.getCollection
import kotlin.reflect.full.memberProperties

/**
 * 公司
 */
class CompanyService(private val db: CoroutineClient) {
    private val companyDB by lazy { db.getCollection<SysCompany>(dbName) }
    private val userDB by lazy { db.getCollection<SysUser>(dbName) }
    private val passWordDB by lazy { db.getCollection<SysPassword>(dbName) }

    /**
     * 获取公司
     */
    suspend fun getCompany(params: SysCompany, sessions: AppSession): Any {
        val page = params.page ?: 0
        val size = params.size ?: 10
        val count = companyDB.countDocuments()
        val result = companyDB.aggregate<SysCompany>(
            skip(page * size),
            limit(size),
            lookup(from = "sysUser", localField = "creatorId", foreignField = "_id", newAs = "users"),
            unwind("\$users", UnwindOptions().preserveNullAndEmptyArrays(true)),
            project(
                SysCompany::creator from "\$users",
                *SysCompany::class.memberProperties
                    .filter { it != SysCompany::creator }
                    .map {
                        it from it
                    }.toTypedArray()
            ),
        ).toList()

//        val result = companyDB.find().skip(page * size).limit(size).toList()

        val data = PageUtil(page = page, size = size, totalElements = count, content = result)

        return CommonResp.success(data = data)
    }

    /**
     * 添加公司
     */
    suspend fun addCompany(params: SysCompany, sessions: AppSession): Any {
        val company = params.copy(status = 0)
        company.deleted = 0
        company.creatorId = sessions.user?.id
        company.creator = null
        company.company = null
        companyDB.insertOne(company)
        return CommonResp.success(data = company)
    }

    /**
     * 获取管理员
     */
    suspend fun getCompanyAdmin(params: SysCompany, sessions: AppSession): Any {
        val admin = userDB.findOne(SysUser::companyId.eq(params.id), SysUser::type.eq(-1), SysUser::deleted.eq(0))
        if (admin != null) {
            val psd = passWordDB.findOne(SysPassword::user.eq(admin.id))
            admin.passWord = psd?.password
        }
        return CommonResp.success(data = admin)
    }

    /**
     * 设置管理员
     */
    suspend fun setCompanyAdmin(params: SysUser, sessions: AppSession): Any {

        if (params.companyId == null) {
            return CommonResp.error(msg = "公司Id不可为空")
        }
        val oldData = userDB.findOne(SysUser::userName.eq(params.userName), SysUser::deleted.eq(0))

        if (oldData != null && oldData.companyId != params.companyId) {
            return CommonResp.error(msg = "该用户已存在")
        }

        val admin = SysUser(id = oldData?.id, userName = params.userName, nickName = "超级管理员", type = -1)

        admin.companyId = params.companyId
        admin.passWord = params.passWord
        admin.creatorId = sessions.user?.id

        userDB.save(admin)

        passWordDB.deleteMany(SysPassword::user.eq(admin.id))
        passWordDB.save(SysPassword(user = admin.id, password = params.passWord))


        return CommonResp.success(data = admin)
    }
}