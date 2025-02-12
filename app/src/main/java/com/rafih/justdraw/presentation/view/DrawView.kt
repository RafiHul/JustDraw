package com.rafih.justdraw.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Xfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.Stack

class DrawView: View {

    private lateinit var drawPath: Path
    private lateinit var myPaint: Paint
    private lateinit var myBitmap: Bitmap
    private var myCanvas: Canvas? = null

    private val undoStack = Stack<Bitmap>()
    private val redoStack = Stack<Bitmap>()
    private var previousBrushColor = defaultBrushColor
    var isErased = false

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
        setBackgroundColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(myBitmap, 0f, 0f, myPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        myBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888)
        myCanvas = Canvas(myBitmap)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        val x = event!!.x
        val y = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
//                drawPath = Path()
                saveBitmapForUndo()
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

    fun changeColor(colorCode: Int){
        if(!isErased){
            previousBrushColor = myPaint.color
            myPaint.color = colorCode
        }
    }

    private fun saveBitmapForUndo(){
        val bitmap = Bitmap.createBitmap(myBitmap)
        undoStack.push(bitmap)

        redoStack.clear()
    }

    fun undo(){
        if (undoStack.isNotEmpty()){

            redoStack.push(Bitmap.createBitmap(myBitmap))

            myBitmap = undoStack.pop()
            myCanvas = Canvas(myBitmap)
            invalidate()
        }
    }

    fun redo(){
        if(redoStack.isNotEmpty()){

            undoStack.push(Bitmap.createBitmap(myBitmap))

            myBitmap = redoStack.pop()
            myCanvas = Canvas(myBitmap)
            invalidate()
        }
    }

    fun setErased(){
        if(!isErased){
            changeColor(Color.WHITE)
            myPaint.strokeWidth = defaultBrushSize // TODO: ini ganti pakai size penghapus
        } else {
            myPaint.color = previousBrushColor
            myPaint.strokeWidth = defaultBrushSize
        }

        isErased = !isErased
    }

    companion object{
        private val defaultBrushColor = Color.BLACK
        private val defaultBrushSize = 5f
    }
}