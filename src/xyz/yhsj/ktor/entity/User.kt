package xyz.yhsj.ktor.entity

import org.bson.codecs.pojo.annotations.BsonId
import java.math.BigDecimal
import java.util.*

data class User(
    @BsonId val id: UUID = UUID.randomUUID(),
    val userName: String,
    val password: String,
    val email: String = "",
    val money: BigDecimal = BigDecimal.valueOf(0),
)