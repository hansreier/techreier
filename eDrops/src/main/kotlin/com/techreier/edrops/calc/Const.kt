package com.techreier.edrops.calc

const val MAX_EXPRESSION_DEPTH = 100
const val MAX_TOKENS = 1000
const val OPERATOR_TEXT_LOG_SIZE = 8

// Parsing errors
const val ILLEGAL_VARIABLES = "IllegalVariables"
const val MISSING_OPERATOR = "MissingOperator"
const val UNPARSEABLE = "Unparseable"
const val EMPTY_PARENTHESIS = "EmptyParenthesis"
const val MISPLACED_PARENTHESIS = "MisplacedParenthesis"
const val MISSING_PARENTHESIS = "MissingParenthesis"
const val PARENTHESIS_MESS = "ParenthesisMess"
const val TOO_MANY_LEFT_PARENTHESIS = "TooManyLeftParenthesis"
const val WRONG_ARGUMENT_COUNT = "WrongArgumentCount"
const val TOO_MANY_RIGHT_PARENTHESIS = "TooManyRightParenthesis"
const val MISPLACED_OPERATOR = "MisplacedOperator"
const val MISPLACED_SEPARATOR = "MisplacedSeparator"
const val MISSING_OPERAND = "MissingOperand"
const val CONSECUTIVE_FUNCTIONS = "ConsecutiveFunctions"
const val CONSECUTIVE_NEGATE = "ConsecutiveNegate"
const val SEPARATOR_NOT_IN_FUNCTION = "SeparatorNotInFunction"
const val EXPRESSION_DEPTH_EXCEEDED = "ExpressionDepthExceeded"
const val MAX_TOKENS_EXCEEDED = "MaxTokensExceeded"
const val VARIABLE_NOT_FOUND ="VariableNotFound"

// Calculation errors
const val CALCULATION_ERROR = "CalculationError"
const val TOO_FEW_ARGUMENTS = "TooFewArguments"
const val INVALID_ARGUMENT = "InvalidArguments"
const val VARYING_ARGUMENT_TYPES = "VaryingArgumentTypes"
const val NO_RESULT ="NoResult"
const val MULTIPLE_RESULTS = "MultipleResults"
const val DOUBLE_FORMAT_ERROR = "DoubleFormatError"
const val BIG_INTEGER_FORMAT_ERROR = "BigIntegerFormatError"
const val BIG_DECIMAL_FORMAT_ERROR = "BigDecimalFormatError"