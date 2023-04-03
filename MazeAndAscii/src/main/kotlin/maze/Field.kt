package maze

class Field(val height: Int, val width: Int) {
    val field = Array(height){Array(width){PathPoint(0,0,'0', null)} }
    var startParticle: PathPoint? = null
    var endParticle: PathPoint? = null
}