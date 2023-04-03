package maze

import java.util.Objects
import kotlin.math.abs

open class Point{
    var y: Int
    var x: Int
    var deadEnd: Boolean
    constructor(y: Int,  x: Int){
        this.y = y
        this.x = x
        this.deadEnd = false
    }

    constructor(){
        this.y = 0
        this.x = 0
        this.deadEnd = true
    }

    fun equals(particle: Point) : Boolean{
        return this.y == particle.y && this.x == particle.x
    }

    override fun hashCode(): Int {
        return Objects.hash(y, x)
    }

    override fun toString(): String {
        return "${this.y}, ${this.x}"
    }
}


class PathPoint(y: Int,
                x: Int,
                val symbol: Char,
                var origin: PathPoint?): Point(y, x) {

    private var gCost = 0
    private var hCost = 0
    var fCost = 0

    companion object{
        @JvmStatic lateinit var END: Point

        fun setGoal(goal: PathPoint){
            END = goal
        }

        fun calculateDistance(y1: Int, x1: Int, y2: Int, x2: Int ): Int {
            return abs(y1 - y2) + abs(x1 - x2)
        }
    }

    fun calPathDistance(): Int {
        var nodes = 0
        var particle = this
        while (particle.origin != null){
            particle = particle.origin!!
            nodes ++
        }
        return if (nodes == 0) {0}
        else{nodes}
    }

    fun calculateCosts(){
        this.gCost = calPathDistance()
        this.hCost = calculateDistance(y, x, END.y, END.x)
        this.fCost = gCost + hCost
    }

    override fun toString(): String {
        return super.hashCode().toString()
    }
}


class Astar(var field: Field,
            var gui: GuiFrame? = null,
            var guiDebugger: Boolean = false) {

    constructor(field: Field,  gui: GuiFrame): this(field){
        this.field = field
        this.gui = gui
        this.guiDebugger = true
    }

    private var currentParticle = this.field.startParticle
    private var closed = mutableSetOf(this.field.startParticle)
    private var available = mutableSetOf<PathPoint>()

    fun findPath(): PathPoint? {
        while (this.currentParticle?.equals(PathPoint.END) == false){
            this.currentParticle = this.findNextPoint()
            if (this.currentParticle!!.origin == null){
                return this.currentParticle
            }
            this.closed.add(this.currentParticle)
            this.available.remove(this.currentParticle)
        }
        return this.currentParticle
    }

    private fun checkIfWalkable(y: Int?, x: Int?, symbol: Char): Boolean {

        if (x != null && y != null) {
            if (y < 0 || x < 0 || y > field.height -1 ||  x > field.width -1){
                return false
            }
            else if (field.field[y][x].symbol == symbol + 1 || field.field[y][x].symbol <= symbol){
                return true
            }
        }
        return false
    }

    private fun findNextPoint(): PathPoint {
        val uRow = this.currentParticle?.y!! - 1; val uCol = this.currentParticle?.x
        val dRow = this.currentParticle?.y!! + 1; val dCol = this.currentParticle?.x
        val lRow = this.currentParticle?.y; val lCol = this.currentParticle?.x!! -1
        val rRow = this.currentParticle?.y; val rCol = this.currentParticle?.x!! + 1

        if (checkIfWalkable(rRow, rCol, currentParticle!!.symbol)){
            val pp = PathPoint(rRow!!, rCol,field.field[rRow][rCol].symbol, this.currentParticle!!)
            this.processNeighbour(pp, this.currentParticle!!)
        }
        if (checkIfWalkable(lRow, lCol, currentParticle!!.symbol)){
            val pp = PathPoint(lRow!!, lCol,field.field[lRow][lCol].symbol, this.currentParticle!!)
            this.processNeighbour(pp, this.currentParticle!!)
        }
        if (checkIfWalkable(uRow, uCol, currentParticle!!.symbol)){
            val pp = PathPoint(uRow, uCol!!,field.field[uRow][uCol].symbol, this.currentParticle!!)
            this.processNeighbour(pp, this.currentParticle!!)
        }
        if (checkIfWalkable(dRow, dCol, currentParticle!!.symbol)){
            val pp = PathPoint(dRow, dCol!!,field.field[dRow][dCol].symbol, this.currentParticle!!)
            this.processNeighbour(pp, this.currentParticle!!)
        }
        if (this.available.size == 0){
            return PathPoint(0, 0 , '0', null)

        }
        var min = 100_000
        var minParticle = PathPoint(0,0,'0', field.startParticle!!)

        for (item in this.available){
            if (item.fCost < min){
                min = item.fCost
                minParticle = item
            }
        }
        return minParticle
    }

    private fun processNeighbour(neighbour: PathPoint, current: PathPoint){

        for (closedEl in this.closed){
            if (closedEl!!.equals(neighbour)){
                return
            }
        }

        var notFound = true

        for (availableElement in this.available){
            if (availableElement.equals(neighbour)){
                notFound = false
                var previous = neighbour // to get rid of nullables
                if (availableElement.y == neighbour.y && availableElement.x == neighbour.x) {
                    previous = availableElement
                }

                val currentDistance = current.calPathDistance() + 1
                if (previous.calPathDistance() > currentDistance) {
                    previous.origin = current
                    previous.calculateCosts()
                }
            }
        }
        if (notFound) {
            this.available.add(neighbour)
        }
    }
}