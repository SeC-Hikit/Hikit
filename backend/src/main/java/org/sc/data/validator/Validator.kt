package org.sc.data.validator

interface Validator<T> {
    fun validate(request: T): Set<String>
}