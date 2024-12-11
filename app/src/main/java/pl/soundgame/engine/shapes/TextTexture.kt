package pl.soundgame.engine.shapes

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

fun createTextTexture(
    text: String,
    width: Int = 256,
    height: Int = 256,
    size: Float = 32f,
    color: Color = Color.WHITE,
    background: Bitmap? = null
): Bitmap {
    var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    var canvasWidth = width
    var canvasHeight = height

    // Use the background if provided
    if (background != null) {
        bitmap = Bitmap.createBitmap(background)
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        canvasWidth = bitmap.width
        canvasHeight = bitmap.height
    }

    val canvas = Canvas(bitmap)

    val textPaint = Paint().apply {
        textSize = size
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        setARGB(color.a, color.r, color.g, color.b)
    }

    val maxTextWidth = canvasWidth * 0.9f
    val lineSpacing = size * 1.2f
    val lines = text.split("\n").flatMap { line ->
        val wrappedLines = ArrayList<String>()
        var currentLine = ""
        for (word in line.split(" ")) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (textPaint.measureText(testLine) < maxTextWidth) {
                currentLine = testLine
            } else {
                wrappedLines.add(currentLine)
                currentLine = word
            }
        }
        wrappedLines.add(currentLine)
        wrappedLines
    }
    val totalTextHeight = lines.size * lineSpacing
    var yOffset = (canvasHeight - totalTextHeight) / 2f + size
    for (line in lines) {
        canvas.drawText(line, canvasWidth / 2f , yOffset, textPaint)
        yOffset += lineSpacing
    }

    return bitmap
}