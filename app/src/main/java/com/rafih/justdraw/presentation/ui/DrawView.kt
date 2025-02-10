package com.rafih.justdraw.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawView: View {

    private lateinit var drawPath: Path
    private lateinit var myPaint: Paint
    private lateinit var myBitmap: Bitmap
    private var myCanvas: Canvas? = null

    constructor(context: Context) : this(context, null) {
        setUpComponent()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        setUpComponent()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setUpComponent()
    }

    private fun setUpComponent(){
        myPaint = Paint().apply {
            color = defaultBrushColor
            strokeWidth = defaultBrushSize
            style = Paint.Style.STROKE
            isAntiAlias = true
            isFilterBitmap = true
            strokeJoin = Paint.Join.ROUND
        }
        drawPath = Path()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(myBitmap, 0f, 0f, myPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        myBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        myCanvas = Canvas(myBitmap)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        val x = event!!.x
        val y = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                drawPath = Path()
                drawPath.moveTo(x,y)
            }

            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(x,y)
                myCanvas!!.drawPath(drawPath,myPaint) //draw path to bitmap from canvas
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                drawPath = Path()
            }
        }

        return true
    }

    fun changeColor(){
        myPaint.color = Color.RED
    }

    companion object{
        private val defaultBrushColor = Color.BLACK
        private val defaultBrushSize = 5f
    }
}