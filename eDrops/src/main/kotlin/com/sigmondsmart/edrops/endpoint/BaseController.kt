package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Language

open class BaseController {

    //Start with hard coding languages
    protected fun fetchLanguages(): MutableList<Language> {
        logger.info("Fetch languages (hard coded)")
        return mutableListOf(
            Language("lang.no","nb-no"),
            Language("lang.eng","en")
        )
    }
}