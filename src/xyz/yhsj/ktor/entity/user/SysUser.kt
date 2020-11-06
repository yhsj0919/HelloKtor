package xyz.yhsj.ktor.entity.user

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import xyz.yhsj.ktor.validator.ValidationGroup
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class SysUser(
    @field:NotBlank(message = "ID不可为空", groups = [ValidationGroup.Update::class, ValidationGroup.Delete::class])
    @BsonId
    val id: Id<SysUser> = newId(),
    @field: NotBlank(message = "用户名不可为空", groups = [ValidationGroup.Login::class, ValidationGroup.Add::class])
    @field:Pattern(
        regexp = "^[1][3456789]\\d{9}\$",
        message = "账号只能为手机号",
        groups = [ValidationGroup.Add::class, ValidationGroup.Login::class]
    )
    var userName: String? = null,

    //昵称
    @field: NotBlank(message = "昵称不可为空", groups = [ValidationGroup.Add::class])
    var nickName: String? = null,
    //Gson序列化反序列化忽略，
    //@Expose(serialize = true, deserialize = true)
    @Transient
    @field: NotBlank(message = "密码不可为空", groups = [ValidationGroup.Login::class, ValidationGroup.Add::class])
    var passWord: String? = null,
    //-1系统管理员，0普通人员
    var type: Int? = 0,
    var deleted: Int? = 0,
)