package com.rafih.justdraw.presentation.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawView: View {

    private var currentBrushColor = defaultBrushColor
    private var currentBrushSize = defaultBrushSize

    private val pathList = ArrayList<CustomPath>()
    private lateinit var drawPath: CustomPath
    private lateinit var myPaint: Paint

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
            strokeJoin = Paint.Join.ROUND
        }
        drawPath = CustomPath(defaultBrushColor,defaultBrushSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in pathList){
            myPaint.color = i.brushColor
            myPaint.strokeWidth = i.brushSize
            canvas.drawPath(i,myPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        val x = event!!.x
        val y = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                drawPath = CustomPath(currentBrushColor,currentBrushSize)
                drawPath.moveTo(x,y)
                pathList.add(drawPath)
            }

            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(x,y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                drawPath = CustomPath(currentBrushColor,currentBrushSize)
            }
        }

        return true
    }

    companion object{
        private val defaultBrushColor = Color.BLACK
        private val defaultBrushSize = 5f
    }

    private data class CustomPath(var brushColor: Int, var brushSize: Float): Path()
}