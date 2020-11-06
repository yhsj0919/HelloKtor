package xyz.yhsj.ktor.entity.common

open class BaseEntity(
    @Transient
    var page: Int = 0,
    @Transient
    var size: Int = 10
)
