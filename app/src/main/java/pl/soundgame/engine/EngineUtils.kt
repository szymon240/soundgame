import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

fun getScreenResolution(context: Context): Pair<Int, Int> {
    val displayMetrics = DisplayMetrics()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    return Pair(width, height)
}

