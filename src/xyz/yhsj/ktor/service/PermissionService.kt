package xyz.yhsj.ktor.service

import org.litote.kmongo.coroutine.CoroutineClient
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.dbName
import xyz.yhsj.ktor.entity.permission.SysPermission
import xyz.yhsj.ktor.entity.resp.CommonResp
import xyz.yhsj.ktor.ext.getCollection

/**
 * 权限，菜单
 */
class PermissionService(private val db: CoroutineClient) {
    private val permission by lazy { db.getCollection<SysPermission>(dbName) }

    /**
     * 获取权限
     */
    suspend fun getPermission(params: SysPermission, sessions: AppSession): Any {

        val result = permission.find().toList()

//        result.filter { it.parent == null }.map {=> it.child=result.filter { it.parent==it } }


        return CommonResp.success(data = result)
    }

    /**
     * 添加权限
     */
    suspend fun addPermission(params: SysPermission, sessions: AppSession): Any {

        return CommonResp.success()
    }
}