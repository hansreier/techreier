package com.techreier.edrops.util

import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import java.util.Locale

//TODO consider removing, not used any more
object MessageSourceMock: MessageSource {

    override fun getMessage(code: String, args: Array<out Any>?, defaultMessage: String?, locale: Locale): String {
        return code + "." + locale.language
    }

    override fun getMessage(code: String, args: Array<out Any>?, locale: Locale): String {
        return code + "." + locale.language
    }

    override fun getMessage(resolvable: MessageSourceResolvable, locale: Locale): String {
        return resolvable.defaultMessage ?: resolvable.codes?.firstOrNull() ?: "??${resolvable.codes?.firstOrNull()}??"
    }
}