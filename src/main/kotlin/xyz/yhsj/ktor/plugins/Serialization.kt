package xyz.yhsj.ktor.plugins

import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import org.litote.kmongo.Id
import xyz.yhsj.ktor.ext.IdSerializer
import java.lang.reflect.Modifier
import java.text.DateFormat

fun Application.configureSerialization() {
    //序列化
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
            //序列化空值
//            serializeNulls()
            //忽略修饰符，这里仅忽略static类型的
            excludeFieldsWithModifiers(Modifier.STATIC)
            //序列化Id
            registerTypeHierarchyAdapter(Id::class.java, IdSerializer())
        }
    }
}
