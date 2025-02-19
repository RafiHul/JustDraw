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
import com.rafih.justdraw.tools.FillColor
import com.rafih.justdraw.tools.Tools
import com.rafih.justdraw.util.MainDrawTool
import com.rafih.justdraw.util.SecDrawTool
import java.util.LinkedList
import java.util.Queue
import java.util.Stack

class DrawView: View {

    private val mainTool = MainToolInit()
    private val secTool = SecToolInit()
    private val touchIndicator = TouchIndicator(null,null)
    var currentTool: Tools = mainTool.brush //default first tool used
    private var previousTools: Tools? = null //to save previous tool used

    private lateinit var drawPath: Path
    private lateinit var myPaint: Paint
    private lateinit var myBitmap: Bitmap
    private var myCanvas: Canvas? = null

    private val undoStack = Stack<Bitmap>()
    private val redoStack = Stack<Bitmap>()
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

        //main draw
        canvas.drawBitmap(myBitmap, 0f, 0f, myPaint)

        //indicator lingkaran ketika user menggambar di layar
        val touchIndicatorX = touchIndicator.x
        val touchIndicatorY = touchIndicator.y
        if (touchIndicatorX != null && touchIndicatorY != null){
            canvas.drawCircle(touchIndicatorX,touchIndicatorY,currentTool.toolsSize,circleOutline)
        }

        //indicator lingkaran ketika user memilih size tool
        if(onDrawCircleToolSizeIndicator){
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
                touchIndicator.setTouchIndicatorPosition(x,y)

                if(currentTool is FillColor){
                    floodFill(x,y)
                } else {
                    drawPath.moveTo(x,y)
                    drawPath.lineTo(x,y)
                    myCanvas!!.drawPath(drawPath,myPaint)
                }

            }
            MotionEvent.ACTION_MOVE -> {
                touchIndicator.setTouchIndicatorPosition(x,y)

                if (currentTool !is FillColor){
                    drawPath.lineTo(x,y)
                    myCanvas!!.drawPath(drawPath,myPaint) //draw path to bitmap from canvas
                }

            }
            MotionEvent.ACTION_UP -> {
                touchIndicator.setTouchIndicatorPosition(null,null)
                drawPath = Path()
            }
        }

        invalidate()
        return true
    }

    fun changeColor(colorCode: Int){
        if(currentTool !is Eraser){ //if not eraser
            currentTool.color = colorCode
        }
    }

    fun changeToolSize(value: Float){
        currentTool.toolsSize = value
        onDrawCircleToolSizeIndicator = true // untuk menggambar indicator lingkaran ukuran/size dari tool
        invalidate()
        // TODO: bugs when tools change, show tools size indicator
    }

    fun changeMainUseTool(tool: MainDrawTool){
        when(tool){
            MainDrawTool.BRUSH -> {
                myPaint = mainTool.brush
                currentTool = mainTool.brush
            }
            MainDrawTool.ERASER -> {
                myPaint = mainTool.eraser
                currentTool = mainTool.eraser
            }
        }

        previousTools = null
    }

    fun changeSecUseTool(tool: SecDrawTool): Boolean {

        //view clicked before
        if (previousTools != null){
            myPaint = previousTools!!
            currentTool = previousTools!!
            return true
        }

        previousTools = currentTool

        when(tool){
            SecDrawTool.FILLCOLOR -> {
                myPaint = secTool.fillColor
                currentTool = secTool.fillColor
            }
        }
        //view never clicked
        return false
    }


    // TODO: Not optimized very slowwwwwwwwwwwww
    private fun floodFill(x: Float,y: Float){
        val newColor = currentTool.color

        if (x < 0 || x >= myBitmap.width || y < 0 || y >= myBitmap.height){
            return
        }

        val targetColor = myBitmap.getPixel(x.toInt(),y.toInt())
        if (targetColor == newColor){
            return
        }

        val queue: Queue<Pair<Float, Float>> = LinkedList()
        queue.add(Pair(x,y))

        while (queue.isNotEmpty()){
            val (px,py) = queue.poll()

            if (px < 0 || px >= myBitmap.width || py < 0 || py >= myBitmap.height || myBitmap.getPixel(px.toInt(),py.toInt()) != targetColor){
                continue
            }

            myBitmap.setPixel(px.toInt(),py.toInt(),newColor)

            queue.add(Pair(px+1,py))
            queue.add(Pair(px-1,py))
            queue.add(Pair(px,py+1))
            queue.add(Pair(px,py-1))
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
        private val circleOutline = Paint().apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Style.STROKE
            isAntiAlias = true
        }
    }

    data class MainToolInit(val brush: Brush = Brush(), val eraser: Eraser = Eraser())
    data class SecToolInit(val fillColor: FillColor = FillColor())

    data class TouchIndicator(var x: Float?, var y: Float?){
        fun setTouchIndicatorPosition(nx: Float?,ny: Float?){
            x = nx
            y = ny
        }
    }
}