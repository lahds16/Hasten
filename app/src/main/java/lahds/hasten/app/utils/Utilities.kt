package lahds.hasten.app.utils

import android.animation.ObjectAnimator
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

object Utilities {
    fun transition(view: View) {
        val autoTransition = AutoTransition()
        autoTransition.duration = 200L
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