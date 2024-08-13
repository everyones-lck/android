package umc.everyones.lck.util.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    return "$this | $team"
}
fun String.toReadViewingPartyDateFormat(): String {
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