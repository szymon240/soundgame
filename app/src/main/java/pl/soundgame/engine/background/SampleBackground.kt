package pl.soundgame.engine.background

import android.content.Context
import pl.soundgame.engine.ShaderProgram
import pl.soundgame.engine.shapes.Drawable

class SampleBackground(context: Context) : Background() {
    override var mShaderProgram: ShaderProgram
    override var mObjects: MutableList<Drawable> = mutableListOf()
    override fun updateFrame() {

    }

    init{
        mShaderProgram = ShaderProgram("foreground_vertex_shader.glsl","foreground_fragment_shader.glsl", context)
    }
}