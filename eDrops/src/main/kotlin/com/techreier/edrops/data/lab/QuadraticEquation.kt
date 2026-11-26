package com.techreier.edrops.data.lab

import com.techreier.edrops.data.EN
import com.techreier.edrops.data.ENGLISH
import com.techreier.edrops.data.NB
import com.techreier.edrops.data.NORWEGIAN
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.util.timestamp

object QuadraticEquation : IExpression {
    val timestamp = timestamp("17.05.2025 07:00:00")
    const val SEGMENT = "quadraticeq"
    const val SUBJECT_NO = "Andregradslikning"
    const val SUBJECT_EN = "Quadratic Equation"
    const val POS = 2

    val ABOUT_NB = """
        Finner de reelle løsningene for **x** i en annengradslikning ved hjelp av abc-formelen.

        - Juster koeffisientene **a**, **b** og **c** øverst.
        - Hvis diskriminanten blir negativ, finnes det ingen reelle løsninger.
    """.trimIndent()

    val ABOUT_EN = """
        Finds the real solutions for **x** in a quadratic equation using the quadratic formula.

        - Adjust the coefficients **a**, **b**, and **c** at the top.
        - If the discriminant is negative, there are no real solutions.
    """.trimIndent()

    val EXPR = """
    // Quadratic equation
    // a*x^2 + b*x + c = 0
    x = 2;
    a = 1;
    b = -5;
    c = 6;
    d = sqrt(b^2 - 4 * a * c);
    // Solution
    x1 = (-b + d) / (2 * a); 
    x2 = (-b - d) / (2 * a);
    // Calculate formula
    a * x^2 + b * x + c
""".trimIndent()

    val norwegian: LanguageCode = LanguageCode(NORWEGIAN, NB)
    val english: LanguageCode = LanguageCode(ENGLISH, EN)
    override fun no() = Expression(timestamp, SEGMENT, POS, SUBJECT_NO, ABOUT_NB, EXPR, NB)
    override fun en() = Expression(timestamp, SEGMENT, POS, SUBJECT_EN, ABOUT_EN, EXPR, EN)
}