package pl.soundgame.engine

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import java.io.BufferedReader

class ShaderProgram(vertShaderName: String, fragShaderName: String ,context: Context) {
    private var mProgram: Int
    private val TAG  = "ShaderProgram"

    fun getProgram(): Int{
        return mProgram
    }

    init {

        val vertexShader: Int = compileAndLoadShader(GLES20.GL_VERTEX_SHADER, vertShaderName, context)
        val fragmentShader: Int = compileAndLoadShader(GLES20.GL_FRAGMENT_SHADER, fragShaderName, context)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }
        Log.i(TAG, "ShaderProgram has been created!")
    }

    private fun compileAndLoadShader(type: Int, shaderFileName: String, context: Context): Int{
        var shaderCode = context.assets.open("shaders/" + shaderFileName)
            .bufferedReader()
            .use(BufferedReader::readText)

        val shader = GLES20.glCreateShader(type).also {
            GLES20.glShaderSource(it, shaderCode)
            GLES20.glCompileShader(it)
        }

        // Check for compile errors
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader $shaderFileName: ${GLES20.glGetShaderInfoLog(shader)}")
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Shader compilation failed")
        }

        return shader
    }
}