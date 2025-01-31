import fonts.FontsLoader
import imgui.*
import window.*
import window.layout.Layout

class Frame: WindowBase() {
    private lateinit var fontsLoader: FontsLoader
    private lateinit var layout: Layout

    override fun initImGui() { //During start
        ImGui.createContext()
        fontsLoader = FontsLoader(1, 30)
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
 * https://oprypin.github.io/crystal-imgui/ImGui/ImGuiCol.html Coloring GUI Elements docs
 **/
// TODO:
// Maze logic and displaying
//