package com.rafih.justdraw.util

sealed interface DrawTool

enum class MainDrawTool: DrawTool {
    ERASER,
    BRUSH
}

enum class SecDrawTool: DrawTool {
    FILLCOLOR,
    SHAPE
}

enum class ShapeToolType{
    RECTANGLE,
    CIRCLE,
    TRIANGLE,
    LINE
}