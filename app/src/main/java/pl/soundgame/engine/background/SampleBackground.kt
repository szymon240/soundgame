package pl.soundgame.engine.background

import android.content.Context
import android.opengl.GLES20
import getScreenResolution
import pl.soundgame.engine.ShaderProgram
import pl.soundgame.engine.shapes.Drawable
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SampleBackground(context: Context) : Background() {
    override var mShaderProgram: ShaderProgram
    override var mObjects: MutableList<Drawable> = mutableListOf()
    private var time = 0.0f
    private val resolution = getScreenResolution(context)
    private val quadVertexBuffer: Int

    private val quadVertices = floatArrayOf(
        -1.0f, -1.0f,  // Bottom-left corner
        1.0f, -1.0f,  // Bottom-right corner
        -1.0f,  1.0f,  // Top-left corner
        1.0f,  1.0f   // Top-right corner
    )

    override fun updateFrame() {
        time += 0.016f  // Assuming 60 FPS, adjust as needed

        GLES20.glUseProgram(mShaderProgram.getProgram())
        val resolutionHandle = GLES20.glGetUniformLocation(mShaderProgram.getProgram(), "u_resolution")
        GLES20.glUniform2fv(resolutionHandle, 1, floatArrayOf(resolution.first.toFloat(), resolution.second.toFloat()), 0)

        val timeHandle = GLES20.glGetUniformLocation(mShaderProgram.getProgram(), "u_time")
        GLES20.glUniform1f(timeHandle, time)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, quadVertexBuffer)
        val positionHandle = GLES20.glGetAttribLocation(mShaderProgram.getProgram(), "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glUseProgram(0)
    }

    init {
        mShaderProgram = ShaderProgram("lights_background_vertex_shader.glsl", "lights_background_fragment_shader.glsl", context)
        val buffers = IntArray(1)
        GLES20.glGenBuffers(1, buffers, 0)
        quadVertexBuffer = buffers[0]
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, quadVertexBuffer)
        val vertexData = ByteBuffer.allocateDirect(quadVertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(quadVertices)
        vertexData.position(0)
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, quadVertices.size * 4, vertexData, GLES20.GL_STATIC_DRAW)

        // Unbind the buffer
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }
}