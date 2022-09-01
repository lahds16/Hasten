package lahds.hasten.ui.components

import android.graphics.Color

object Theme {
    var background: Int = Color.WHITE
    var toolbar: Int = Color.WHITE
    var toolbarTitle: Int = Color.WHITE
    var title: Int = Color.WHITE
    var subtitle: Int = Color.WHITE
    var toolbarIcon: Int = Color.WHITE
    var icon: Int = Color.WHITE
    var primary: Int = Color.WHITE
    var receiverMessage: Int = Color.WHITE
    var receiverText: Int = Color.WHITE

    fun bootTheme(theme: Map<String, String>) {
        background = getColor(theme["background"]!!)
        toolbar = getColor(theme["toolbar"]!!)
        toolbarTitle= getColor(theme["toolbarTitle"]!!)
        title = getColor(theme["title"]!!)
        subtitle = getColor(theme["subtitle"]!!)
        toolbarIcon = getColor(theme["toolbarIcon"]!!)
        icon = getColor(theme["icon"]!!)
        primary = getColor(theme["primary"]!!)
        receiverText = getColor(theme["receiverText"]!!)
        receiverMessage = getColor(theme["receiverMessage"]!!)
    }

    private fun getColor(color: String): Int {
        return Color.parseColor(color)
    }
}