package window.layout

import Frame
import Images
import ascii_converter.AsciiConverter
import ascii_converter.UnconvertedImage
import fonts.FontsLoader
import imgui.ImGui
import imgui.ImVec2
import java.io.File
import javax.imageio.ImageIO


class Layout(val window: Frame, private val fontsLoader: FontsLoader) {
    private val availableGridsHorizontally: Int = 16
    private val availableGridsVertically: Int = 9
    private var singleGridWidth: Float = 0f
    private var singleGridHeight: Float = 0f
    private val ascii = UnconvertedImage()
    private val settingsWindow = SettingsWindow(window, this)
    private val style = ImGui.getStyle()
    private val maze = Array(10){BooleanArray(10){false}}

    fun showLayout(){
        if (!settingsWindow.selected){
            settingsWindow.runSettingsWindow(15)
            if (settingsWindow.isSelectedResolutionInitialized())
                getMainWindowResolution()
        }
        else{
            showOriginalImage(15)
            showImageChooser(15)
            showMaze(1,1)
            showAsciiImage(3)
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
        ImGui.setWindowPos(singleGridWidth * 2, 0f,)
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

    private fun showMaze(width: Int, height: Int){
        fontsLoader.changeFontSize(15,"gruppo")

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