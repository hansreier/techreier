package com.techreier.edrops.util

import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")

fun checkStringSize(
    value: String?,
    maxSize: Int,
    field: String,
    bindingResult: BindingResult,
    messageSource: MessageSource,
    minSize: Int = 0,
) {
    val form = bindingResult.objectName
    if (value.isNullOrBlank()) {
        if (minSize == 1) {
            bindingResult.addFieldError(field, "empty", messageSource, value)
        }
        return
    }
    if (value.length > maxSize) {
        logger.info("$form $field: ${value.length} is longer than the allowed size: $maxSize")
        bindingResult.addFieldError(field, "maxSize", messageSource, value)
        return
    }
    if (value.length < minSize) {
        logger.info("$form $field: ${value.length} is shorter than the minimum size: $minSize")
        bindingResult.addFieldError(field, "minSize", messageSource, value)
        return
    }
    val byteSize = value.toByteArray(Charsets.UTF_8).size
    if (byteSize > maxSize) {
        logger.info("$form $field: $byteSize (checked for multibyte) is longer than the allowed size: $maxSize")
        bindingResult.addFieldError(field, "maxSizeM", messageSource, value)
    }
}

fun checkSegment(
    value: String?,
    field: String,
    messageSource: MessageSource,
    bindingResult: BindingResult,
) {
    val form = bindingResult.objectName
    val regex = "^[a-z](?:[a-z0-9-]*[a-z0-9])?$".toRegex()

    if (value.isNullOrBlank()) {
        logger.info("$form $field: used in URL and cannot be empty")
        bindingResult.addFieldError(field, "empty", messageSource, value)
        return
    }
    if (value.length > MAX_SEGMENT_SIZE) {
        logger.info("$form $field: ${value.length} is longer than the allowed size: $MAX_SEGMENT_SIZE")
        bindingResult.addFieldError(field, "maxSize", messageSource, value)
        return
    }
    if (!value.matches(regex)) {
        logger.info("$form $field: used in URL, use lower case and no special characters ")
        bindingResult.addFieldError(field, "segment", messageSource, value)
    }
}

fun checkInt(
    value: String?,
    field: String,
    bindingResult: BindingResult,
    messageSource: MessageSource, minValue: Int? = null, maxValue: Int? = null, required: Boolean = true
): Int? {
    if (value.isNullOrBlank()) {
        if (required)
            bindingResult.addFieldError(field, "empty", messageSource)
        return null
    } else {
        val result = value.toIntOrNull()
        if (result == null)
            bindingResult.addFieldError(field, "noInteger", messageSource, value)
        else if ((minValue != null) && (result < minValue))
                bindingResult.addFieldError(field, "lessThan", messageSource, value, arrayOf(minValue))
        else if ((maxValue != null) && (result > maxValue))
                    bindingResult.addFieldError(field, "greaterThan", messageSource, value, arrayOf(maxValue))
        return result
    }
}

fun checkLong(
    value: String?,
    field: String,
    bindingResult: BindingResult,
    messageSource: MessageSource, minValue: Long? = null, maxValue: Long? = null, required: Boolean = true
): Long? {
    if (value.isNullOrBlank()) {
        if (required)
            bindingResult.addFieldError(field, "empty", messageSource)
        return null
    } else {
        val result = value.toLongOrNull()
        if (result == null)
            bindingResult.addFieldError(field, "noInteger", messageSource, value)
        else if ((minValue != null) && (result < minValue))
                bindingResult.addFieldError(field, "lessThan", messageSource, value, arrayOf(minValue))
        else if ((maxValue != null) && (result > maxValue))
                    bindingResult.addFieldError(field, "greaterThan", messageSource, value, arrayOf(maxValue))
        return result
    }
}

// Extension function to simplify implementation of adding field error
// Normally a default value should be added, but not required
// (The simplified FieldError constructor with 3 arguments did not allow for default value)
fun BindingResult.addFieldError(
    field: String,
    key: String,
    messageSource: MessageSource,
    defaultFieldValue: String? = null,
    args: Array<Any>? = null,
) {
    addError(
        FieldError(
            this.objectName, field, defaultFieldValue, true, null, null,
            msg(messageSource, "error.$key", args)
        )
    )
}
