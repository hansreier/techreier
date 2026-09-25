package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.ArrayDeque
import java.util.EnumSet
import java.util.Locale

class Expr(var calc: Calc<*>) {

    var expr: String = ""
        set(value) {
            field = value.trim()
        }

    private val tokens = ArrayList<Token>()
    private var level: Int = 0
    private var trace: Trace = Trace.OFF
    private var opStack = ArrayDeque<Oper>()

    init {
        calc.logOp = false
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

    fun traceLevel(trace: Trace) {
        this.trace = trace
        calc.logOp = trace != Trace.OFF
    }

    fun op(text: String, pos: Int, set: EnumSet<Op>): Oper? {
        val foundOp = Op.operator(text, pos, set) ?: return null
        return Oper(foundOp, pos)
    }

    private fun trace(text: String) {
        if (trace == Trace.ALL) {
            logger.info(text)
        }
    }

    private fun warn(text: String) {
        logger.warn(text)
    }

    private fun err(operator: String, pos: Int, text: String) {
        logger.info(expr) //TODO ReierAsk remove
        logger.error("Error: $operator pos=$pos: $text \n" +
                "${expr.substring(0, pos  + operator.length -1) +"???" + expr.substring(pos + operator.length -1)} ")
        calc.logStack()
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
                    noArgs -= (t.operator.noArgs() - 1)
                }
            }
            if (noArgs != o.noArgs()) {
                err(o.abbrev(),o.pos + 1,  "Number of arguments $noArgs should be ${o.noArgs()}")
                return false
            }
        }

        tokens.add(Token(o))
        trace("@adding token:$o")
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

    fun parse(): Boolean {
        var i1: Int
        var i2: Int
        var numbers = 0
        var n: Number?
        var pos: ParsePosition
        var os: Oper?
        var ov: Oper?
        var oh: Oper?
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
                n = parseNumber(expr, pos)
                i2 = pos.index
                ov = null
                if (i2 <= i1) {
                    ox = o
                    ov = op(expr, i1, calc.operators)
                }

                if ((i2 > i1) || ((ov != null) && ov.noArgs() == 0)) {
                    numbers++ //number (or operator with zero arguments) found

                    if (ov == null) {
                        trace("number: $n[$i1] sequence: $numbers")
                        trace("@adding token: $n[$i1]")
                        tokens.add(Token(n, i1))
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
                                            oh = op(expr, i2, Op.basicOperators())
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
                                        err(o.abbrev(),i1 + 1, "can not directly follow ${ox.abbrev()}"  )
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
                            err(expr.substring(i1, i2-1), i1 + 1, "Invalid operator" )
                            return false
                        } else {
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

            if (plevel < 0) {
                warn("Advarsel: ${-plevel} for mange høyreparenteser.")
            } else if (plevel > 0) {
                warn("Advarsel: $plevel for mange venstreparenteser.")
            } else if (level != 0) {
                warn("Advarsel: Sluttnivå $level forskjellig fra null, trolig programmeringsfeil!")
            }
            return true
        } finally {
            opStack.clear()
            delP()
        }
    }

    fun parse(expr: String): Boolean {
        this.expr = expr
        return parse()
    }
}