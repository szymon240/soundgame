package pl.soundgame.engine.shapes

data class Color(
    val r: Int,
    val g: Int,
    val b: Int,
    val a: Int
){
    companion object{
        val BLUE = Color(0x00,0x00,0xff,0xff)
        val BLACK = Color(0x00,0x00,0x00,0xff)
        val WHITE = Color(0xff,0xff,0xff,0xff)
        val YELLOW = Color( 0xff, 0xff, 0x00,0xff)
    }
}

