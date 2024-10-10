package pl.soundgame.engine.shapes

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

fun createTextTexture(text: String, width: Int = 256, height: Int = 256, size: Float = 32f, color: Color = Color.WHITE, background: Bitmap? = null ): Bitmap {
    var bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888)
    var width = width
    var height = height
    if(background != null) {
        bitmap = Bitmap.createBitmap(background)
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        width = bitmap.width
        height = bitmap.height
    }

    val canvas = Canvas(bitmap)


    val textPaint = Paint()
    textPaint.textSize = size
    textPaint.isAntiAlias = true
    textPaint.textAlign = Paint.Align.CENTER
    textPaint.setARGB(color.a, color.r, color.g, color.b)

    canvas.drawText(text, width/2.0f, height/2.0f, textPaint)

    return bitmap
}

