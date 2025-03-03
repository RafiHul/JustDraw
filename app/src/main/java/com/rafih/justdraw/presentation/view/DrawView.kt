package com.rafih.justdraw.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageButton
import com.google.android.material.slider.Slider
import com.rafih.justdraw.tools.Brush
import com.rafih.justdraw.tools.Eraser
import com.rafih.justdraw.tools.FillColor
import com.rafih.justdraw.tools.ShapeTool
import com.rafih.justdraw.tools.Tools
import com.rafih.justdraw.util.MainDrawTool
import com.rafih.justdraw.util.SecDrawTool
import java.util.Stack
import kotlin.math.max
import kotlin.math.min

class DrawView: View {

    private val mainTool = MainToolInit()
    private val secTool = SecToolInit()
    private val touchIndicator = TouchIndicator(x = null, y = null)
    private var currentMainTool: Tools = mainTool.brush //default first main tool used
    private var currentSecTool: Tools? = null //default first sec tool used / null

    private lateinit var scaleGesture: ScaleGestureDetector
    private lateinit var drawPath: Path
    private lateinit var myPaint: Paint
    private lateinit var myBitmap: Bitmap
    private var myCanvas: Canvas? = null
    private val bitmapMatrix = Matrix()

    private var scaleFactor = 1f
    private var maxZoomIn = 10.0f
    private var maxZoomOut = 0.1f

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
        myPaint = currentMainTool
        drawPath = Path()
        setBackgroundColor(Color.WHITE)
        scaleGesture = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = max(maxZoomOut, min(scaleFactor, maxZoomIn))  //between 0.1f and 10.0f
                applyZoom()
                return true
            }
        })
    }

//    fun zoomIn() {
//        if (scaleFactor < maxScale) {
//            scaleFactor *= 1.2f
//            applyZoom()
//        }
//    }
//
//    fun zoomOut() {
//        if (scaleFactor > minScale) {
//            scaleFactor /= 1.2f
//            applyZoom()
//        }
//    }

    private fun applyZoom() {
        bitmapMatrix.reset()
        bitmapMatrix.postScale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        invalidate()
    }

    //kalo mau menggambar sesuatu lakukan di ondraw, jangan di luarnya
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = (height / 2f) + (width / 4f)

        //main draw
        canvas.drawBitmap(myBitmap, bitmapMatrix, myPaint)

        if (currentSecTool is ShapeTool) {
            val shapeTool = currentSecTool as ShapeTool
            if (shapeTool.isCurrentlyDrawing()) {

                shapeTool.drawShapePreview(canvas)

                val dimensions = shapeTool.getCurrentDimensions()
                canvas.drawText(dimensions, 20f, 60f, textPaint)
            }
        }

        //indicator lingkaran ketika user memilih size tool
        // TODO: edit this later
        if(onDrawCircleToolSizeIndicator){
            canvas.drawCircle(centerX,centerY,currentMainTool.toolsSize, circleOutline)
            onDrawCircleToolSizeIndicator = false
        }

        //indicator lingkaran ketika user menggambar di layar
        val touchIndicatorX = touchIndicator.x
        val touchIndicatorY = touchIndicator.y
        if (touchIndicatorX != null && touchIndicatorY != null && currentSecTool == null) {
            canvas.drawCircle(
                touchIndicatorX,
                touchIndicatorY,
                currentMainTool.toolsSize,
                circleOutline
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        myBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888)
        myCanvas = Canvas(myBitmap)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGesture.onTouchEvent(event)
        val x = event.x
        val y = event.y

        if (currentSecTool is ShapeTool) { //untuk membuat shape
            val shapeTool = currentSecTool as ShapeTool
            val result = shapeTool.onTouchEvent(event) //change toucheventlistener
            if (event.action == MotionEvent.ACTION_UP) {
                saveBitmapForUndo()

                shapeTool.drawShapeOnCanvas(myCanvas)
            }

            invalidate()
            return result
        }

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
//                drawPath = Path()
                saveBitmapForUndo()
                touchIndicator.setTouchIndicatorPosition(x,y)

                //add other validation
                if(currentSecTool is FillColor){
                    val fillTool = currentSecTool as FillColor
                    fillTool.floodFill(x.toInt(), y.toInt(), myBitmap)
                    invalidate()
                    return true
                }

                drawPath.moveTo(x,y)
                drawPath.lineTo(x,y)
                myCanvas!!.drawPath(drawPath,myPaint)
            }
            MotionEvent.ACTION_MOVE -> {
                touchIndicator.setTouchIndicatorPosition(x,y)

                if (currentSecTool !is FillColor){
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
        currentSecTool?.color = colorCode

        if(currentMainTool is Eraser){ //eraser cannot change color
            mainTool.brush.color = colorCode
            return
        }

        currentMainTool.color = colorCode

    }

    fun changeToolSize(value: Float){
        currentMainTool.toolsSize = value
        onDrawCircleToolSizeIndicator = true // untuk menggambar indicator lingkaran ukuran/size dari tool
        invalidate()
        // TODO: bugs when tools change, show tools size indicator
    }

    fun getCurrentToolSize(): Float {
        return currentMainTool.toolsSize
    }

    fun changeMainUseTool(tool: MainDrawTool){
        when(tool){
            MainDrawTool.BRUSH -> {
                changeMainToolDrawProperty(mainTool.brush)
            }
            MainDrawTool.ERASER -> {
                changeMainToolDrawProperty(mainTool.eraser)
            }
        }
    }

    fun changeSecUseTool(tool: SecDrawTool, imageButton: ImageButton, sliderSize: Slider ,selectedBackgroundColor: Int){

        //view clicked before
        if (currentSecTool != null){
            changeMainToolDrawProperty(currentMainTool)
            imageButton.setBackgroundColor(Color.WHITE) //set background button to default
            currentSecTool = null
            sliderSize.visibility = VISIBLE
            return
        }

        //view never clicked
        when(tool){
            SecDrawTool.FILLCOLOR -> {
                changeSecToolDrawProperty(secTool.fillColor)
                imageButton.setBackgroundColor(selectedBackgroundColor)
                sliderSize.visibility = GONE
            }
            SecDrawTool.SHAPE -> {
                changeSecToolDrawProperty(secTool.shape)
                imageButton.setBackgroundColor(selectedBackgroundColor)
                sliderSize.visibility = GONE
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

    private fun changeMainToolDrawProperty(nTool: Tools){
        myPaint = nTool
        currentMainTool = nTool
    }

    private fun changeSecToolDrawProperty(nTool: Tools){
        myPaint = nTool
        currentSecTool = nTool //set current sectool to fillcolor
        currentSecTool!!.color = currentMainTool.color //transfer main tool color to sec tool
    }

    companion object{
        private val circleOutline = Paint().apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Style.STROKE
            isAntiAlias = true
        }
        private val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 30f
            style = Style.FILL
        }
    }

    data class MainToolInit(val brush: Brush = Brush(), val eraser: Eraser = Eraser())
    data class SecToolInit(val fillColor: FillColor = FillColor(), val shape: ShapeTool = ShapeTool())
    data class TouchIndicator(var x: Float?, var y: Float?){
        fun setTouchIndicatorPosition(nx: Float?,ny: Float?){
            x = nx
            y = ny
        }
    }
}