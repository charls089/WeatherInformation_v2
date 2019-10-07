package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.Index


/**
 * @param tmFc      발표시각(년월일시분)
 * @param tmSeq     발표번호(월별)
 * @param tmEf      특보발효현황 시각(년월일시분)
 * @param t6        특보발효현황 내용
 * @param t7        예비특보 발효현황
 * @param other     참고사항
 *
 */
@Entity(
    tableName = "SpecialNews",
    primaryKeys = ["key"],
    indices = [Index("tmFc", "tmSeq")]
)
data class SpecialNews(
    val key: String,
    val tmFc: Long,
    val tmSeq: Int,
    val tmEf: Long,
    val t6: String,
    val t7: String,
    val other: String
)