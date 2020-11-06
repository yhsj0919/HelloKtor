package xyz.yhsj.ktor.service

import org.litote.kmongo.coroutine.CoroutineClient
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.dbName
import xyz.yhsj.ktor.entity.company.SysCompany
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.entity.resp.PageUtil
import xyz.yhsj.ktor.ext.getCollection

/**
 * 公司
 */
class CompanyService(private val db: CoroutineClient) {
    val companyDB by lazy { db.getCollection<SysCompany>(dbName) }

    /**
     * 获取公司
     */
    suspend fun getCompany(params: SysCompany, sessions: AppSession): Any {
        val count = companyDB.countDocuments()
        val datas = companyDB.find().limit(params.size).skip(params.page * params.size).toList()

        val data = PageUtil(page = params.page, size = params.size, totalElements = count, content = datas)

        return CommonResp.success(data = data)
    }
}