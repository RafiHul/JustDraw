package com.rafih.justdraw.tools

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import com.rafih.justdraw.util.DefaultDrawToolProperty

abstract class Tools: Paint(){

    init {
        strokeWidth = DefaultDrawToolProperty.brushSize
        style = Style.STROKE
        isAntiAlias = true
        isFilterBitmap = true
        strokeJoin = Join.ROUND
        strokeCap = Cap.ROUND
        isDither = true
        isSubpixelText = true
        maskFilter = BlurMaskFilter(3f, BlurMaskFilter.Blur.NORMAL)
    }

    var toolsSize: Float
        get() {
            return strokeWidth
        }
        set(value) {
            strokeWidth = value
        }
}