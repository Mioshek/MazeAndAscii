package window.layout

import Ascii
import Frame
import fonts.FontsLoader
import imgui.ImGui
import imgui.ImVec2


class Layout(val window: Frame, private val fontsLoader: FontsLoader) {
    private lateinit var windowResolution: ImVec2
    private val availableGridsHorizontally: Int = 16
    private val availableGridsVertically: Int = 9
    private var singleGridWidth: Float = 0f
    private var singleGridHeight: Float = 0f
    private val ascii = Ascii()
    private val settingsWindow = SettingsWindow(window, this)
    val style = ImGui.getStyle()
    val maze = Array(10){BooleanArray(10){false}}

    fun showLayout(){
        if (!settingsWindow.selected){
            settingsWindow.runSettingsWindow(15)
            if (settingsWindow.isSelectedResolutionInitialized())
            getMainWindowResolution()
        }
        else{
            showOriginalImage(15)
            chooseImage(15)
            showMaze(1,1)
            showAsciiImage(15)
        }
    }

    private fun getMainWindowResolution(){
        singleGridWidth = (settingsWindow.selectedResolution.width /availableGridsHorizontally).toFloat()
        singleGridHeight = (settingsWindow.selectedResolution.height/availableGridsVertically).toFloat()
    }

    private fun chooseImage(fontSize: Int){
        fontsLoader.changeFontSize(fontSize)

        ImGui.begin("Image Chooser")
        ImGui.setWindowPos(0f, 0f)
        ImGui.setWindowSize(singleGridWidth * 2,singleGridHeight * 4)
        ascii.maintainRadioButtons()
        ImGui.end()
        fontsLoader.popFont()
    }

    private fun showOriginalImage(fontSize: Int){
        fontsLoader.changeFontSize(fontSize)
        ImGui.begin("Original Image")
        ImGui.setWindowPos(singleGridWidth * 2, 0f,)
        ImGui.setWindowSize(singleGridWidth * 6.5f, singleGridHeight * 4)

        ascii.availableImages.forEach {
            if(it.value){
                val imageId = ImageConverter.convertBufferedImageToIntId(it.key)
                ImGui.image(imageId, singleGridWidth * 6, singleGridHeight * 3)
            }
        }
        ImGui.end()

        fontsLoader.popFont()
    }

    private fun showAsciiImage(fontSize: Int){
        fontsLoader.changeFontSize(fontSize)
        ImGui.begin("Converted To Ascii")

        ImGui.setWindowPos(singleGridWidth* 8.5f, 0f)
        ImGui.setWindowSize(singleGridWidth * 7.5f, singleGridHeight * 7.5f)
        ImGui.text("hi")

        ImGui.end()
        fontsLoader.popFont()
    }

    private fun showMaze(width: Int, height: Int){
        fontsLoader.changeFontSize(15)

        ImGui.begin("Maze")
        ImGui.setWindowPos(0f,singleGridHeight * 4)
        ImGui.setWindowSize(singleGridWidth * 6,singleGridHeight * 5)
        style.setItemSpacing(1f,1f)

        for (y in 0 until 10){
            for (x in 0 until 10){

                if (maze[y][x]){
                    ImGui.pushStyleColor(21, 255f, 0f, 0f, 1f)
                }
                else{
                    ImGui.pushStyleColor(21, 0f, 255f, 0f, 1f)
                }
                val button = ImGui.button("##$y$x", 50f, 50f)
                if (button){
                    maze[y][x] = !maze[y][x]
                }
                if (x != 9){
                    ImGui.sameLine()
                }

                ImGui.popStyleColor()
            }
        }

        ImGui.end()

        fontsLoader.popFont()
    }


}