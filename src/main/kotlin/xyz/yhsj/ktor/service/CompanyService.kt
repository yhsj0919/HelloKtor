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
import xyz.yhsj.ktor.ext.getCollection
import kotlin.reflect.full.memberProperties

/**
 * 公司
 */
class CompanyService(private val db: CoroutineClient) {
    private val companyDB by lazy { db.getCollection<SysCompany>(dbName) }

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
}