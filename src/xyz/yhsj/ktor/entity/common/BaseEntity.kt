package xyz.yhsj.ktor.entity.common

import com.mongodb.DBRef
import org.litote.kmongo.Id
import xyz.yhsj.ktor.entity.company.SysCompany
import xyz.yhsj.ktor.entity.user.SysUser

open class BaseEntity(
    @Transient
    var page: Int? = null,
    @Transient
    var size: Int? = null,
    //是否删除
    var deleted: Int? = 0,
    //备注
    var note: String? = null,

    //公司Id
    var companyId: Id<SysCompany>? = null,
    //公司
    var company: SysCompany? = null,
    //创建人Id
    var creatorId: Id<SysUser>? = null,
    //创建人
    var creator: SysUser? = null,
)