package org.sc.util

import org.springframework.stereotype.Component
import java.util.*

@Component
class StringIdGenerator {

    private val stringLength = 10
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun generate() =
        (1..stringLength)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString(Date().toInstant().toString())


}