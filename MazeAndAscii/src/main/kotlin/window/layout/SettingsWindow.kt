package window.layout

import Frame
import imgui.*
import org.lwjgl.glfw.GLFW
import window.*

class SettingsWindow(val frame: Frame, val layout: Layout) {
    var selected = false
    private val resolutions = sortedMapOf<String, Boolean>()
    lateinit var selectedResolution: WindowSize
    val config = Config()
    init {
        resolutions["1.SD"] = false
        resolutions["2.HD"] = true
        resolutions["3.FULLHD"] = false
        resolutions["4.QHD"] = false
        resolutions["5.UHD"] = false
    }

    fun isSelectedResolutionInitialized() = ::selectedResolution.isInitialized

    fun runSettingsWindow(fontSize: Int){
        ImGui.begin("Settings")
        ImGui.setWindowSize(config.width.toFloat()/2, config.height.toFloat()/2)
        ImGui.text("Choose Window Resolution")
        for (resolution in resolutions){
            val resButton = ImGui.radioButton(resolution.key, resolution.value)
            if (resButton){
                resolutions[resolution.key] = !resolutions[resolution.key]!!
                ButtonsHandling.changeGroupValue(resolution.key, resolutions)
            }

        }
        val submitButton = ImGui.button("Submit")
        if (submitButton){
            selected = true
            resolutions.forEach {
                if(it.value){
                    val resolutionName = it.key.split(".")[1]
                    selectedResolution = WindowSize.equals(resolutionName)
                    GLFW.glfwSetWindowSize(WindowBase.windowPtr,selectedResolution.width, selectedResolution.height)
                }
            }
        }
        ImGui.end()
    }
}