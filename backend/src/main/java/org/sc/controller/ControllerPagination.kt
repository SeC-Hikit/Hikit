package org.sc.controller

import org.springframework.stereotype.Component
import kotlin.math.ceil

@Component
object ControllerPagination {

    fun electPageSize(pageSize: Int) = if (pageSize <= 0) 1 else pageSize

    fun getTotalPages(totalCount: Long, pageSize: Int): Long {
        val toLong = ceil(totalCount.toDouble() / electPageSize(pageSize)).toLong()
        return if (toLong == 0L) 1 else toLong
    }

    fun getCurrentPage(skip: Int = 0, pageSize: Int = 1) : Long {
        return (skip / electPageSize(pageSize)).toLong()
    }

}