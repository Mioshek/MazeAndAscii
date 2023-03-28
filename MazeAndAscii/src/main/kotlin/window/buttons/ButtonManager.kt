package window.buttons

import imgui.ImGui
import java.util.SortedMap

class ButtonManager {

    companion object{
        //Only one radio button can be checked at a time
        fun changeGroupValue(lastCheckedRadioButton: String, groupButtons: SortedMap<String, Boolean>) {
            for (button in groupButtons) {
                val buttonValue = button.value
                if (button.key != lastCheckedRadioButton && buttonValue) {
                    groupButtons[button.key] = false
                }
            }
        }

        fun maintainRadioButtons(buttonGroup: SortedMap<String, Boolean>){
            for (button in buttonGroup){
                val resButton = ImGui.radioButton(button.key, button.value)
                if (resButton){
                    buttonGroup[button.key] = true
                    changeGroupValue(button.key, buttonGroup)
                }
            }
        }
    }
}