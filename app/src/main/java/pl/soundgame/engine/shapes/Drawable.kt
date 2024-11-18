package pl.soundgame.engine.shapes

abstract class Drawable {
    protected abstract val mMatrix: FloatArray
    abstract fun draw(shaderProgram: Int, mvpMatrix: FloatArray)
}