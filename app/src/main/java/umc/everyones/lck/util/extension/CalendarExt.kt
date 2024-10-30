package umc.everyones.lck.util.extension

import android.annotation.SuppressLint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar

fun getFormattedPreviousDay(calendar: Calendar): String {
    return Calendar.getInstance().apply {
        Timber.d("this time ${this.time}")
        time = calendar.time; add(Calendar.DATE, -1)
    }.getFormattedDate()
}

fun getFormattedNextDay(calendar: Calendar): String {
    return Calendar.getInstance().apply {
        time = calendar.time; add(Calendar.DATE, 1)
    }.getFormattedDate()
}

@SuppressLint("SimpleDateFormat")
fun Calendar.getFormattedDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    return dateFormat.format(this.time)
}