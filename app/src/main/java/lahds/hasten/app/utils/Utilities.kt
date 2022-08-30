package lahds.hasten.app.utils

import android.animation.ObjectAnimator
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
        autoTransition.duration = 250L
        TransitionManager.beginDelayedTransition(view as ViewGroup?, autoTransition)
    }

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
                    view.performClick()
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
}