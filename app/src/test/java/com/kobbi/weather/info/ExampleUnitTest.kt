package com.kobbi.weather.info

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun replace_isCorrect() {
        val dateTime = "2019-08-31 18:00"
        val result = Regex("\\D").replace(dateTime, "")
        assertEquals(result, "201908311800")
    }

    @Test
    fun substring_isCorrect() {
        val string = "201909101600"
        val result = string.substring(0..5)
        assertEquals(result, "201909")
    }
}
