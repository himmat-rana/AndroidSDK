package io.supportgenie.androidlibrary.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

// helper function to parse a json dictionary to a class
fun <T>parseJsonValue(data: Map<String, Any?>, type: Type) : T? {
    val moshi = Moshi.Builder().build()
    val adapter: JsonAdapter<T> = moshi.adapter(type)
    return adapter.fromJsonValue(data)
}
