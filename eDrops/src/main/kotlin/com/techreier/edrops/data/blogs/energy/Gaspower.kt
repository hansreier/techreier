package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.strip
import com.techreier.edrops.util.timestamp

    object Gaspower: BlogPosts {
        val timestamp = timestamp("12.05.2025 16:00:00")
        const val SEGMENT = "gaspower"
        const val TITLE_NO = "Gasskraft"
        const val TITLE_EN = "Gas power"

        const val SUMMARY_NO = "Gass er en fossil energikilde, som slipper ut CO2, " +
                "men mindre enn kull og olje. Samtidig er den fleksibel. " +
                "Gasskraftverk leverer stabil elkraft uansett vær og kan plasseres der strømmen trengs. " +
                "Den trenger ikke store naturinngrep som vindturbiner gjerne gir. " +
                "Direkte bruk av gass er mer effektivt enn omveien om strøm."

        const val SUMMARY_EN = "Gas is a fossil energy source that emits CO2, " +
                "but less than coal and oil. At the same time, it is flexible " +
                "Gas power plants provides stable electric power regardless of weather, " +
                "and can be placed where the power is needed. " +
                "It does not require large environmental interventions like wind turbines often do. " +
                "Direct use of gas is more efficient than converting it to electricity first."

        override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO.strip(), blog)

        override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN.strip(), blog)
    }
