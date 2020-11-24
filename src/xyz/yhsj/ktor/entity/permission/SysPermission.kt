package xyz.yhsj.ktor.entity.permission

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import xyz.yhsj.ktor.entity.common.BaseEntity
import xyz.yhsj.ktor.validator.ValidationGroup
import javax.validation.constraints.NotBlank

data class SysPermission(
    @field:NotBlank(message = "ID不可为空", groups = [ValidationGroup.Update::class, ValidationGroup.Delete::class])
    @BsonId
    val id: Id<SysPermission>? = null,
    //父级
    val parent: Id<SysPermission>? = null,

    @field: NotBlank(message = "名称不可为空", groups = [ValidationGroup.Add::class])
    val name: String? = null,

    //类型，0，菜单，1，接口
    @field: NotBlank(message = "类型不可为空", groups = [ValidationGroup.Add::class])
    val type: Int = 0,

    @field: NotBlank(message = "权重不可为空", groups = [ValidationGroup.Add::class])
    val weight: Int = 0,

    @field: NotBlank(message = "路径不可为空", groups = [ValidationGroup.Add::class])
    val path: Int? = 0,

    val icon: Int? = 0xe867,

    val enable: Boolean = true,

    val child: List<SysPermission>? = null

) : BaseEntity()

