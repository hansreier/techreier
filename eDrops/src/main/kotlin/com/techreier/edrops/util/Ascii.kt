package com.techreier.edrops.util

typealias Presenter = (matrix: List<List<String>>, maxCharsPerLine: Int) -> String

fun asciiToTable(
    source: String,
    presenter: Presenter,
    invisibleChar: Char = '#',
    maxCharsPerLine: Int = 10,
): String {
    val matrix = stringToMatrix(source, maxCharsPerLine, invisibleChar)
    return presenter(matrix, maxCharsPerLine)
}

// Make a Matrix of the String
fun stringToMatrix(
    source: String,
    maxCharsPerLine: Int = 10,
    invisibleChar: Char = '#',
): List<List<String>> {

    val asciiEntries: List<String> = source.map { char ->
        val codeValue = char.code
        val displayChar = if (char.isISOControl()) invisibleChar else char
        "$displayChar->$codeValue"
    }

    return asciiEntries.chunked(maxCharsPerLine)
}

fun asciiMarkdown(
    matrix: List<List<String>>,
    maxCharsPerLine: Int = 10,
): String {
    return markdownHeadline(maxCharsPerLine) + markdownTable(matrix, maxCharsPerLine)
}

fun markdownHeadline(maxCharsPerLine: Int = 10): String {

    val result = StringBuilder()

    result.append("| Pos |")

    val headers = (0 until maxCharsPerLine).map { it }
    result.append(headers.joinToString("|", transform = { " $it " }))
    result.append("|\n")

    result.append("|---|")
    result.append(headers.joinToString("|", transform = { "---" }))
    result.append("|\n")

    return result.toString()
}

fun markdownTable(
    matrix: List<List<String>>,
    maxCharsPerLine: Int = 10,
): String {

    val result = StringBuilder()

    matrix.forEachIndexed { rowIndex, row ->

        val rowPrefix = rowIndex * maxCharsPerLine
        result.append("| $rowPrefix |")
        result.append(row.joinToString("|", transform = { " $it " }))
        val paddingCount = maxCharsPerLine - row.size
        if (paddingCount > 0) {
            result.append("|")
            result.append((0 until paddingCount).joinToString("|", transform = { " " }))
        }

        result.append("|\n")
    }

    return result.toString()
}

fun asciiLog(
    matrix: List<List<String>>,
    maxCharsPerLine: Int = 10,
): String {
    return logHeadline(maxCharsPerLine) + "\n" + logTable(matrix, maxCharsPerLine)
}

fun logHeadline(maxCharsPerLine: Int = 10): String {

    val posWidth = 5
    val dataWidth = 9
    val separator = "  "

    val result = StringBuilder()

    result.append("Pos".padEnd(posWidth))

    val headers = (0 until maxCharsPerLine).map { it.toString() }
    result.append(separator)
    headers.forEachIndexed { index, header ->
        result.append(header.padEnd(dataWidth))
        if (index < maxCharsPerLine - 1) {
            result.append(separator)
        }
    }

    result.append("\n")
    result.append("-".repeat(posWidth))
    result.append(separator)

    (0 until maxCharsPerLine).forEachIndexed { index, _ ->
        result.append("-".repeat(dataWidth))
        if (index < maxCharsPerLine - 1) {
            result.append(separator)
        }
    }

    return result.toString()
}

fun logTable(
    matrix: List<List<String>>,
    maxCharsPerLine: Int = 10,
): String {

    val posWidth = 5
    val dataWidth = 9
    val separator = "  "

    val result = StringBuilder()

    matrix.forEachIndexed { rowIndex, row ->

        val rowPrefix = rowIndex * maxCharsPerLine

        result.append(rowPrefix.toString().padEnd(posWidth))

        result.append(separator)
        row.forEachIndexed { colIndex, entry ->
            result.append(entry.padEnd(dataWidth))
            if (colIndex < row.size - 1) {
                result.append(separator)
            }
        }

        val paddingCount = maxCharsPerLine - row.size
        if (paddingCount > 0) {
            (0 until paddingCount).forEach { _ ->
                result.append(separator)
                result.append(" ".repeat(dataWidth))
            }
        }

        result.append("\n")
    }

    return result.toString().trim()
}

