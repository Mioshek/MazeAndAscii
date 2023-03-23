package fonts

import imgui.*
import imgui.ImGuiIO

class FontsLoader(private val minSize: Int, private val maxSize: Int) {
    private val gruppoFonts = mutableListOf<ImFont>()
    private val gruppoVectorFont = this.javaClass.getResourceAsStream("/fonts/Gruppo/Gruppo-Regular.ttf").readAllBytes()
    private val imIO: ImGuiIO = ImGui.getIO()
    private val atlas: ImFontAtlas = imIO.fonts

    fun setDefaultFont(fontSize: Int) {
        atlas.locked = false
//           val indieFlower = this.javaClass.getResourceAsStream("/fonts/Indie_Flower/Indie_Flower-Regular.ttf").readAllBytes()
//           val chakraPetch = this.javaClass.getResourceAsStream("/fonts/Chakra_Petch-Regular.ttf").readAllBytes()
        imIO.fontAllowUserScaling = true
        imIO.setFontDefault(gruppoFonts[fontSize - minSize - 1])
    }

    fun loadFonts(){
        for (i in minSize..maxSize){
            gruppoFonts.add(atlas.addFontFromMemoryTTF(gruppoVectorFont, i.toFloat()))
        }
    }
    fun changeFontSize(fontSize: Int){
        try{
            ImGui.pushFont(gruppoFonts[fontSize - minSize -1])
        }
        catch (e:java.lang.Exception){
            println("Can't set fontSize to $fontSize")
        }
    }
    fun popFont(){
        ImGui.popFont()
    }
}