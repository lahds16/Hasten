package lahds.hasten.app

import android.app.Application
import android.content.Context

class HastenApp : Application() {
    companion object {
        lateinit var applicationContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        Companion.applicationContext = this
    }
}