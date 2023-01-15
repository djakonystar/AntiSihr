package dev.djakonystar.antisihr.utils

import android.view.View
import android.widget.*
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
