package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.basicOperators
import com.techreier.edrops.calc.Op.Companion.variables
import com.techreier.edrops.config.logger
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.*

class Expr(val calc: Calc<*>, expr: String) {

    val expr: String
    var relaxed: Boolean
    val parseErrors = mutableListOf<ParseError>()
    val tokens = ArrayList<Token>()
    val xTokens = ArrayList<Token>()
    private var level: Int = 0
    private var resultSize = 0
    private var trace: Trace = Trace.OFF
    private var opStack = ArrayDeque<Oper>()
    private var noOfResults = 1

    init {
        this.expr = expr.trim()
        this.relaxed = false
        calc.logOp = false
        trace(expr)
    }

    fun rotateTraceLevel() {
        when (trace) {
            Trace.OFF -> {
                trace = Trace.STACK
                calc.logOp = true
                logger.info("trace stack")
            }

            Trace.STACK -> {
                trace = Trace.ALL
                calc.logOp = true
                logger.info("trace alt")
            }

            Trace.ALL -> {
                trace = Trace.OFF
                calc.logOp = false
                logger.info("trace av")
            }
        }
    }

    fun trace(trace: Trace) {
        this.trace = trace
        calc.logOp = trace != Trace.OFF
    }

    fun operator(text: String, pos: Int, set: Set<Op>): Oper? {
        val foundOp = Op.operator(text, pos, set) ?: return null
        return Oper(foundOp, pos)
    }

    private fun trace(text: String) {
        if (trace == Trace.ALL) {
            logger.info(text)
        }
    }

    private fun warn(text: String, relaxed: Boolean = false) {
        if (relaxed)
            logger.warn("warning: $text")
        else
            logger.error("error: $text")
    }

    fun errorIndicator(errPosition: Int): String {
        val startPos = (errPosition - 40).coerceIn(0, expr.length)
        val endPos = (errPosition + 40).coerceIn(0, expr.length)
        return "${expr.substring(startPos, errPosition)}???" +
                "${if (errPosition == expr.length) "" else expr.substring(errPosition, endPos)}"
    }

    // TODO Remove used by old parser
    private fun err(operator: String, pos: Int, text: String) {
        logger.error(
            "Error: $operator pos=$pos: $text \n" +
                    "${
                        expr.substring(
                            0,
                            pos + operator.length - 1
                        ) + "???" + expr.substring(pos + operator.length - 1)
                    } "
        )
    }

    private fun setLevel(o: Oper?, ox: Oper?) {
        if (o != null) {
            if (o.op == Op.LEFTP) {
                level++
            } else if (ox == null) {
                level = level + Op.maxLevel - o.op.prior()
            } else if (o.op == Op.RIGHTP) {
                level = level + ox.op.prior() - Op.maxLevel - 1
            } else {
                level = level + ox.op.prior() - o.op.prior()
            }
        }
    }

    private fun corrLevel(os: Oper?) {
        if (os != null) {
            level = level + os.op.prior() - Op.maxLevel
            trace("$os correct level to $level")
        }
    }

    private fun addToken(o: Oper): Boolean {
        trace("$o checking arguments:${o.noArgs()}")

        if (o.noArgs() != 0) {
            var noArgs = 0
            for (i in tokens.size - 1 downTo 0) {
                val t = tokens[i]
                if (t.position < o.pos) {
                    if (o.isBasic()) {
                        noArgs++
                    }
                    break
                }
                if (t.type == TokenType.NUMBER) {
                    noArgs++
                } else {
                    noArgs -= (t.operator.noArgs() - t.operator.noResults())
                }
            }
            if (noArgs != o.noArgs()) {
                err(o.abbrev(), o.pos + 1, "Number of arguments $noArgs should be ${o.noArgs()}")
                return false
            }
        }

        resultSize += -o.op.noArgs() + o.op.noResults()
        tokens.add(Token(o))
        trace("@adding token:$o level: $resultSize)")
        return true
    }

    private fun addP() {
        calc.operators.add(Op.LEFTP)
        calc.operators.add(Op.RIGHTP)
    }

    private fun delP() {
        calc.operators.remove(Op.LEFTP)
        calc.operators.remove(Op.RIGHTP)
    }

    fun calculate() {
        try {
            calc.clear() //clear the stack
            calc.logStack = (trace != Trace.OFF)
            for (t in tokens) {
                if (t.type == TokenType.NUMBER) {
                    calc.enter(t.argument)
                } else {
                    if (calc.op(t.operator) == null) {
                        calc.addVarsOnEmptyStack()
                        break
                    }
                }
            }
        } finally {
            if (!calc.logStack) {
                calc.logStack = true
                calc.logStack()
            }
        }
    }

    //one time parse through expression
    fun preparse(): Boolean {
        var i1: Int
        var i2: Int
        var numbers = 0
        var number: Number?
        var pos: ParsePosition
        var oper: Oper? = null
        var pLevel = 0
        var leftpPos = 0
        var whitespace = false
        resultSize = 0
        var lastOpType = OpType.EMPTY
        addP()

        xTokens.clear()

        pos = ParsePosition(0)
        do {
            i1 = pos.index
            while ((i1 < expr.length -1) && (expr[i1].isWhitespace())) {
                whitespace = true
                i1++
            }
            pos.index = i1
            val minus = ((expr).isNotEmpty() && (expr[pos.index] == '-'))

            number = if (!minus || (whitespace && minus)) { //skip number if it is the minus operator
                when (calc.type) {
                    Double::class.javaObjectType -> {
                        parseDouble(expr, pos)
                    }
                    else -> throw Exception("Not implemented") //TODO ReierArk implement for other calculator types
                }
            } else null

            i2 = pos.index
            if (i2 <= i1) {
                oper = operator(expr, i1, calc.operators)
            }
            if (number != null) {
                if (lastOpType == OpType.VARIABLE) {
                    parseErrors.add(ParseError("NumberVar", i1))
                    return false
                }//number
                lastOpType = OpType.NUMBER
                xTokens.add(Token(number, i1))
            } else { //operator (including variables and separators)
                if ((oper == null)) {
                    parseErrors.add(ParseError("Unparseable", i1))
                    return false
                }
                when (oper.op) {
                    Op.LEFTP -> {
                        pLevel++; leftpPos = i1
                        if (lastOpType == OpType.NUMBER || lastOpType == OpType.VARIABLE || lastOpType == OpType.RIGHTP) {
                            xTokens.add(Token(Op.MULTIPLY, i1))
                        }
                        lastOpType = OpType.LEFTP
                    }

                    Op.RIGHTP -> {
                        pLevel--
                        if (lastOpType == OpType.LEFTP  ) {
                            parseErrors.add(ParseError("EmptyParenthesis", i1, oper.abbrev()))
                            return false
                        }
                        if (lastOpType == OpType.OPERATOR ) {
                            parseErrors.add(ParseError("OperatorMisplaced", i1, oper.abbrev()))
                            return false
                        }
                        lastOpType = OpType.RIGHTP
                    }

                    in variables -> {
                        if (lastOpType == OpType.VARIABLE) {
                            parseErrors.add(ParseError("DoubleVar", i1, oper.abbrev()))
                            return false
                        }
                        if (lastOpType == OpType.NUMBER) { //insert multiplicator
                            xTokens.add(Token(Op.MULTIPLY, i1))
                        }
                        lastOpType = OpType.VARIABLE
                    }

                    in basicOperators -> {
                        if (lastOpType == OpType.OPERATOR) {
                            parseErrors.add(ParseError("DoubleOper", i1, oper.abbrev()))
                            return false
                        }
                        lastOpType = OpType.OPERATOR
                    }

                    else -> {
                        if (lastOpType == OpType.FUNCTION) {
                            parseErrors.add(ParseError("DoubleFunc", i1, oper.abbrev()))
                            return false
                        }
                        lastOpType = OpType.FUNCTION
                    }
                }
                if (pLevel < 0) {
                    parseErrors.add(ParseError("RightPExtra", i1))
                    return false
                }
                xTokens.add(Token(oper))
                pos.index = i1 + oper.abbrev().length
            }
        } while (pos.index < expr.length)
        if (pLevel > 0) {
            parseErrors.add(ParseError("LeftPExtra", leftpPos))
            return false
        }
        return true
    }


    // TODO note A few serious bugs found in the original Java code. To be replaced by a recursion based parser.
    fun parse(): Boolean {
        var i1: Int
        var i2: Int
        var numbers = 0
        var n: Number?
        var pos: ParsePosition
        var os: Oper?
        var ov: Oper?
        var oh: Oper?
        resultSize = 0
        addP()
        try {
            tokens.clear()
            opStack = ArrayDeque()
            val formatter = NumberFormat.getInstance(Locale.ENGLISH)
            formatter.isGroupingUsed = false
            if (formatter is DecimalFormat) {
                formatter.isParseBigDecimal = true
            }
            level = 0
            var xlevel = 0
            var plevel = 0
            var o: Oper? = null
            var ox: Oper? = null
            pos = ParsePosition(0)
            do {
                i1 = pos.index
                while ((i1 < expr.length - 1) && (expr[i1].isWhitespace())) {
                    i1++
                }
                pos.index = i1
                n = parseDouble(expr, pos)
                i2 = pos.index
                ov = null
                if (i2 <= i1) {
                    ox = o
                    ov = operator(expr, i1, calc.operators)
                }

                if ((i2 > i1) || ((ov != null) && ov.noArgs() == 0)) {
                    numbers++ //number (or operator with zero arguments) found

                    if (ov == null) {
                        trace("number: $n[$i1] sequence: $numbers")
                        trace("@adding token: $n[$i1]")
                        tokens.add(Token(n, i1))
                        resultSize++
                    } else {
                        trace("variable: $ov[$i1] sequence: $numbers")
                        if (!addToken(ov)) {
                            return false
                        }
                        pos.index = i1 + ov.abbrev().length
                    }
                } else {
                    o = ov
                    if (o != null) {
                        setLevel(o, ox)
                        trace("operator: $o noArgs: ${o.noArgs()} level: $level prevop: $ox")
                        if (o.op == Op.LEFTP) {
                            if (ox != null) {
                                if ((numbers == 0) && (ox.op != Op.LEFTP) && (ox.op != Op.RIGHTP)) {
                                    trace("push:$ox (left of LEFTP)")
                                    opStack.push(ox)
                                } else {
                                    if ((ox.op != Op.LEFTP) && (ox.op != Op.RIGHTP)) {
                                        if (!addToken(ox)) {
                                            return false
                                        }

                                        os = opStack.peek()
                                        while ((os != null) && (os.op != Op.LEFTP)) {
                                            opStack.remove()
                                            if (!addToken(os)) {
                                                return false
                                            }
                                            os = opStack.peek()
                                        }
                                    }
                                    ox = null
                                }
                            }
                            trace("push:$o")
                            opStack.push(o)
                            plevel++
                        } else {
                            if (o.op == Op.RIGHTP) {
                                plevel--
                                if (ox != null) {
                                    if ((ox.op != Op.LEFTP) && (ox.op != Op.RIGHTP)) {
                                        if (!addToken(ox)) {
                                            return false
                                        }
                                    }

                                    os = opStack.poll()
                                    trace("Retrieved from stack:$os")
                                    while ((os != null) && (os.op != Op.LEFTP)) {
                                        if (!addToken(os)) {
                                            return false
                                        }
                                        os = opStack.poll()
                                        trace("retreived from stack:$os")
                                    }
                                    if (os != null) {
                                        os = opStack.peek()
                                        if ((os != null) && (os.op != Op.LEFTP)) {
                                            i2 = i1 + Op.RIGHTP.abbrev().length
                                            while ((i2 < expr.length - 1) && expr[i2].isWhitespace()) {
                                                i2++
                                            }
                                            oh = operator(expr, i2, Op.basicOperators())
                                            trace("$oh to the right of RIGHTP")
                                            if ((oh == null) || (os.op.prior() <= oh.op.prior())) {
                                                corrLevel(os)
                                                while ((os != null) && (os.op != Op.LEFTP)) {
                                                    opStack.remove()
                                                    if (!addToken(os)) {
                                                        return false
                                                    }
                                                    os = opStack.peek()
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (ox != null) {
                                    if ((ox.isOrdinary()) && (o.isOrdinary()) && (numbers == 0)) {
                                        err(o.abbrev(), i1 + 1, "can not directly follow ${ox.abbrev()}")
                                        return false
                                    }

                                    if ((o.isOrdinary() && (numbers > 0)) ||
                                        (o.isBasic() && (((level < xlevel) || ((level == xlevel) && (numbers > 0))) || ox.op == Op.RIGHTP))
                                    ) {
                                        if (ox.op != Op.LEFTP && ox.op != Op.RIGHTP) {
                                            if (!addToken(ox)) {
                                                return false
                                            }
                                        }

                                        os = opStack.peek()
                                        corrLevel(os)
                                        while ((os != null) &&
                                            (os.op != Op.LEFTP) &&
                                            (os.isOrdinary() || (os.isBasic() && ((os.level > level) || (os.level == level))))
                                        ) {
                                            opStack.remove()
                                            if (!addToken(os)) {
                                                return false
                                            }
                                            os = opStack.peek()
                                        }
                                    } else {
                                        if (ox.op != Op.LEFTP && ox.op != Op.RIGHTP) {
                                            trace("push:$ox")
                                            ox.level = xlevel
                                            opStack.push(ox)
                                        }
                                    }
                                }
                                numbers = 0
                            }
                        }
                        o.level = level
                        xlevel = level
                    } else {
                        if (expr.isNotEmpty()) {
                            var c: Char
                            i2 = i1
                            do {
                                c = expr[i2]
                                i2++
                            } while ((i2 < expr.length) && ((c.isLetterOrDigit() || (c.toString() == Op.SEPARATOR.abbrev()))))
                            err(expr.substring(i1, i2), i1 + 1, "Invalid operator")
                            return false
                        } else {
                            logger.info("Reier was here")
                            return true
                        }
                    }
                    pos.index = i1 + o.abbrev().length
                }
                trace("----------------------------------")
            } while (pos.index < expr.length)

            trace("Do the remaining operators")
            os = o
            corrLevel(o)
            if ((o != null) && (os.op != Op.RIGHTP) && (os.op != Op.LEFTP)) {
                if (!addToken(o)) {
                    return false
                }
            }
            os = opStack.poll()
            while (os != null) {
                if ((os.op != Op.RIGHTP) && (os.op != Op.LEFTP)) {
                    if (!addToken(os)) {
                        return false
                    }
                }
                os = opStack.poll()
            }
            trace("----------------------------------")

            // Warn for unbalanced expression (parenthesis).
            // Note: The code does not warn for other stupidities like )(
            // In general this is just ignored if the parser cannot find the use
            // of this expression leveling.
            trace("number of results: " + resultSize)

            if ((resultSize != noOfResults)) {
                warn("$resultSize result values, expected $noOfResults", relaxed)
                return relaxed
            }

            if (plevel < 0) {
                warn("${-plevel} too many right parentheses", relaxed)
                return relaxed
            } else if (plevel > 0) {
                warn("$plevel too many left parentheses", relaxed)
                return relaxed
            } else if (level != 0) {
                warn("Unbalanced structure in formula", relaxed)
                return relaxed
            }

            if (opStack.isEmpty()) return true
            warn("Warning, Stack is not empty, contains ${opStack.size} elements, first is ${opStack.peek()}")
            return relaxed
        } finally {
            opStack.clear()
            delP()
        }
    }
}