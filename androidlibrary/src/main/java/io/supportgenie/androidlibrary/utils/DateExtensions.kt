package io.supportgenie.androidlibrary.utils

import android.content.Context
import androidx.core.os.ConfigurationCompat
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun getDbDateFromApiDate(strDateTime:String) : Long {
    // todo - we might have to add special handling for more precision microsecs .SSSSSS instead of .SSS
    // its asssumed the format is like 2018-10-12T06:00:10.911+0000
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    var date: Date

    if (strDateTime != "") {
        date = format.parse(strDateTime)
    } else {
        date = Date()
    }
    return date.getTime()
}

// from https://javarevisited.blogspot.com/2012/12/how-to-convert-millisecond-to-date-in-java-example.html
fun formatDateTime(dateTime:Long, context: Context):String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = dateTime
    val currentLocale = ConfigurationCompat.getLocales(context.resources.configuration)[0]
    val formatter = DateFormat.getDateTimeInstance(
        DateFormat.MEDIUM,
        DateFormat.SHORT,
        currentLocale
    )
    return formatter.format(cal.time)
}

fun getCurTimeInMillisecs() : Long {
    return Date().getTime()
}

