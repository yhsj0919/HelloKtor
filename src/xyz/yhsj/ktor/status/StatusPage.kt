package xyz.yhsj.ktor.status

import com.mongodb.ErrorCategory
import com.mongodb.MongoClientException
import com.mongodb.MongoWriteException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import xyz.yhsj.ktor.entity.resp.CommonResp

fun StatusPages.Configuration.statusPage() {
    status(HttpStatusCode.NotFound) {
        call.respond(HttpStatusCode.OK, CommonResp.notFound(msg = "路径不存在"))
    }
    status(HttpStatusCode.InternalServerError) {
        call.respond(HttpStatusCode.OK, CommonResp.notFound(msg = "服务器异常"))
    }
    exception<Exception> {
        it.printStackTrace()
        println("出现错误:" + it.message)
        call.respond(
            HttpStatusCode.OK, when (it) {
                is MongoWriteException -> {
                    when (ErrorCategory.fromErrorCode(it.code)) {
                        ErrorCategory.UNCATEGORIZED -> CommonResp.error(msg = "数据库异常")
                        ErrorCategory.EXECUTION_TIMEOUT -> CommonResp.error(msg = "数据库超时")
                        ErrorCategory.DUPLICATE_KEY -> {
                            val msg = it.error.message
                                .split("key:")
                                .last()
                                .replace(" ", "")
                                .replace("\"", "'")
                            CommonResp.error(msg = "数据库唯一键重复:$msg")
                        }
                        else -> CommonResp.error(msg = "未知数据库异常:${it.error.message}")
                    }
                }
                is MongoClientException -> {
                    CommonResp.error(msg = "数据库异常:${it.message}")
                }
                else -> {
                    CommonResp.error(msg = "服务器异常")
                }
            }
        )


    }
}