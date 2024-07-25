package com.techreier.edrops.util

import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.ThreadMXBean

const val MBYTE = 1024 * 1024
fun mem(): MB {
    val memory: MemoryMXBean = ManagementFactory.getMemoryMXBean()
    val threads: ThreadMXBean = ManagementFactory.getThreadMXBean()
    val mem = MB(
        memory.heapMemoryUsage.init,
        memory.heapMemoryUsage.used,
        memory.heapMemoryUsage.committed,
        memory.heapMemoryUsage.max,
        threads.threadCount,
        Thread.currentThread().isVirtual
    )
    return mem
}

data class MB(
    val init: Long, val used: Long, val committed: Long, val max: Long, val threads: Int,
    val virtual: Boolean
) {
    override fun toString(): String {
        return "init=${init / MBYTE}MB, used=${used / MBYTE}MB, committed=${committed / MBYTE}MB" +
                ", max=${max / MBYTE}MB, ${if (virtual) "vthreads=" else "threads="}$threads"
    }
}



