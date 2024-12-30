import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager
import java.util.Locale

/**
 * Gets Pair of screen resolution values
 *
 * @param context Main activity app context
 * @return Pair object of two Ints - width and height
 */
fun getScreenResolution(context: Context): Pair<Int, Int> {
    val displayMetrics = DisplayMetrics()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    return Pair(width, height)
}

/**
 * Changes localization of app
 *
 * @param context
 * @param languageCode
 * @return
 */
fun changeLocale(context: Context, languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    return context.createConfigurationContext(config)
}