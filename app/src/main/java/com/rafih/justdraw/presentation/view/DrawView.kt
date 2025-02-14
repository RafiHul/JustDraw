package com.rafih.justdraw.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.rafih.justdraw.tools.Brush
import com.rafih.justdraw.tools.Eraser
import com.rafih.justdraw.tools.Tools
import com.rafih.justdraw.util.DrawTool
import java.util.Stack

class DrawView: View {

    private val mainTool = MainTool(Brush(),Eraser())
    private var currentTool: Tools = mainTool.brush //default first tool used

    private lateinit var drawPath: Path
    private lateinit var myPaint: Paint
    private lateinit var myBitmap: Bitmap
    private var myCanvas: Canvas? = null

    private val undoStack = Stack<Bitmap>()
    private val redoStack = Stack<Bitmap>()
    private var previousBrushColor = defaultBrushColor
    private var onDrawCircleToolSizeIndicator = false

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
        myPaint = currentTool
        drawPath = Path()
        setBackgroundColor(Color.WHITE)
    }

    //kalo mau menggambar sesuatu lakukan di ondraw, jangan di luarnya
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = (height / 2f) + (width / 4f)

        canvas.drawBitmap(myBitmap, 0f, 0f, myPaint)

        if(onDrawCircleToolSizeIndicator){ //draw circle tool size indicator
            canvas.drawCircle(centerX,centerY,currentTool.toolsSize, circleOutline)
            onDrawCircleToolSizeIndicator = false
        }
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
        if(currentTool is Brush){ // TODO: do it with != Eraser
            currentTool.color = colorCode
        }
    }

    fun changeToolSize(value: Float){
        currentTool.toolsSize = value
        onDrawCircleToolSizeIndicator = true // untuk menggambar indicator lingkaran ukuran/size dari tool
        invalidate()
    }

    fun changeUseTool(tool: DrawTool){
        when(tool){
            DrawTool.BRUSH -> {
                // TODO: tambahkan width/size nya juga
                myPaint = mainTool.brush.apply {
                    color = previousBrushColor
                }
                currentTool = mainTool.brush
            }
            DrawTool.ERASER -> {
                previousBrushColor = myPaint.color
                myPaint = mainTool.eraser
                currentTool = mainTool.eraser
            }
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

    companion object{
        private val defaultBrushColor = Color.BLACK
        private val defaultToolSize = 5f
        private val circleOutline = Paint().apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Style.STROKE
            isAntiAlias = true
        }
    }

    data class MainTool(val brush: Brush, val eraser: Eraser)
}