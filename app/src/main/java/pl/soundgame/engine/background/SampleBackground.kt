package pl.soundgame.engine.background

import android.content.Context
import pl.soundgame.engine.ShaderProgram
import pl.soundgame.engine.shapes.Drawable

class SampleBackground(context: Context) : Background() {
    override var mShaderProgram: ShaderProgram
    override var mObjects: MutableList<Drawable> = mutableListOf()

    init{
        mShaderProgram = ShaderProgram("vertex_shader.glsl","fragment_shader.glsl", context)
    }
}