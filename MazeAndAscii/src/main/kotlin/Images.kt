import imgui.ImGui
import org.lwjgl.opengl.GL30
import window.layout.ButtonsHandling
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import javax.imageio.ImageIO

class Images {
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

    private fun canBeCreated(path: String, filename: String): Boolean {
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
            val id = GL30.glGenTextures()
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, id)
            GL30.glTexParameterf(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR.toFloat())
            GL30.glTexParameterf(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR.toFloat())
            GL30.glTexImage2D(
                GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA8, image.width, image.height,
                0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer
            )
            GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D)
            return id
        }
    }
}