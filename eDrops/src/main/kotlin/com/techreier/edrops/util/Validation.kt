package com.techreier.edrops.util

import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import org.slf4j.LoggerFactory
import org.springframework.validation.BindingResult
import org.springframework.dao.DuplicateKeyException

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")

fun checkStringSize(
    value: String?,
    maxSize: Int,
    field: String,
    bindingResult: BindingResult,
    minSize: Int = 0,
): Boolean {
    val form = bindingResult.objectName
    if (value.isNullOrBlank()) {
        if (minSize >= 1) {
            bindingResult.rejectValue(field, "error.empty")
            return false
        }
        return true
    }
    if (value.length > maxSize) {
        logger.info("$form $field: ${value.length} is longer than the allowed size: $maxSize")
        bindingResult.rejectValue(field,"error.maxSize", arrayOf(maxSize),  value)
        return false
    }
    if (value.length < minSize) {
        logger.info("$form $field: ${value.length} is shorter than the minimum size: $minSize")
        bindingResult.rejectValue(field,"error.minSize", arrayOf(minSize),  value)
        return false
    }
    val byteSize = value.toByteArray(Charsets.UTF_8).size
    if (byteSize > maxSize) {
        logger.info("$form $field: $byteSize (checked for multibyte) is longer than the allowed size: $maxSize")
        bindingResult.rejectValue(field,"error.maxSizeM", arrayOf(maxSize),  value)
        return false
    }
    return true
}

fun checkSegment(
    value: String?,
    field: String,
    bindingResult: BindingResult,
): Boolean {
    val form = bindingResult.objectName
    val regex = "^[a-z](?:[a-z0-9-]*[a-z0-9])?$".toRegex()

    if (value.isNullOrBlank()) {
        logger.info("$form $field: used in URL and cannot be empty")
        bindingResult.rejectValue(field, "error.empty")
        return false
    }
    if (value.length > MAX_SEGMENT_SIZE) {
        logger.info("$form $field: ${value.length} is longer than the allowed size: $MAX_SEGMENT_SIZE")
        bindingResult.rejectValue(field,"error.maxSize", arrayOf(MAX_SEGMENT_SIZE),  value)
        return false
    }
    if (!value.matches(regex)) {
        logger.info("$form $field: used in URL, use lower case and no special characters ")
        bindingResult.rejectValue(field, "error.segment", value)
        return false
    }
    return true
}

fun checkInt(
    value: String?,
    field: String,
    bindingResult: BindingResult,
    minValue: Int? = null, maxValue: Int? = null, required: Boolean = true,
): Int? {
    if (value.isNullOrBlank()) {
        if (required)
            bindingResult.rejectValue(field, "error.empty")
        return null
    } else {
        val result = value.toIntOrNull()
        if (result == null)
            bindingResult.rejectValue(field,"error.noInteger")
        else if ((minValue != null) && (result < minValue))
            bindingResult.rejectValue(field,"error.lessThan", arrayOf(minValue),  value)
        else if ((maxValue != null) && (result > maxValue))
            bindingResult.rejectValue(field, "error.greaterThan", arrayOf(maxValue), value)
        return result
    }
}

fun checkLong(
    value: String?,
    field: String,
    bindingResult: BindingResult,
    minValue: Long? = null, maxValue: Long? = null, required: Boolean = true,
): Long? {
    if (value.isNullOrBlank()) {
        if (required)
            bindingResult.rejectValue(field, "error.empty")
        return null
    } else {
        val result = value.toLongOrNull()
        if (result == null)
            bindingResult.rejectValue(field, "error.noLong", value)
        else if ((minValue != null) && (result < minValue))
            bindingResult.rejectValue(field,"error.lessThan", arrayOf(minValue),  value)
        else if ((maxValue != null) && (result > maxValue))
            bindingResult.rejectValue(field, "error.greaterThan", arrayOf(maxValue), value)
        return result
    }
}

fun checkDouble(
    value: String?,
    field: String,
    bindingResult: BindingResult,
    minValue: Double? = null, maxValue:Double? = null, required: Boolean = true,
): Double? {
    if (value.isNullOrBlank()) {
        if (required)
            bindingResult.rejectValue(field, "error.empty")
        return null
    } else {
        val result = value.toDoubleOrNull()
        if (result == null)
            bindingResult.rejectValue(field, "error.noDouble", value)
        else if ((minValue != null) && (result < minValue))
            bindingResult.rejectValue(field,"error.lessThan", arrayOf(minValue),  value)
        else if ((maxValue != null) && (result > maxValue))
            bindingResult.rejectValue(field, "error.greaterThan", arrayOf(maxValue), value)
        return result
    }
}

// To check for uniqueness of combined attributes in a list of objects (One, Pair, Triple, More: Use data class)
// Pair example: blogList.checkDuplicates { it.segment to it.topic.language.code}
fun <T, K> Iterable<T>.checkDuplicates(keySelector: (T) -> K) {
    val duplicates = this.groupBy(keySelector).filterValues { it.size > 1 }
    if (duplicates.isNotEmpty()) {
        throw DuplicateKeyException("Duplicate items detected: ${duplicates.keys}")
    }
}





