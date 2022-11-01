package me.acayrin.assignment.activity.hooks

import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import me.acayrin.assignment.R

object PreRender {
    @JvmStatic
    fun render(context: Context) {
        val window = (context as Activity).window
        window.statusBarColor = ContextCompat.getColor(context, R.color.c1)
        window.navigationBarColor = ContextCompat.getColor(context, android.R.color.transparent)
    }
}