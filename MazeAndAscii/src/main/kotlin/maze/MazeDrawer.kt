package maze
import kotlinx.coroutines.*
import java.awt.Color

enum class MazeColor(val r: Float, val g: Float, val b: Float, val a: Float){
    START(50f, 50f, 50f, 1f),
    END(150f, 150f, 150f, 1f),
    BLUE(0f, 0f, 255f, 1f),
    GREEN(0f, 255f, 0f, 1f),
    YELLOW(255f, 255f, 0f, 1f),
    WHITE(255f, 255f, 255f, 1f)
}

class MazeDrawer {

    companion object{
        var buttonsWritten = false
        var toGenerateGrid = false
        var rows = 5
        var cols = 5
        var maze = Array(rows){Array(cols){MazeColor.WHITE} }

        fun changeMazeDimensions(array: Array<FloatArray>){
            val r = array[0][0].toInt()
            val c = array[0][0].toInt()
            if(r < 4 || r > 70 ){
                this.rows = 5
            }
            else if (c < 4){
                this.cols = 5
            }

            if (r % 2 == 1 && c % 2 ==1){
                this.rows= r
                this.cols= c
            }
            else{
                this.rows= r + 1
                this.cols= c + 1
            }
            this.maze =  Array(rows){Array(cols){MazeColor.WHITE}}

        }

        fun changeCellColor(row: Int, col: Int, color: MazeColor){
            this.maze[row][col] = color
        }

        fun generateGrid() = GlobalScope.async {

            for (row in 0 until rows){
                for (col in 0 until cols){
                    if (isGrid(row, col)){
                        delay(1L)
                        maze[row][col] = MazeColor.BLUE
                    }
                }
            }
        }

        fun isGrid(row: Int, col: Int): Boolean {
            if (row == 0 || row == this.rows){
                return true
            }

            if(row % 2 == 0 || col % 2 == 0){
                return true
            }
            return false
        }
    }
}