package com.techreier.edrops.calc

const val MAX_EXPRESSION_DEPTH = 100
const val MAX_TOKENS = 100

const val MISSING_OPERATOR = "MissingOperator"
const val UNPARSEABLE = "Unparseable"
const val EMPTY_PARENTHESIS = "EmptyParenthesis"
const val MISPLACED_PARENTHESIS = "MisplacedParenthesis"
const val MISSING_PARENTHESIS = "MissingParenthesis"
const val TOO_MANY_LEFT_PARENTHESIS = "TooManyLeftParenthesis"
const val TOO_MANY_RIGHT_PARENTHESIS = "TooManyRightParenthesis"
const val MISPLACED_OPERATOR = "MisplacedOperator"
const val MISPLACED_SEPARATOR = "MisplacedSeparator"
const val MISSING_OPERAND = "MissingOperand"
const val CONSECUTIVE_FUNCTIONS = "ConsecutiveFunctions"
const val WRONG_NO_OF_ARGUMENTS = "WrongNoOfArguments"
const val SEPARATOR_NOT_IN_FUNCTION = "SeparatorNotInFunction"
const val EXPRESSION_DEPTH_EXCEEDED = "ExpressionDepthExceeded"
const val MAX_TOKENS_EXCEEDED = "MaxTokensExceeded"
const val UNKNOWN_PARSE_ERROR ="UnknownParseError"