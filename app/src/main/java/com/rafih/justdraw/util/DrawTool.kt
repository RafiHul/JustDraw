package com.rafih.justdraw.util

sealed interface TopTool

enum class MainDrawTool: TopTool {
    ERASER,
    BRUSH
}

enum class SecDrawTool {
    FILLCOLOR,
    SHAPE
}

enum class ShapeToolType: TopTool{
    RECTANGLE,
    CIRCLE,
    TRIANGLE,
    LINE
}