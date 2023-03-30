package fonts

import imgui.*
import imgui.ImGuiIO

class FontsLoader(private val minSize: Int, private val maxSize: Int) {
    private val gruppoFonts = mutableListOf<ImFont>()
    private val jBrainsMonoFonts = mutableListOf<ImFont>()
    private val jBrainsMonoVectorFont = this.javaClass.getResourceAsStream("/fonts/JetBrainsMono-Regular.ttf").readAllBytes()
    private val gruppoVectorFont = this.javaClass.getResourceAsStream("/fonts/Chakra_Petch/ChakraPetch-Regular.ttf").readAllBytes()
    private val imIO: ImGuiIO = ImGui.getIO()
    private val atlas: ImFontAtlas = imIO.fonts

    fun setDefaultFont(fontSize: Int) {
        atlas.locked = false
        imIO.fontAllowUserScaling = true
        imIO.setFontDefault(gruppoFonts[fontSize - minSize - 1])
    }

    fun loadFonts(){
        for (i in minSize..maxSize){
            gruppoFonts.add(atlas.addFontFromMemoryTTF(gruppoVectorFont, i.toFloat()))
            jBrainsMonoFonts.add(atlas.addFontFromMemoryTTF(jBrainsMonoVectorFont, i.toFloat()))
        }
    }
    fun changeFontSize(fontSize: Int, fontFamily: String){
        try{
            when(fontFamily){
                "gruppo" -> ImGui.pushFont(gruppoFonts[fontSize - minSize -1])
                "jbrains" -> ImGui.pushFont(jBrainsMonoFonts[fontSize - minSize -1])
            }
        }
        catch (e:java.lang.Exception){
            println("Can't set fontSize to $fontSize, $fontFamily")
        }
    }
    fun popFont(){
        ImGui.popFont()
    }
}