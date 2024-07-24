package com.techreier.edrops.util

import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.ThreadMXBean
import java.math.BigDecimal
import java.math.RoundingMode

const val MByte = 1024 * 1024

private val logger = LoggerFactory.getLogger("memory")

fun mem(): MB {
    val memory: MemoryMXBean = ManagementFactory.getMemoryMXBean()
    val os = ManagementFactory.getOperatingSystemMXBean() as? com.sun.management.OperatingSystemMXBean
    val cpuLoad:  BigDecimal? = os?.let { BigDecimal(it.cpuLoad).setScale(3, RoundingMode.HALF_UP) }
    val threads: ThreadMXBean = ManagementFactory.getThreadMXBean()
    val mem = MB(
        memory.heapMemoryUsage.init / MByte,
        memory.heapMemoryUsage.used / MByte,
        memory.heapMemoryUsage.committed / MByte,
        memory.heapMemoryUsage.max / MByte,
        threads.threadCount,
        Thread.currentThread().isVirtual,
        cpuLoad
    )
    return mem
}

fun logMem() {
    logger.info("${mem()}")
}

data class MB(
    val init: Long, val used: Long, val committed: Long, val max: Long, val threads: Int,
    val virtual: Boolean, val cpu: BigDecimal?
)



