package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Test

class OpTest {

    @Test
    fun listOperatorTest() {
        logger.info(Op.toString(Op.basicOperators))
        val supportedFunctions = Op.functions.filter { it in CalcDouble().operators }.toSet()
        logger.info(Op.toString(supportedFunctions))
        logger.info(Op.toString(Op.constants))
        logger.info(Op.toString(Op.parenthesis))
        logger.info(Op.toString(Op.variables))
    }
}