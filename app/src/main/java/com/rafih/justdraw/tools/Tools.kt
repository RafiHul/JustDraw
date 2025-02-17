package com.rafih.justdraw.tools

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import com.rafih.justdraw.util.DrawToolProperty

abstract class Tools: Paint(){

    init {
        strokeWidth = DrawToolProperty.defaultBrushSize
        style = Style.STROKE
        isAntiAlias = true
        isFilterBitmap = true
        strokeJoin = Join.ROUND
        strokeCap = Paint.Cap.ROUND
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