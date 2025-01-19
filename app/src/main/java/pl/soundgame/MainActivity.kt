package pl.soundgame

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import changeLocale
import pl.soundgame.engine.GameGLSurfaceView
import java.util.Locale

/**
 * The main entry point of the app, responsible for initializing the game and rendering the OpenGL view.
 *
 * @author Adam Czyżak & Szymon Szymankiewicz
 */
class MainActivity : AppCompatActivity() {
    private lateinit var gLView: GLSurfaceView
    private lateinit var editText: EditText
    private var onEnterCallback: ((String) -> Unit)? = null
    /**
     * Called when the activity is first created.
     * Initializes the game and sets up the OpenGL surface view for rendering.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains its previous state.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val savedLanguageCode =
            sharedPreferences.getString("language_code", "en") ?: "en" // Default to "en"

        applyLanguage(savedLanguageCode)
        val game = SoundGame(this)

        gLView = GameGLSurfaceView(this, game)
        val frameLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            addView(gLView)
        }

        setContentView(frameLayout)
        editText = EditText(this).apply {
            visibility = View.GONE
            inputType = InputType.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_ACTION_DONE
            filters = arrayOf(
                InputFilter.LengthFilter(16),
                InputFilter { source, _, _, _, _, _ ->
                    if (source.matches(Regex("^[a-zA-Z0-9 ]*$"))) source else ""
                }
            )

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val enteredText = text.toString()
                    onEnterCallback?.invoke(enteredText)
                    text.clear()
                    visibility = View.GONE
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }

            setBackgroundColor(Color.WHITE)
            setTextColor(Color.BLACK)
        }

        val editTextParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM
        )
        addContentView(editText, editTextParams)
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)

            val screenHeight = rootView.rootView.height
            val keyboardHeight = screenHeight - rect.bottom

            if (keyboardHeight > screenHeight * 0.15) { //
                val params = editText.layoutParams as FrameLayout.LayoutParams
                params.bottomMargin = keyboardHeight
                editText.layoutParams = params
                editText.visibility = View.VISIBLE
            } else {
                val params = editText.layoutParams as FrameLayout.LayoutParams
                params.bottomMargin = 0
                editText.layoutParams = params
                editText.visibility = View.GONE
            }
        }
    }

    fun showKeyboard(onEnter: (String) -> Unit) {
        onEnterCallback = onEnter
        runOnUiThread {
            editText.visibility = View.VISIBLE
            editText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    // Hide the keyboard
    fun hideKeyboard() {
        runOnUiThread {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
            editText.visibility = View.GONE
        }
    }

    fun getUserInput(): String {
        return editText.text.toString()
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}