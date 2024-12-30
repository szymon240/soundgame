package pl.soundgame.engine.shapes

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

const val COORDS_PER_VERTEX = 3

val TEXTURE_COORDINATES = floatArrayOf(
    0.0f, 0.0f,
    0.0f, 1.0f,
    1.0f, 1.0f,
    1.0f, 0.0f,
)
class Sprite(bitmap: Bitmap) {
    var aspectRatio: Float = bitmap.width.toFloat() / bitmap.height.toFloat()
    var width: Float =  bitmap.width.toFloat()
    var height: Float = bitmap.height.toFloat()
    // Update vertex coordinates dynamically
    private val squareCoords = floatArrayOf(
        -0.5f * aspectRatio,  0.5f, 0.0f,   // top left
        -0.5f * aspectRatio, -0.5f, 0.0f,   // bottom left
        0.5f * aspectRatio, -0.5f, 0.0f,    // bottom right
        0.5f * aspectRatio,  0.5f, 0.0f     // top right
    )
    private var textureBitmap: Bitmap
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices
    private var textureUnit = IntArray(1)
    private var textureUniformHandle: Int = -1
    private var positionHandle: Int = 0
    private var texPositionHandle: Int = 0
    private val vertexCount: Int = squareCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4
    private var vPMatrixHandle: Int = 0
    private val TAG = "Sprite"


    init {
        textureBitmap = bitmap
        initializeTexture()
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "OpenGL error after texImage2D: $error")
        }
    }

    private fun initializeTexture() {
        GLES20.glGenTextures(1, textureUnit, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureUnit[0])
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        NUMBER_OF_TEXTURES++
    }

    fun swapImage(newBitmap: Bitmap) {
        // Generate and bind a new texture unit
        GLES20.glDeleteTextures(1,textureUnit,0)

        GLES20.glGenTextures(1, textureUnit, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureUnit[0])
        textureBitmap = newBitmap
        // Set the new texture with the new bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, newBitmap, 0)

        // Set texture parameters
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)

        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "OpenGL error after texImage2D: $error")
        }
    }

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    private val drawListBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val textureCoordinatesBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(TEXTURE_COORDINATES.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(TEXTURE_COORDINATES)
                position(0)
            }
        }

    fun draw(shaderProgram: Int, mvpMatrix: FloatArray) {
        GLES20.glUseProgram(shaderProgram)

        // Ensure the texture is active and bound before drawing
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureUnit[0])

        // Bind position data
        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
        }

        // Set MVP matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

        // Bind texture uniform
        textureUniformHandle = GLES20.glGetUniformLocation(shaderProgram, "uTexture")
        GLES20.glUniform1i(textureUniformHandle, 0)

        // Bind texture coordinates
        texPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "texCoord0").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it,
                2,
                GLES20.GL_FLOAT,
                false,
                2 * 4,
                textureCoordinatesBuffer
            )
        }

        // Draw the sprite
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT,
            drawListBuffer
        )

        // Disable vertex arrays
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texPositionHandle)

        // Unbind texture (optional, depending on your rendering pipeline)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glUseProgram(0)
    }
    companion object{
        var NUMBER_OF_TEXTURES = 0;
    }
}