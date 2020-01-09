package com.kobbi.weather.info

import com.kobbi.weather.info.presenter.model.type.OfferType
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

    @Test
    fun array_check() {
        val array = Array(8) { i -> "${if (i * 3 < 10) "0" else ""}${i*3}" }
        print("array : ${array.toList()}")
    }

    @Test
    fun offerTypeCheck() {
        val isNeedToUpdate = OfferType.isNeedToUpdate(OfferType.LIFE_TIME)
        println("isNeedToUpdate : $isNeedToUpdate")

        val time = OfferType.getBaseDateTime(OfferType.LIFE_TIME)
        println("time : $time")
        println(time.first+time.second.dropLast(2))
    }
}
