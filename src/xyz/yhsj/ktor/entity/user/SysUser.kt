package xyz.yhsj.ktor.entity.user

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import xyz.yhsj.ktor.validator.ValidationGroup
import javax.validation.constraints.NotBlank

data class SysUser(
    @BsonId
    val id: Id<SysUser> = newId(),
    @field: NotBlank(message = "用户名不可为空", groups = [ValidationGroup.Login::class])
    var userName: String? = null,
    @Transient
    @field: NotBlank(message = "密码不可为空", groups = [ValidationGroup.Login::class])
    var password: String? = null,
    var type: Int? = 0,
    var deleted: Int? = 0,
)