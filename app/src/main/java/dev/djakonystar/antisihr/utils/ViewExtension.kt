package dev.djakonystar.antisihr.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dev.djakonystar.antisihr.app.App

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun toast(msg: String) {
    Toast.makeText(App.instance, msg, Toast.LENGTH_SHORT).show()
}

fun showSnackBar(view: View, msg: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(view, msg, length).show()
}

fun ImageView.setImageWithGlide(context: Context, url: String) {
    Glide.with(context).load(url).into(this)
}

val String.toPhoneNumber: String
    get() {
        val arr = this.toCharArray()
        var phone = "+7 ("
        arr.forEachIndexed { index, c ->
            if (index != 0) phone += c
            if (index == 3) {
                phone += ") "
            }
            if (index == 6 || index == 8) {
                phone += "-"
            }
        }
        return phone
    }

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

