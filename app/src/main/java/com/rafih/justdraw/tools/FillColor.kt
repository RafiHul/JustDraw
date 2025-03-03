package com.rafih.justdraw.tools

import android.graphics.Bitmap

class FillColor: Tools(){

    // TODO: add coroutines
    fun floodFill(x: Int, y: Int, bitmap: Bitmap) {

        val newColor = this.color
        val bitmapH = bitmap.height
        val bitmapW = bitmap.width

        val targetColor = bitmap.getPixel(x, y)
        if (targetColor == newColor) return

        val pixels = IntArray(bitmapW * bitmapH)
        bitmap.getPixels(pixels, 0, bitmapW, 0, 0, bitmapW, bitmapH)

        val queue = ArrayDeque<Pair<Int, Int>>()
        queue.add(Pair(x, y))

        while (queue.isNotEmpty()) {
            val (px, py) = queue.removeFirst()
            val index = py * bitmapW + px

            if (px < 0 || px >= bitmapW || py < 0 || py >= bitmapH ||
                pixels[index] != targetColor) {
                continue
            }

            pixels[index] = newColor

            queue.add(Pair(px + 1, py))
            queue.add(Pair(px - 1, py))
            queue.add(Pair(px, py + 1))
            queue.add(Pair(px, py - 1))
        }

        bitmap.setPixels(pixels, 0, bitmapW, 0, 0, bitmapW, bitmapH)
    }
}