package org.sc.controller

import org.junit.Assert.*
import org.junit.Test

class ControllerPaginationTest {

    @Test
    fun `shall return correct number of pages`() {
        assertEquals(1, ControllerPagination.getTotalPages(0, 1))
        assertEquals(10, ControllerPagination.getTotalPages(10, 1))
        assertEquals(2, ControllerPagination.getTotalPages(10, 5))
        assertEquals(4, ControllerPagination.getTotalPages(10, 3))
        assertEquals(167, ControllerPagination.getTotalPages(1000, 6))
    }

    @Test
    fun `shall return correct page number`() {
        assertEquals(1, ControllerPagination.getCurrentPage(0, 5))
        assertEquals(2, ControllerPagination.getCurrentPage(11, 10))
        assertEquals(3, ControllerPagination.getCurrentPage(20, 10))
        assertEquals(3, ControllerPagination.getCurrentPage(29, 10))
    }

}