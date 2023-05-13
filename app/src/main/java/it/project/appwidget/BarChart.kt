package it.project.appwidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class BarChart(context: Context, attrs: AttributeSet): View(context, attrs) {

    var mShowText: Boolean
        get() = mShowText

    var mTextPos: Int
        get() = mTextPos


    private var center_width: Float
    private var center_height: Float
    private val paint: Paint
    private var centered_square: RectF

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0)

        typedArray.apply {
            try {
                mShowText = this.getBoolean(R.styleable.PieChart_showText, false)
                mTextPos = this.getInteger(R.styleable.PieChart_labelPosition, 0)
            }
            finally {
                recycle()
            }
        }

        //Start drawing
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.apply {
            color = Color.CYAN
        }

        center_width = 0F
        center_height = 0F
        centered_square = RectF()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //Calculate center of view
        center_width = w.toFloat() / 2
        center_height = h.toFloat() / 2

        centered_square.apply {
            left = center_width - 500
            right = center_width + 500
            top = center_height - 500
            bottom = center_height + 500
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //canvas?.drawOval(centered_square, paint)

        var numbars = 5
        var space = width / numbars
        var relative_center = space / 2

        for (i in 0..4){
            var abs_center = relative_center + (i * space)
            var left = abs_center - space/4
            var right = abs_center + space/4
            var top = 600F + Random.nextInt(8) * 100
            canvas?.drawRect(left.toFloat(), top, right.toFloat(), height.toFloat()-200F, paint)
        }

    }


}