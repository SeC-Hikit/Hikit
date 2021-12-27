package org.sc.util

import org.springframework.stereotype.Component
import java.util.*

@Component
class StringIdGenerator {

    private val stringLength = 30
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun generate(): String {
        val map = (1..stringLength)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
        return map.joinToString("")
    }



}