package lahds.hasten.app

import android.app.Application
import android.content.Context
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider

class HastenApp : Application() {
    companion object {
        lateinit var applicationContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        EmojiManager.install(IosEmojiProvider())
        Companion.applicationContext = this
    }
}