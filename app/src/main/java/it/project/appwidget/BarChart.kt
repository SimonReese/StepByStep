package it.project.appwidget

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.text.DecimalFormat

/**
 * Classe che implementa un basilare grafico a barre. Viene utilizzato un [Canvas] per disegnare
 * rettangoli sullo schermo, scalati rispetto ad un certo valore massimo.
 * @property days Lista di etichette da applicare alla barra orizziontale del grafico.
 * @property valueArray Lista di valori da graficare tramite barre.
 */
class BarChart(context: Context, attrs: AttributeSet?): View(context, attrs) {

    // TODO: A che servono? Rimuovere dopo aver compreso dall'esempio
    private var mShowText: Boolean

    private var mTextPos: Int

    /**
     * Array di etichette da applicare lungo l'asse x.
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
     * Array di valori da rappresentare tramite barre.
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val lowerMargin = 50f
        val upperMargin = 100f

        //Numero di barre
        val numbars = valueArray.size
        //Spazio a disposizione per disegnare ogni barra (comprende lo spazio vuoto attorno a sè)
        val space = width / numbars
        //Distanza del centro della barra dall'inizio dello spazio
        val relative_center = space / 2

        // Bordo inferiore con margine
        val bottom = height - lowerMargin

        println(width)
        println(height)


        //Cerco l'elemento più grande
        val maxValue: Float = valueArray.max().toFloat()

        // 623.0, 723.0, 6159, 5873
        //$top, $bottom, $right, $left
        //canvas?.drawRect(0 + 20F, 0 + 20F, width -20F, height -20F, paint)

        //return

        //Per ogni barra
        for ((position, value) in valueArray.withIndex()){
            //Calcolo la posizione assoluta del i-esimo centro della barra rispetto alla width della view
            val abs_center = relative_center + (position * space)
            //Coordinata del bordo sinistro (metà della distanza tra centro e inizio spazio)
            val left = abs_center - space/4
            //Coordinata del bordo destro della barra (a metà distanza tra centro e fine spazio)
            val right = abs_center + space/4

            var scale: Double = 0.0
            if(maxValue != 0f) {
                //Calcolo il rapporto tra il valore e l'elemento più grande del vettore
                scale = value / maxValue
            }

            // Per calcolare l'altezza, parto da bottom e sottraggo height*scale
            val top = bottom - (height - upperMargin)*scale

            Log.d("BarChart", "$position: $top, $bottom, $right, $left")
            //Disegno rettangolo della barra tramite bordi sinistro, superiore, destro, inferiore
            canvas?.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom, barPaint)
            if (value != 0.0) {
                canvas?.drawText(DecimalFormat("#.#km").format(value), left.toFloat(), top.toFloat(), textPaint)

            }
            canvas?.drawText(days[position], left.toFloat(), bottom + 50, textPaint)
        }

    }

    /**
     * Metodo che restituisce l'immagine del grafico a barre.
     * @return Bitmap rappresentante il grafico a barre.
     */
    fun getChartImage(width: Int = 900, height: Int = 850, color: Int = Color.CYAN, label: String = "km"): Bitmap {
        // Aggiorno painter della classe
        barPaint.color = color
        // Creo una bitmap vuota
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // Creo un canvas sulla bitmap
        val canvas = Canvas(bitmap)
        // Disegno la view sul canvas
        onDrawBit(canvas, width, height, label)
        // Restituisco la bitmap
        return bitmap
    }


    private fun onDrawBit(canvas: Canvas, w: Int, h: Int, label: String) {
        val lowerMargin = 50f
        val upperMargin = -700f
        //Numero di barre
        val numbars = valueArray.size
        //Spazio a disposizione per disegnare ogni barra (comprende lo spazio vuoto attorno a sè)
        val space = w / numbars
        //Distanza del centro della barra dall'inizio dello spazio
        val relative_center = space / 2

        // Bordo inferiore con margine
        val bottom = h - lowerMargin

        // Formattazione
        val singleDecimal = DecimalFormat("#.#$label")

        //Cerco l'elemento più grande
        val maxValue: Float = valueArray.max().toFloat()

        // 623.0, 723.0, 6159, 5873
        //$top, $bottom, $right, $left
        //canvas?.drawRect(0 + 20F, 0 + 20F, width -20F, height -20F, paint)

        //return

        //Per ogni barra
        for ((position, value) in valueArray.withIndex()){
            //Calcolo la posizione assoluta del i-esimo centro della barra rispetto alla width della view
            val abs_center = relative_center + (position * space)
            //Coordinata del bordo sinistro (metà della distanza tra centro e inizio spazio)
            val left = abs_center - space/4
            //Coordinata del bordo destro della barra (a metà distanza tra centro e fine spazio)
            val right = abs_center + space/4

            var scale = 0.0
            if(maxValue != 0f) {
                //Calcolo il rapporto tra il valore e l'elemento più grande del vettore
                scale = value / maxValue
            }

            // Per calcolare l'altezza, parto da bottom e sottraggo height*scale
            val top = bottom - (height - upperMargin)*scale

            //Disegno rettangolo della barra tramite bordi sinistro, superiore, destro, inferiore
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom, barPaint)
            if (value != 0.0) {
                canvas.drawText(singleDecimal.format(value), left.toFloat(), top.toFloat(), textPaint)

            }
            canvas.drawText(days[position], left.toFloat(), bottom + 50, textPaint)
        }

    }


}