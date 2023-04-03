package window

/**
 * This is a modified version of imgui.app.Configuration <br>
 * For docs refer to: {@link imgui.app.Configuration}
 **/
//Converted From Java to Kotlin by me

class Config {

    val title = "ImGui Window"
    var fullscreen = false; var resizable = true;  var decorated = true
    private val resolution = WindowSize.HD
    var width = resolution.width; var height = resolution.height
}

enum class WindowSize(val width: Int, val height: Int){
    SD(720, 480),
    HD(1280,720),
    FULLHD(1920, 1080),
    QHD(2560, 1440),
    UHD(3840, 2160);

    companion object{
        fun equals(other: String): WindowSize {
            return when (other){
                "SD" -> SD
                "HD" -> HD
                "FULLHD" -> FULLHD
                "QHD" -> QHD
                else -> UHD
            }
        }
    }

}