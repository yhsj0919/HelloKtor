package xyz.yhsj.ktor.entity.user

import org.bson.codecs.pojo.annotations.BsonId
import xyz.yhsj.ktor.validator.ValidationGroup
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotBlank

data class SysUser(
    @BsonId val id: UUID = UUID.randomUUID(),
    @field: NotBlank(message = "ID不可为空", groups = [ValidationGroup.Update::class])
    val userName: String,
    val password: String,
    val email: String = "",
    val money: BigDecimal = BigDecimal.valueOf(0),
)