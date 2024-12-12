import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager
import java.util.Locale

fun getScreenResolution(context: Context): Pair<Int, Int> {
    val displayMetrics = DisplayMetrics()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    return Pair(width, height)
}

fun changeLocale(context: Context, languageCode: String): Context {
    // Set the desired locale (you can change this dynamically based on the language code)
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    // Create a new configuration object to apply the new locale
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    // Apply the new configuration to the context and return the updated context
    return context.createConfigurationContext(config)
}