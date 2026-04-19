package com.techreier.edrops.domain


enum class AccessLevel(val level: Int) {
    READ(0),
    WRITE(1),
    ADMIN(2);

    companion object {
        fun fromInt(level: Int) = entries.firstOrNull { it.level == level } ?: READ
    }
}
