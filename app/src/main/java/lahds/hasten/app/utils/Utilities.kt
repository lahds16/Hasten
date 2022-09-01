package lahds.hasten.app.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import lahds.hasten.app.HastenApp
import kotlin.math.ceil

object Utilities {
    var density = 1f

    fun dp(value: Float): Int {
        return if (value == 0f) {
            0
        } else ceil((density * value).toDouble()).toInt()
    }

    fun transition(view: View) {
        val autoTransition = AutoTransition()
        autoTransition.duration = 200L
        TransitionManager.beginDelayedTransition(view as ViewGroup?, autoTransition)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun animateClick(view: View) {
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate("scaleX", 1.2F, 100)
                    view.animate("scaleY", 1.2F, 100)
                }
                MotionEvent.ACTION_UP -> {
                    view.animate("scaleX", 1.0F, 100)
                    view.animate("scaleY", 1.0F, 100)
                }
            }
            false
        }
    }

    private fun View.animate(propertyName: String, value: Float, duration: Long) {
        val anim = ObjectAnimator()
        anim.target = this
        anim.setPropertyName(propertyName)
        anim.setFloatValues(value)
        anim.duration = duration
        anim.start()
    }

    fun isDarkTheme(): Boolean {
        return when (HastenApp.applicationContext.resources?.configuration?.uiMode?.and(
            Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    fun defaultLightTheme(): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        map["background"] = "#FFFFFF"
        map["toolbar"] = "#FFFFFF"
        map["toolbarTitle"] = "#333333"
        map["title"] = "#222222"
        map["subtitle"] = "#8B8D8F"
        map["toolbarIcon"] = "#404E56"
        map["icon"] = "#8E959B"
        map["primary"] = "#2196F3"
        map["receiverText"] = "#424242"
        map["receiverMessage"] = "#EEEEEE"
        return map
    }

    fun defaultDarkTheme(): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        map["background"] = "#1B1C1F"
        map["toolbar"] = "#272A31"
        map["toolbarTitle"] = "#FFFFFF"
        map["title"] = "#FFFFFF"
        map["subtitle"] = "#7D8B99"
        map["toolbarIcon"] = "#FFFFFF"
        map["icon"] = "#738190"
        map["primary"] = "#2196F3"
        map["receiverText"] = "#FFFFFF"
        map["receiverMessage"] = "#272A31"
        return map
    }
}