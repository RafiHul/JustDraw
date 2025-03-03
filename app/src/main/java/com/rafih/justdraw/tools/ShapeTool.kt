package com.rafih.justdraw.tools

import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent
import kotlin.math.abs

class ShapeTool: Tools() {

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var currentX: Float = 0f
    private var currentY: Float = 0f

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
        shapeRect.set(
            minOf(startX, currentX),
            minOf(startY, currentY),
            maxOf(startX, currentX),
            maxOf(startY, currentY)
        )
        canvas.drawRect(shapeRect, this)
    }

    fun drawShapeOnCanvas(canvas: Canvas?) {
        // Draw shape based on type
        shapeRect.set(
            minOf(startX, currentX),
            minOf(startY, currentY),
            maxOf(startX, currentX),
            maxOf(startY, currentY)
        )
        canvas?.drawRect(shapeRect, this)
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