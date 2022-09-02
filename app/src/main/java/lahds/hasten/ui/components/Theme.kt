package lahds.hasten.ui.components

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import lahds.hasten.app.utils.Utilities
import lahds.hasten.ui.LaunchActivity
import java.lang.reflect.Type

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
        if (theme.size == 10) {
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
        } else {
            applyTheme()
        }
    }

    private fun getColor(color: String): Int {
        return Color.parseColor(color)
    }

    private fun applyTheme() {
        lateinit var themePreferences: SharedPreferences
        val gson = GsonBuilder().create()

        if (Utilities.isDarkTheme()) {
            themePreferences = LaunchActivity.activity.getSharedPreferences("darkTheme", MODE_PRIVATE
            )
            themePreferences.edit()
                .putString("themeMap", gson.toJson(Utilities.defaultDarkTheme()))
                .apply()
        } else {
            themePreferences = LaunchActivity.activity.getSharedPreferences("lightTheme", MODE_PRIVATE
            )
            themePreferences.edit()
                .putString("themeMap", gson.toJson(Utilities.defaultLightTheme()))
                .apply()
        }
        val json = themePreferences.getString("themeMap", "")
        val typeOfHashMap: Type = object : TypeToken<Map<String?, String?>?>() {}.type
        val themeMap: Map<String, String> = gson.fromJson(json, typeOfHashMap)
        bootTheme(themeMap)
    }
}