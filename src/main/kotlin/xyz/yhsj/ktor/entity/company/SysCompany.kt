package xyz.yhsj.ktor.entity.company

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import xyz.yhsj.ktor.entity.common.BaseEntity
import xyz.yhsj.ktor.validator.ValidationGroup
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * 公司
 */
data class SysCompany(
    @field:NotNull(message = "ID不可为空", groups = [ValidationGroup.Update::class, ValidationGroup.Delete::class])
    @BsonId
    val id: Id<SysCompany>? = null,
    //公司名称
    @field:NotBlank(message = "公司名称不可为空", groups = [ValidationGroup.Add::class])
    var name: String? = null,
    //联系方式
    @field:NotBlank(message = "联系方式不可为空", groups = [ValidationGroup.Add::class])
    var phone: String? = null,

    //到期时间
    @field:NotNull(message = "到期时间不可为空", groups = [ValidationGroup.Add::class])
    var expirationTime: Long? = null,
    //状态0,正常,1禁用
    var status: Int? = 0,
    //短信通知,0无权限，1有权限
    var smsNotification: Int? = 0,
    //短信通知数量
    var smsCount: Int? = 0,
    //短信通知总数
    var smsTotal: Int? = 0,
    //纬度
    var latitude: String? = null,
    //经度
    var longitude: String? = null,
    //地址
    var address: String? = null,

    ) : BaseEntity()