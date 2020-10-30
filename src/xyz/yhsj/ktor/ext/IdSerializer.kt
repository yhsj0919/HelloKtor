package xyz.yhsj.ktor.ext

import com.google.gson.*
import org.litote.kmongo.Id
import org.litote.kmongo.id.IdGenerator
import java.lang.reflect.Type

class IdSerializer : JsonSerializer<Id<*>?>, JsonDeserializer<Id<*>?> {

    override fun serialize(
        data: Id<*>?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(data.toString())
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Id<*>? {
        return if (json.isJsonNull) {
            null
        } else {
            IdGenerator.defaultGenerator.create(json.asString)
        }
    }
}