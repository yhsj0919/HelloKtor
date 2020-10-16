package xyz.yhsj.ktor.entity

data class RespResult(
    val code: Int = 200,
    val msg: String = "操作成功",
    val data: Any? = null
) {
    companion object {
        fun success(data: Any?) = RespResult(data = data)
        fun error(code: Int, msg: String) = RespResult(code, msg)
    }
}
