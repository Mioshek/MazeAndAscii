package window.layout

import Frame
import Images
import ascii_converter.AsciiConverter
import ascii_converter.UnconvertedImage
import fonts.FontsLoader
import imgui.ImGui
import imgui.ImGuiIO
import maze.MazeColor
import maze.MazeDrawer
import kotlinx.coroutines.*
import maze.Backtracking

class Layout(val window: Frame, private val fontsLoader: FontsLoader) {
    private val availableGridsHorizontally: Int = 16
    private val availableGridsVertically: Int = 9
    private var singleGridWidth: Float = 0f
    private var singleGridHeight: Float = 0f
    private val ascii = UnconvertedImage()
    private val settingsWindow = SettingsWindow()
    private val style = ImGui.getStyle()

    fun showLayout(){
        if (!settingsWindow.selected){
            settingsWindow.runSettingsWindow(15)
            if (settingsWindow.isSelectedResolutionInitialized())
                getMainWindowResolution()
        }
        else{
            showOriginalImage(15)
            showImageChooser(15)
            showMaze()
            showAsciiImage(2)
        }
    }

    private fun getMainWindowResolution(){
        singleGridWidth = (settingsWindow.selectedResolution.width /availableGridsHorizontally).toFloat()
        singleGridHeight = (settingsWindow.selectedResolution.height/availableGridsVertically).toFloat()
    }

    private fun showImageChooser(fontSize: Int){
        fontsLoader.changeFontSize(fontSize, "gruppo")
        ImGui.begin("Image Chooser")
        ImGui.setWindowPos(0f, 0f)
        ImGui.setWindowSize(singleGridWidth * 2,singleGridHeight * 4)
        ascii.chooseImage()
        ImGui.end()
        fontsLoader.popFont()
    }

    private fun showOriginalImage(fontSize: Int){
        fontsLoader.changeFontSize(fontSize, "gruppo")
        ImGui.begin("Original Image")
        ImGui.setWindowPos(singleGridWidth * 2, 0f)
        ImGui.setWindowSize(singleGridWidth * 6.5f, singleGridHeight * 4)
        if (ascii.isChosenImageInitiated()){
            val bufferedImage = ascii.chosenImage
            val imageId = Images.convertBufferedImageToIntId(bufferedImage)
            ImGui.image(imageId, bufferedImage.width.toFloat(), bufferedImage.height.toFloat())
        }
        ImGui.end()
        fontsLoader.popFont()
    }

    private fun showAsciiImage(fontSize: Int){
        fontsLoader.changeFontSize(15, "gruppo")
        ImGui.begin("Converted To Ascii")
        fontsLoader.popFont()
        fontsLoader.changeFontSize(fontSize, "jbrains")
        ImGui.setWindowPos(singleGridWidth* 8.5f, 0f)
        ImGui.setWindowSize(singleGridWidth * 7.5f, singleGridHeight * 7.5f)
        if(ascii.isChosenImageInitiated() && UnconvertedImage.wasImageChanged){
            AsciiConverter.convertToAscii(ascii.chosenImage)
            UnconvertedImage.wasImageChanged = false
        }
        ImGui.text(AsciiConverter.finalAsciiImage)
        fontsLoader.popFont()
        ImGui.end()

    }

    private fun showMaze(){
        fontsLoader.changeFontSize(15,"gruppo")
        val windowWidth = singleGridWidth * 8
        val windowHeight = singleGridHeight * 5
        val buttonWidth = (windowWidth -100) / MazeDrawer.cols
        val buttonHeight = (windowHeight - 150) / MazeDrawer.rows

        ImGui.begin("Maze")
        ImGui.setWindowPos(0f,singleGridHeight * 4)
        ImGui.setWindowSize(windowWidth, windowHeight)
        style.setItemSpacing(1f,1f)

        if (MazeDrawer.toGenerateGrid){

            runBlocking { MazeDrawer.generateGrid() }
//            MazeDrawer.toGenerateGrid = false
        }

        for (row in 0 until MazeDrawer.rows){
            for (col in 0 until MazeDrawer.cols){

                val color = MazeDrawer.maze[row][col]
                ImGui.pushStyleColor(21, color.r, color.g, color.b, color.a)

                val button = ImGui.button("##$row$col", buttonWidth, buttonHeight)

                if (col != MazeDrawer.cols -1){
                    ImGui.sameLine()
                }

                ImGui.popStyleColor()
            }
        }
        val genGridButton = ImGui.button("Generate Grid")
        ImGui.sameLine()
        val genMaze = ImGui.button("Generate Maze")
        ImGui.sameLine()

        if (genMaze){
            Backtracking()
        }
        val solveMaze = ImGui.button("Solve Maze")
        if(genGridButton){
            MazeDrawer.toGenerateGrid = true
        }
        val sliderValue = arrayOf( floatArrayOf(MazeDrawer.rows.toFloat()), floatArrayOf(MazeDrawer.cols.toFloat()))
        val rows = ImGui.sliderFloat("Number of rows", sliderValue[0], 1f, 100f)
        val cols = ImGui.sliderFloat("Number of columns", sliderValue[1], 1f, 100f)

        if (rows){
            MazeDrawer.changeMazeDimensions(sliderValue)
        }


        ImGui.end()

        fontsLoader.popFont()
    }


}