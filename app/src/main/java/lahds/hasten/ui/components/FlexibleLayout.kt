package lahds.hasten.ui.components

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import lahds.hasten.R

class FlexibleLayout : RelativeLayout {
    private var viewPartMain: TextView? = null
    private var viewPartSlave: View? = null
    private var a: TypedArray? = null
    private var viewPartMainLayoutParams: LayoutParams? = null
    private var viewPartMainWidth = 0
    private var viewPartMainHeight = 0
    private var viewPartSlaveLayoutParams: LayoutParams? = null
    private var viewPartSlaveWidth = 0
    private var viewPartSlaveHeight = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        a = context.obtainStyledAttributes(attrs, R.styleable.FlexibleLayout, 0, 0)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try {
            viewPartMain = findViewById<View>(
                a!!.getResourceId(
                    R.styleable.FlexibleLayout_viewPartMain,
                    -1
                )
            ) as TextView
            viewPartSlave =
                findViewById(a!!.getResourceId(R.styleable.FlexibleLayout_viewPartSlave, -1))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (viewPartMain == null || viewPartSlave == null || widthSize <= 0) {
            return
        }

        val availableWidth = widthSize - paddingLeft - paddingRight
        viewPartMainLayoutParams = viewPartMain!!.layoutParams as LayoutParams
        viewPartMainWidth =
            viewPartMain!!.measuredWidth + viewPartMainLayoutParams!!.leftMargin + viewPartMainLayoutParams!!.rightMargin
        viewPartMainHeight =
            viewPartMain!!.measuredHeight + viewPartMainLayoutParams!!.topMargin + viewPartMainLayoutParams!!.bottomMargin
        viewPartSlaveLayoutParams = viewPartSlave!!.layoutParams as LayoutParams?
        viewPartSlaveWidth =
            viewPartSlave!!.measuredWidth + viewPartSlaveLayoutParams!!.leftMargin + viewPartSlaveLayoutParams!!.rightMargin
        viewPartSlaveHeight =
            viewPartSlave!!.measuredHeight + viewPartSlaveLayoutParams!!.topMargin + viewPartSlaveLayoutParams!!.bottomMargin

        val viewPartMainLineCount = viewPartMain!!.lineCount
        val viewPartMainLastLineWidth: Float =
            if (viewPartMainLineCount > 0) viewPartMain!!.layout.getLineWidth(viewPartMainLineCount - 1) else 0f
        widthSize = paddingLeft + paddingRight

        var heightSize: Int = paddingTop + paddingBottom
        if (viewPartMainLineCount > 1 && viewPartMainLastLineWidth + viewPartSlaveWidth < viewPartMain!!.measuredWidth) {
            widthSize += viewPartMainWidth
            heightSize += viewPartMainHeight
        } else if (viewPartMainLineCount > 1 && viewPartMainLastLineWidth + viewPartSlaveWidth >= availableWidth) {
            widthSize += viewPartMainWidth
            heightSize += viewPartMainHeight + viewPartSlaveHeight
        } else if (viewPartMainLineCount == 1 && viewPartMainWidth + viewPartSlaveWidth >= availableWidth) {
            widthSize += viewPartMain!!.measuredWidth
            heightSize += viewPartMainHeight + viewPartSlaveHeight
        } else {
            widthSize += viewPartMainWidth + viewPartSlaveWidth
            heightSize += viewPartMainHeight
        }
        setMeasuredDimension(widthSize, heightSize)

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (viewPartMain == null || viewPartSlave == null) {
            return
        }

        viewPartMain!!.layout(
            paddingLeft,
            paddingTop,
            viewPartMain!!.width + paddingLeft,
            viewPartMain!!.height + paddingTop
        )

        viewPartSlave!!.layout(
            right - left - viewPartSlaveWidth - paddingRight,
            bottom - top - paddingBottom - viewPartSlaveHeight,
            right - left - paddingRight,
            bottom - top - paddingBottom
        )
    }
}