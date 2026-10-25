package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.MISSING_PARENTHESIS
import com.techreier.edrops.calc.NumberToken
import com.techreier.edrops.calc.Op
import com.techreier.edrops.calc.OperatorToken
import com.techreier.edrops.calc.toExpr
import com.techreier.edrops.config.logger
import com.techreier.edrops.exceptions.ParseException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FindClosureTest {

    // (((3 + 4) * (5 - 1) + (6 / (2 + 1))))
    private val tokens = listOf(
        OperatorToken(Op.LEFTP, 0),
        OperatorToken(Op.LEFTP, 1),
        OperatorToken(Op.LEFTP, 2),
        NumberToken(3, 3),
        OperatorToken(Op.ADD, 4),
        NumberToken(4, 5),
        OperatorToken(Op.RIGHTP, 6),  // End (3 + 4)
        OperatorToken(Op.MULTIPLY, 7),
        OperatorToken(Op.LEFTP, 8),
        NumberToken(5, 9),
        OperatorToken(Op.SUBTRACT, 10),
        NumberToken(1, 11),
        OperatorToken(Op.RIGHTP, 12), // End (5 - 1)
        OperatorToken(Op.ADD, 13),
        OperatorToken(Op.LEFTP, 14),
        NumberToken(6, 15),
        OperatorToken(Op.DIVIDE, 16),
        OperatorToken(Op.LEFTP, 17),
        NumberToken(2, 18),
        OperatorToken(Op.ADD, 19),
        NumberToken(1, 20),
        OperatorToken(Op.RIGHTP, 21),
        OperatorToken(Op.RIGHTP, 22),
        OperatorToken(Op.RIGHTP, 23),
        OperatorToken(Op.RIGHTP, 24)
    )


    @Test
    fun findClosureTest() {
        val argumentInfo = findClosure(Range(14, tokens.size - 1), tokens)
        logger.info(tokens.toExpr())
        logger.info(tokens.toExpr(argumentInfo.range))
        Assertions.assertEquals(14, argumentInfo.range.min)
        Assertions.assertEquals(22, argumentInfo.range.max)
    }

    @Test
    fun findClosureFailsTest() {
        val e = assertThrows<ParseException> {
            findClosure(Range(13, tokens.size - 1), tokens)
        }
        Assertions.assertEquals(MISSING_PARENTHESIS, e.parseError.key)
        Assertions.assertEquals(13, e.parseError.position)
        Assertions.assertEquals("(", e.parseError.oper)
    }

    @Test
    fun findFirstFreeOperatorTest() {
        val e = assertThrows<ParseException> {
            findClosure(Range(13, tokens.size - 1), tokens)
        }
        Assertions.assertEquals(MISSING_PARENTHESIS, e.parseError.key)
        Assertions.assertEquals(13, e.parseError.position)
        Assertions.assertEquals("(", e.parseError.oper)
    }
}