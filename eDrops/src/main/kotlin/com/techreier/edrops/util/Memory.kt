package com.techreier.edrops.util

import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.ThreadMXBean
import java.math.BigDecimal
import java.math.RoundingMode

const val MBYTE = 1024 * 1024

fun mem(): MB {
    val memory: MemoryMXBean = ManagementFactory.getMemoryMXBean()
    val os = ManagementFactory.getOperatingSystemMXBean() as? com.sun.management.OperatingSystemMXBean
    val threads: ThreadMXBean = ManagementFactory.getThreadMXBean()
    val mem = MB(
        memory.heapMemoryUsage.init,
        memory.heapMemoryUsage.used,
        memory.heapMemoryUsage.committed,
        memory.heapMemoryUsage.max,
        threads.threadCount,
        Thread.currentThread().isVirtual,
        os?.cpuLoad
    )
    return mem
}

data class MB(
    val init: Long, val used: Long, val committed: Long, val max: Long, val threads: Int,
    val virtual: Boolean, val cpu: Double?
) {
    override fun toString(): String {
        return "init=${init / MBYTE}MB, used=${used / MBYTE}MB, committed=${committed / MBYTE}MB" +
                ", max=${max / MBYTE}MB, ${if (virtual) "vthreads=" else "threads="}$threads, cpu=${cpuPercent(cpu)}%"
    }

    private fun cpuPercent(cpuLoad: Double?): BigDecimal? {
        return cpuLoad?.let { BigDecimal(cpuLoad * 100).setScale(2, RoundingMode.HALF_UP) }
    }
}



