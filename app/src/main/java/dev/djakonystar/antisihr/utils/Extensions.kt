package dev.djakonystar.antisihr.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.djakonystar.antisihr.data.models.GenericResponse
import okhttp3.ResponseBody

fun <T> errorResponse(errorBody: ResponseBody?): GenericResponse<T> {
    val gson = Gson()
    val type = object : TypeToken<GenericResponse<T>>() {}.type
    return gson.fromJson(errorBody?.charStream(), type)
}
