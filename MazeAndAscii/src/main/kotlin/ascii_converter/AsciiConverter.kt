package ascii_converter

import imgui.ImGui
import window.buttons.ButtonManager
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import javax.imageio.ImageIO


class AsciiConverter {

    companion object{
        var finalAsciiImage = ""
        private val ASCII_CHARACTERS = "    `^\\,:;Il!i~+_-?][}{1)(|/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$".toCharArray()
        private const val ARGB_MAX_BRIGHTNESS = 765f // 3*255

        fun convertToAscii(image: BufferedImage) {
//            val start = System.currentTimeMillis()
            val pixels = (image.raster.dataBuffer as DataBufferByte).data // contains r g b pixels separate or a r g b
            val cols = image.width
            val rows = image.height
            val pixelsLength = cols * rows
            val imageArr = CharArray(pixelsLength * 2 + rows){' '}
            println(imageArr.size)
            val hasAlphaChannel = image.alphaRaster != null
            var pos = 0
            var readyImageIterator = 0
            if (hasAlphaChannel){ //for ex. .png file
                for (i in 0 until pixelsLength){
                    var argb = 1
                    argb += pixels[pos + 1].toInt() and 0xff // blue
                    argb += pixels[pos + 2].toInt() and 0xff // green
                    argb += pixels[pos + 3].toInt() and 0xff // red
                    pos += 4
                    val brightnessIntensity = ASCII_CHARACTERS.size / ARGB_MAX_BRIGHTNESS
                    val asciiIndex = (argb * brightnessIntensity -1).toInt()
                    val singlePixelInAscii = ASCII_CHARACTERS[asciiIndex]
                    imageArr[readyImageIterator] = singlePixelInAscii
                    imageArr[readyImageIterator + 1] = singlePixelInAscii
                    /*ascii field is a rectangle and to make image close to square 2 characters are needed*/
                    readyImageIterator += 2
                    if (i % cols == 0){
                        imageArr[readyImageIterator] = '\n'
                        readyImageIterator += 1
                    }
                }
            }
            else{ // .jpg .jpeg
                for (i in 0 until pixelsLength){

                    var argb = 1
                    argb += pixels[pos].toInt() and 0xff // blue
                    argb += pixels[pos + 1].toInt() and 0xff // green
                    argb += pixels[pos + 2].toInt() and 0xff // red
                    pos +=3
                    val brightnessIntensity = ASCII_CHARACTERS.size / ARGB_MAX_BRIGHTNESS
                    val asciiIndex = (argb * brightnessIntensity -1).toInt()
                    val singlePixelInAscii = ASCII_CHARACTERS[asciiIndex]
                    imageArr[readyImageIterator] = singlePixelInAscii
                    imageArr[readyImageIterator + 1] = singlePixelInAscii
                    /*ascii field is a rectangle and to make image close to square 2 characters are needed*/
                    readyImageIterator +=2
                    if (i % cols == 0){
                        imageArr[readyImageIterator] = '\n'
                        readyImageIterator += 1
                    }
                }
            }
            finalAsciiImage = String(imageArr)
//            println((System.currentTimeMillis() - start))
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
    private val availableImages = sortedMapOf<String, Boolean>()
    lateinit var chosenImage: BufferedImage


    fun chooseImage(){
        File(imagesPath).walkTopDown().forEach {

            val imageName = it.toString()
            val relativeImagePath = imageName.subSequence(imagesPathLastIndex, imageName.length).toString()
            if (canBeCreated(relativeImagePath, imageName)){
                availableImages[imageName] = ImGui.radioButton(relativeImagePath, false)
            }
            else if (imageName in availableImages){
                val currentButton = ImGui.radioButton(relativeImagePath, availableImages[imageName]!!)
                if (currentButton){
                    availableImages[imageName] = true
                    ButtonManager.changeGroupValue(imageName, availableImages)
                    if (availableImages[imageName] == true){
                        chosenImage = ImageIO.read(File(imageName))
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