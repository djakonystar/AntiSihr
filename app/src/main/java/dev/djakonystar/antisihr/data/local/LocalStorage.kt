package dev.djakonystar.antisihr.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.djakonystar.antisihr.app.App
import dev.djakonystar.antisihr.utils.StringPreference
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LocalStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val pref: SharedPreferences =
            App.instance.getSharedPreferences("LocaleStorage", Context.MODE_PRIVATE)
    }

    val language by StringPreference(pref,"ru")

}