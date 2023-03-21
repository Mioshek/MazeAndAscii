package window.layout

import java.util.SortedMap

class ButtonsHandling {

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
    }
}