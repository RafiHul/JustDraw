package com.rafih.justdraw.tools

import android.graphics.Paint

abstract class Tools: Paint(){

    init {
        strokeWidth = 5f
        style = Style.STROKE
        isAntiAlias = true
        isFilterBitmap = true
        strokeJoin = Join.ROUND
    }

    var toolsSize: Float
        get() {
            return strokeWidth
        }
        set(value) {
            strokeWidth = value
        }
}