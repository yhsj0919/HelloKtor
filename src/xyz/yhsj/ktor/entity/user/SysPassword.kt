package xyz.yhsj.ktor.entity.user

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class SysPassword(
    @BsonId
    val id: Id<SysPassword>? = newId(),
    var user: Id<SysUser>? = null,
    var password: String? = null,
)