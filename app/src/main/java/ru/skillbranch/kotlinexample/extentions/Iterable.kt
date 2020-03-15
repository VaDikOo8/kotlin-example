package ru.skillbranch.kotlinexample.extentions

fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    val result = arrayListOf<T>()
    for (item in this) {
        if (predicate(item)) {
            return result
        } else {
            result.add(item)
        }
    }
    return result
}