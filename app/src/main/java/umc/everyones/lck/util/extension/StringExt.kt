package umc.everyones.lck.util.extension

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

// 카테고리 -> 포지션 확장함수
fun String.toCategoryPosition(): Int{
    return when (this) {
        "잡담" -> 0
        "응원" -> 1
        "FA" -> 2
        "거래" -> 3
        "질문" -> 4
        "후기" -> 5
        else -> 0
    }
}

fun String.combineNicknameAndTeam(team: String): String{
    return if(team == "empty") this else "$this | $team"
}
fun String.toReadDateFormat(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    val partyDateFormatter = DateTimeFormatter.ofPattern("yy.MM.dd hh:mm")

    return LocalDateTime.parse(this, formatter).format(partyDateFormatter).toString()
}

fun String.toLocalDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy / MM / dd | HH:mm")
    return LocalDateTime.parse(this, formatter).toString()
}

fun String.toWriteViewingPartyDateFormat(): String {
    return this.split(" ").run {
        "20${this[0].replace(".", " / ")} | ${this[1].trim()}"
    }
}

fun String.toListViewingPartyDateFormat(): String{
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    val partyDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    return LocalDateTime.parse(this, formatter).format(partyDateFormatter).toString()
}

fun formatMatchTitle(season: String, matchNumber: Int): String {
    val suffix = when (matchNumber % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
    return "$season LCK ${matchNumber}${suffix} Match"
}


fun String.toTimeFormat(): String{
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    val partyDateFormatter = DateTimeFormatter.ofPattern("HH:mm")

    return LocalDateTime.parse(this, formatter).format(partyDateFormatter).toString()
}


@SuppressLint("SimpleDateFormat")
fun String.toCalendar(): Calendar {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val date = sdf.parse(this)
    return Calendar.getInstance().apply {
        time = date!!
    }
}
