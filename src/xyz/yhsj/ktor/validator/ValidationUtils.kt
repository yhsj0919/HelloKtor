package xyz.yhsj.ktor.validator


import xyz.yhsj.ktor.ext.json
import java.io.Serializable
import java.util.*
import javax.validation.Validation
import javax.validation.groups.Default

object ValidationUtils {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    fun <T> validateEntity(obj: T?, vararg groups: Class<*>): ValidationResult {
        val result = ValidationResult()

        if (obj == null) {
            return result
        }
        val set = validator.validate(obj, *groups)
        if (set.isNotEmpty()) {
            true.also { result.hasErrors = it }
            val errorMsg = HashMap<String, String>()
            for (cv in set) {
                errorMsg[cv.propertyPath.toString()] = cv.message
            }
            result.errorMsg = errorMsg
        }
        return result
    }

    fun <T> validateProperty(obj: T, propertyName: String): ValidationResult {
        val result = ValidationResult()
        val set = validator.validateProperty(obj, propertyName, Default::class.java)
        if (set.isNotEmpty()) {
            result.hasErrors = true
            val errorMsg = HashMap<String, String>()
            for (cv in set) {
                errorMsg[propertyName] = cv.message
            }
            result.errorMsg = errorMsg
        }
        return result
    }
}


