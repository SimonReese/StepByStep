package it.project.appwidget

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.text.DecimalFormat

/**
 * classe che disegna il grafico a barre
 */

class BarChart(context: Context, attrs: AttributeSet): View(context, attrs) {

    // TODO: A che servono? Rimuovere dopo aver compreso dall'esempio
    var mShowText: Boolean

    var mTextPos: Int

    /**
     * Array di etichette da applicare lungo l'asse x
     */
    var days: ArrayList<String> = arrayListOf("LUN", "MAR", "MER", "GIO", "VEN", "SAB", "DOM")
        set(newArray) {
            if (newArray.size != this.valueArray.size)
                return
            field = newArray
            invalidate()
            requestLayout()
        }

    /**
     * Array di valori da rappresentare
     */
    var valueArray: ArrayList<Double> = arrayListOf(10.0, 20.0, 70.0, 30.0, 60.0, 40.0, 50.0)
        set(newArray) {
            if (newArray.size != this.valueArray.size)
                return
            field = newArray
            invalidate()
            requestLayout()
        }

    // Oggetti paint per definire le proprietà del disegno
    private val barPaint: Paint
    private val textPaint: Paint

    init {
        //Leggo gli attributi definiti per la classe dal file strings.xml
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0)


        val nightModeFlags = getContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        typedArray.apply {
            try {
                mShowText = this.getBoolean(R.styleable.PieChart_showText, false)
                mTextPos = this.getInteger(R.styleable.PieChart_labelPosition, 0)
            }
            finally {
                recycle()
            }
        }


        //Impostazioni oggetto paint
        barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        barPaint.apply {
            color = Color.CYAN
        }

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.apply {
            when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> color = Color.WHITE
                Configuration.UI_MODE_NIGHT_NO -> color =Color.BLACK
                Configuration.UI_MODE_NIGHT_UNDEFINED -> color = Color.BLACK
            }
            textAlign = Paint.Align.LEFT
            textSize = 35f
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val lowerMargin = 200f
        val upperMargin = 400f

        //Numero di barre
        var numbars = valueArray.size
        //Spazio a disposizione per disegnare ogni barra (comprende lo spazio vuoto attorno a sè)
        var space = width / numbars
        //Distanza del centro della barra dall'inizio dello spazio
        var relative_center = space / 2

        // Bordo inferiore con margine
        val bottom = height - lowerMargin

        //Cerco l'elemento più grande
        val maxValue: Float = valueArray.max().toFloat()

        // 623.0, 723.0, 6159, 5873
        //$top, $bottom, $right, $left
        //canvas?.drawRect(0 + 20F, 0 + 20F, width -20F, height -20F, paint)

        //return

        //Per ogni barra
        for ((position, value) in valueArray.withIndex()){
            //Calcolo la posizione assoluta del i-esimo centro della barra rispetto alla width della view
            var abs_center = relative_center + (position * space)
            //Coordinata del bordo sinistro (metà della distanza tra centro e inizio spazio)
            var left = abs_center - space/4
            //Coordinata del bordo destro della barra (a metà distanza tra centro e fine spazio)
            var right = abs_center + space/4

            //Calcolo il rapporto tra il valore e l'elemento più grande del vettore
            val scale: Double = value / maxValue

            // Per calcolare l'altezza, parto da bottom e sottraggo height*scale
            var top = bottom - (height - upperMargin)*scale

            Log.d("Canvas", "$position: $top, $bottom, $right, $left")
            //Disegno rettangolo della barra tramite bordi sinistro, superiore, destro, inferiore
            canvas?.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom, barPaint)
            if (value != 0.0) {
                canvas?.drawText(DecimalFormat("#.#km").format(value), left.toFloat(), top.toFloat(), textPaint)

            }
            canvas?.drawText(days[position], left.toFloat(), bottom + 50, textPaint)
        }

    }


}