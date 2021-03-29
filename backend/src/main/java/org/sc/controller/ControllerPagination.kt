package org.sc.controller

import org.springframework.stereotype.Component
import kotlin.math.ceil

@Component
object ControllerPagination {

    private fun electPageSize(pageSize: Int) = if (pageSize <= 0) 1 else pageSize

    fun getTotalPages(totalCount: Long, pageSize: Int): Long {
        val result = ceil(totalCount.toDouble() / electPageSize(pageSize)).toLong()
        return electResult(result)
    }

    fun getCurrentPage(skip: Int = 0, pageSize: Int = 1) : Long {
        val result = (skip / electPageSize(pageSize)).toLong() + 1
        return electResult(result)
    }

    private fun electResult(toLong: Long) = if (toLong == 0L) 1 else toLong

}