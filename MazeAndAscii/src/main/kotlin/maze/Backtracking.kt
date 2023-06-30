package maze

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.random.Random


enum class Direction(dirNumber: Int){
    UP(1),
    DOWN(2),
    LEFT(3),
    RIGHT(4)
}


class Backtracking {
    companion object{
        var rows = MazeDrawer.rows
        var cols = MazeDrawer.cols

        val startRow = getPoint(1, rows -1) ; val startCol = getPoint(1, cols -1)
        val endRow = getPoint(1, rows -1); val endCol = getPoint(1, cols -1)

        init {
            MazeDrawer.maze[startRow][startCol] = MazeColor.START
            MazeDrawer.maze[endRow][endCol] = MazeColor.END
            runGenerateField(startRow, startCol)
        }

        fun getPoint(lower: Int, higher: Int): Int {
            val num = Random.nextInt(lower, higher)
            if (num % 2 != 0){
                return num
            }
            else return num + 1
        }

        fun runGenerateField(currRow: Int, currCol: Int) = GlobalScope.async {
            generateField(currRow, currCol)
        }
        fun generateField(currRow: Int, currCol: Int) {
            MazeDrawer.maze[currRow][currCol] = MazeColor.BLUE
//            if (MazeDrawer.maze[currRow - 2][currCol] == MazeColor.BLUE && MazeDrawer.maze[currRow + 2][ currCol] == MazeColor.BLUE && MazeDrawer.maze[currRow][ currCol - 2] == MazeColor.BLUE && MazeDrawer.maze[currRow][ currCol + 2] == MazeColor.BLUE){
//
//            }
//            else
            if (currRow - 2 < 1 ||  currRow + 2 > MazeDrawer.rows || currCol - 2 < 1 || currCol + 2 > MazeDrawer.cols){

            }
            else{
                val li = arrayListOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
                while (li.size > 0){
                    val dir = li.random()
                    li.remove(dir)

                    val (newX, newY, mx, my) = when (dir) {
                        Direction.LEFT -> listOf(currCol - 2, currRow, currCol - 1, currRow)
                        Direction.RIGHT -> listOf(currCol + 2, currRow, currCol + 1, currRow)
                        Direction.UP -> listOf(currCol, currRow - 2, currCol, currRow - 1)
                        Direction.DOWN -> listOf(currCol, currRow + 2, currCol, currRow + 1)
                    }

                    if (MazeDrawer.maze[newY][newX] != MazeColor.WHITE) {
                        MazeDrawer.maze[my][mx] = MazeColor.WHITE
                        generateField(newY, newX)
                    }
                }
            }
        }
    }
}