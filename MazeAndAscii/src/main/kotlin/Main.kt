import fonts.FontsLoader
import imgui.*
import org.lwjgl.opengl.GL30.*
import window.*
import window.layout.ButtonsHandling
import window.layout.SettingsWindow
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import javax.imageio.ImageIO


class Frame: WindowBase() {
    private val ascii = Ascii()
    private val settingsWindow = SettingsWindow(this)

    override fun initImGui() { //During start
        ImGui.createContext()
        FontsLoader.loadFonts()
    }

    override fun process() { //every frame
        if (!settingsWindow.selected){
            settingsWindow.runSettingsWindow()
        }
        else{
            FontsLoader.changeFontSize("Small")
            ImGui.begin("Original Image")
            ImGui.setWindowPos(0f, 0f)

            ascii.availableImages.forEach {
                if(it.value){
                    val imageId = ImageConverter.convertBufferedImageToIntId(it.key)
                    ImGui.image(imageId, 500f, 500f)
                }
            }
            ImGui.end()

            FontsLoader.popFont()
            FontsLoader.changeFontSize("Big")

            ImGui.begin("Mioshek")
            ImGui.setWindowPos(1000f, 0f)
            ImGui.setWindowSize(300f,500f)
            ascii.maintainRadioButtons()
            ImGui.end()
            FontsLoader.popFont()
        }
    }
}

class Ascii{
    private val projectPath = System.getProperty("user.dir")
    private val imagesPath = "$projectPath/src/main/resources/AsciiImages"
    private val imagesPathLastIndex = imagesPath.length
    val availableImages = sortedMapOf<String, Boolean>()


    fun maintainRadioButtons(){
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
                    ButtonsHandling.changeGroupValue(imageName, availableImages)
                }
            }
        }
    }

    fun canBeCreated(path: String, filename: String): Boolean {
        return path.isNotEmpty() && filename !in availableImages.keys
    }
}

class ImageConverter{
    //static to be more specific
    companion object{
        //Source but written in Java
        //https://stackoverflow.com/questions/59856706/how-can-i-load-bufferedimage-as-opengl-texture
        @Throws(IOException::class)
        fun convertBufferedImageToIntId(path: String): Int  {
            val image = ImageIO.read(File(path))
            val width = image.width; val height = image.height
            val pixels = image.getRGB(0, 0, width, height, null, 0, width)

            val buffer = ByteBuffer.allocateDirect(width * height * 4)
            for (h in 0 until height){
                for (w in 0 until width){
                    val pixel = pixels[h * width + w]
                    buffer.put((pixel shr 16 and 0xFF).toByte())
                    buffer.put((pixel shr 8 and 0xFF).toByte())
                    buffer.put((pixel and 0xFF).toByte())
                    buffer.put((pixel shr 24 and 0xFF).toByte())
                }
            }
            buffer.flip()
            val id = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, id)
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR.toFloat())
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGBA8, image.width, image.height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer
            )
            glGenerateMipmap(GL_TEXTURE_2D)
            return id
        }
    }
}


fun main() {
    val frame = Frame()
    frame.init()
    frame.run()
}