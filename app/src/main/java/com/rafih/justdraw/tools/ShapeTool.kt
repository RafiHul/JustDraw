package com.rafih.justdraw.tools

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.view.MotionEvent
import com.rafih.justdraw.util.ShapeToolType
import kotlin.math.abs
import kotlin.math.hypot

class ShapeTool: Tools() {

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var currentX: Float = 0f
    private var currentY: Float = 0f

    private var currentShape: ShapeToolType = ShapeToolType.RECTANGLE

    private val shapePath = Path()

    private var isDrawing = false

    private val shapeRect = RectF()

    fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Set start point
                startX = x
                startY = y
                currentX = x
                currentY = y
                isDrawing = true
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // Update current point
                currentX = x
                currentY = y
                return true
            }
            MotionEvent.ACTION_UP -> {
                // Finalize drawing
                isDrawing = false
                return true
            }
            else -> return false
        }
    }
    fun drawShapePreview(canvas: Canvas?) {
        if (!isDrawing || canvas == null) return

        // Draw shape based on type
        when (currentShape) {
            ShapeToolType.RECTANGLE -> {
                shapeRect.set(
                    minOf(startX, currentX),
                    minOf(startY, currentY),
                    maxOf(startX, currentX),
                    maxOf(startY, currentY)
                )
                canvas.drawRect(shapeRect, this)
            }
            ShapeToolType.CIRCLE -> {
                // Calculate radius based on distance
                val radius = hypot(
                    (currentX - startX).toDouble(),
                    (currentY - startY).toDouble()
                ).toFloat()
                canvas.drawCircle(startX, startY, radius, this)
            }
            ShapeToolType.TRIANGLE -> {
                shapePath.reset()
                shapePath.moveTo(startX, startY)
                shapePath.lineTo(currentX, currentY)
                shapePath.lineTo(startX - (currentX - startX), currentY)
                shapePath.close()
                canvas.drawPath(shapePath, this)
            }
            ShapeToolType.LINE -> {
                canvas.drawLine(startX, startY, currentX, currentY, this)
            }
        }
    }

    fun drawShapeOnCanvas(canvas: Canvas?) {
        when (currentShape) {
            ShapeToolType.RECTANGLE -> {
                shapeRect.set(
                    minOf(startX, currentX),
                    minOf(startY, currentY),
                    maxOf(startX, currentX),
                    maxOf(startY, currentY)
                )
                canvas?.drawRect(shapeRect, this)
            }
            ShapeToolType.CIRCLE -> {
                val radius = hypot(
                    (currentX - startX).toDouble(),
                    (currentY - startY).toDouble()
                ).toFloat()
                canvas?.drawCircle(startX, startY, radius, this)
            }
            ShapeToolType.TRIANGLE -> {
                shapePath.reset()
                shapePath.moveTo(startX, startY)
                shapePath.lineTo(currentX, currentY)
                shapePath.lineTo(startX - (currentX - startX), currentY)
                shapePath.close()
                canvas?.drawPath(shapePath, this)
            }
            ShapeToolType.LINE -> {
                canvas?.drawLine(startX, startY, currentX, currentY, this)
            }
        }
    }

    fun getCurrentDimensions(): String {
        val width = abs(currentX - startX)
        val height = abs(currentY - startY)

        return "W: ${width.toInt()}px, H: ${height.toInt()}px"
    }

    fun isCurrentlyDrawing(): Boolean {
        return isDrawing
    }
}