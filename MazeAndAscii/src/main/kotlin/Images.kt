import imgui.ImGui
import org.lwjgl.opengl.GL30
import java.awt.image.BufferedImage
import java.io.IOException
import java.nio.ByteBuffer

class Images {
    //static to be more specific
    companion object{
        //Source but written in Java
        //https://stackoverflow.com/questions/59856706/how-can-i-load-bufferedimage-as-opengl-texture
        @Throws(IOException::class)
        fun convertBufferedImageToIntId(image: BufferedImage): Int  {
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