package dev.djakonystar.antisihr.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.djakonystar.antisihr.data.models.GenericResponse
import okhttp3.ResponseBody
import java.util.*

fun <T> errorResponse(errorBody: ResponseBody?): GenericResponse<T> {
    val gson = Gson()
    val type = object : TypeToken<GenericResponse<T>>() {}.type
    return gson.fromJson(errorBody?.charStream(), type)
}


fun setAppLocale(languageFromPreference: String?, context: Context) {
    if (languageFromPreference != null) {
        val resources: Resources = context.resources
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        config.setLocale(Locale(languageFromPreference.lowercase(Locale.ROOT)))
        resources.updateConfiguration(config, dm)
    }
}


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}