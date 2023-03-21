package fonts

import imgui.*
import imgui.ImGuiIO
import java.nio.file.Files
import java.nio.file.Paths

class FontsLoader {
    companion object{
        val gruppo = this.javaClass.getResourceAsStream("/fonts/Gruppo/Gruppo-Regular.ttf").readAllBytes()
        val imIO: ImGuiIO = ImGui.getIO()
        val atlas = imIO.fonts
        val gruppoFontSmall = atlas.addFontFromMemoryTTF(gruppo, 10f)
        val gruppoFontNormal = atlas.addFontFromMemoryTTF(gruppo, 15f)
        val gruppoFontBig = atlas.addFontFromMemoryTTF(gruppo, 20f)
        fun loadFonts() {
            atlas.locked = false
//            val indieFlower = this.javaClass.getResourceAsStream("/fonts/Indie_Flower/Indie_Flower-Regular.ttf").readAllBytes()
//            val chakraPetch = this.javaClass.getResourceAsStream("/fonts/Chakra_Petch-Regular.ttf").readAllBytes()
            imIO.fontAllowUserScaling = true
            imIO.setFontDefault(gruppoFontNormal)
//            imIO.fonts.build()
        }

        fun changeFontSize(fontName: String){
            when (fontName){
                "Small" -> {
                    ImGui.pushFont(gruppoFontSmall)
                }
                "Normal" -> {
                    ImGui.pushFont(gruppoFontNormal)
                }
                "Big" -> {
                    ImGui.pushFont(gruppoFontBig)
                }
                else -> ImGui.pushFont(gruppoFontBig)
            }
        }

        fun popFont(){
            ImGui.popFont()
        }
    }
}