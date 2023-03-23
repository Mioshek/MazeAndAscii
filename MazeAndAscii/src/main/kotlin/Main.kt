import fonts.FontsLoader
import imgui.*
import window.*
import window.layout.Layout

class Frame: WindowBase() {
    lateinit var fontsLoader: FontsLoader
    private lateinit var layout: Layout

    override fun initImGui() { //During start
        ImGui.createContext()
        fontsLoader = FontsLoader(10, 30)
        layout = Layout(this, fontsLoader)
        fontsLoader.loadFonts()
        fontsLoader.setDefaultFont(15)
    }

    override fun process() { //every frame
        layout.showLayout()
    }
}


fun main() {
    val frame = Frame()
    frame.init()
    frame.run()
}

/**
USEFUL REFERENCE LINKS:
 * https://oprypin.github.io/crystal-imgui/ImGui/ImGuiCol.html Coloring GUI docs
**/