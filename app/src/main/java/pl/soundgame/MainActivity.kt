package pl.soundgame

import android.content.Context
import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import changeLocale
import pl.soundgame.engine.GameGLSurfaceView

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


        val game = SoundGame(this)

        gLView = GameGLSurfaceView(this, game)
        val frameLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            addView(gLView)
        }

        setContentView(frameLayout)
        editText = EditText(this).apply {
            visibility = View.GONE
            inputType = InputType.TYPE_CLASS_TEXT // Basic text input
            imeOptions = EditorInfo.IME_ACTION_DONE // Show "Done" button on the keyboard
            filters = arrayOf(
                InputFilter.LengthFilter(20), // Limit to 20 characters
                InputFilter { source, _, _, _, _, _ ->
                    if (source.matches(Regex("^[a-zA-Z0-9]*$"))) source else "" // Allow only alphanumeric
                }
            )

            // Listen for the "Done" key
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val enteredText = text.toString()
                    onEnterCallback?.invoke(enteredText) // Trigger the callback
                    text.clear() // Clear the input text
                    visibility = View.GONE // Hide the EditText
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }
        }


        // Add the EditText to the window
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        addContentView(editText, layoutParams)
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
}