package ascii_converter

import imgui.ImGui
import window.buttons.ButtonManager
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File


class AsciiConverter() {

    companion object{
        var finalAsciiImage = ""
        private var hasAlphaChannel = false
        private val ASCII_CHARACTERS = "    `^\\,:;Il!i~+_-?][}{1)(|/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$".toCharArray()
        private const val ARGB_MAX_BRIGHTNESS = 1020f // 4*255


        fun convertToAscii(image: BufferedImage) {
            finalAsciiImage = ""
            val pixels = (image.raster.dataBuffer as DataBufferByte).data
            val cols = image.width
            val rows = image.height
            val hasAlphaChannel = image.alphaRaster != null
            var pos = 0
            for(row in 0 until rows){
                for (col in 0 until cols){
                    var argb = 0

                    if (hasAlphaChannel){
                        argb += pixels[pos].toInt() and 0xff // alpha
                        argb += pixels[pos + 1].toInt() and 0xff // blue

                        argb += pixels[pos + 2].toInt() and 0xff // green

                        argb += pixels[pos + 3].toInt() and 0xff // red
                        pos += 4
                    }
                    else{
                        argb += 255 // alpha
                        argb += pixels[pos].toInt() and 0xff // blue

                        argb += pixels[pos + 1].toInt() and 0xff // green

                        argb += pixels[pos + 2].toInt() and 0xff // red
                        pos +=3
                    }

                    val brightnessIntensity = ASCII_CHARACTERS.size / ARGB_MAX_BRIGHTNESS
                    val asciiIndex = (argb * brightnessIntensity -1).toInt()
                    val singlePixelInAscii = ASCII_CHARACTERS[asciiIndex]
                    finalAsciiImage += singlePixelInAscii
                    finalAsciiImage += singlePixelInAscii
                    /*ascii field is a rectangle and to make image close to square 2 characters are needed*/
                }
                finalAsciiImage += "\n"
            }
        }
    }

}

class UnconvertedImage{
    companion object{
        var wasImageChanged = false
    }

    private val projectPath = System.getProperty("user.dir")
    private val imagesPath = "$projectPath/src/main/resources/AsciiImages"
    private val imagesPathLastIndex = imagesPath.length
    val availableImages = sortedMapOf<String, Boolean>()
    lateinit var chosenImage: BufferedImage


    fun chooseImage(){
        wasImageChanged = false
        File(imagesPath).walkTopDown().forEach {

            val imageName = it.toString()
            val relativeImagePath = imageName.subSequence(imagesPathLastIndex, imageName.length).toString()
            if (canBeCreated(relativeImagePath, imageName)){
                availableImages[imageName] = ImGui.radioButton(relativeImagePath, false)
            }
            else if (imageName in availableImages){
                val currentButton = ImGui.radioButton(relativeImagePath, availableImages[imageName]!!)
                if (currentButton){

                    availableImages[imageName] = !availableImages[imageName]!!
                    ButtonManager.changeGroupValue(imageName, availableImages)
                    if (availableImages[imageName] == true){
                        wasImageChanged = true
                    }
                }

            }
        }
    }

    fun isChosenImageInitiated() = ::chosenImage.isInitialized

    private fun canBeCreated(path: String, filename: String): Boolean {
        return path.isNotEmpty() && filename !in availableImages.keys
    }
}
