package xyz.yhsj.ktor.service

import org.litote.kmongo.Id
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
    private val permissionDB by lazy { db.getCollection<SysPermission>(dbName) }

    /**
     * 获取权限
     */
    suspend fun getPermission(params: SysPermission, sessions: AppSession): Any {

        val result = permissionDB.find().toList()

        val tree = getTree(result, null)
        return CommonResp.success(data = tree)
    }

    private fun getTree(list: List<SysPermission>, parent: Id<SysPermission>?): List<SysPermission> {
        return list.filter { it.parent == parent }.map { item -> item.child = getTree(list, item.id);item }.toList()
    }

    /**
     * 添加权限
     */
    suspend fun addPermission(params: SysPermission, sessions: AppSession): Any {

        val data = params.copy(id = null, enable = true, child = null)
        data.deleted = 0
        data.creatorId = sessions.user?.id
        data.creator = null
        data.company = null
        permissionDB.insertOne(data)


        val result = permissionDB.find().toList()
        val tree = getTree(result, null)
        return CommonResp.success(data = tree)
    }
}