package lahds.hasten.ui

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import lahds.hasten.R
import lahds.hasten.app.utils.Utilities
import lahds.hasten.ui.components.BaseFragment
import lahds.hasten.ui.components.Theme
import lahds.hasten.ui.models.User
import java.lang.reflect.Type

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        keyboardAnimation()

        activity = this
        applyTheme()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        window.statusBarColor = Theme.background
        window.navigationBarColor = Theme.background

        val container = findViewById<FrameLayout>(R.id.container)
        if (auth.currentUser != null) {
            database.reference.child("Users").child(auth.uid!!).
            addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            container.removeAllViews()
                            presentFragment(HomeActivity())
                        } else {
                            container.removeAllViews()
                            presentFragment(EditProfileActivity(), false)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        } else {
            container.removeAllViews()
            presentFragment(LoginActivity(), false)
        }
    }

    override fun onBackPressed() {
        if (activity.supportFragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            database.reference.child("Presence").child(auth.uid!!).setValue("online")
        }
    }

    override fun onPause() {
        super.onPause()
        if (auth.currentUser != null) {
            database.reference.child("Presence").child(auth.uid!!).setValue(System.currentTimeMillis())
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
        applyTheme()
    }

    private fun keyboardAnimation() {
        window.decorView.setOnApplyWindowInsetsListener(
            View.OnApplyWindowInsetsListener { view, windowInsets ->
                Utilities.transition(view)
                return@OnApplyWindowInsetsListener view.onApplyWindowInsets(windowInsets)
            })
    }

    companion object {
        lateinit var arguments: Any
        lateinit var activity: LaunchActivity

        lateinit var auth: FirebaseAuth
        lateinit var database: FirebaseDatabase

        fun presentFragment(fragment: BaseFragment, withBackStack: Boolean = true) {
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragment)
            if (withBackStack) {
                transaction.addToBackStack(fragment.tag)
            }
            transaction.commit()
        }

        fun applyTheme() {
            lateinit var themePreferences: SharedPreferences
            val gson = GsonBuilder().create()

            if (Utilities.isDarkTheme()) {
                themePreferences = activity.getSharedPreferences("darkTheme", MODE_PRIVATE)
                if (themePreferences.getString("themeMap", "") == "") {
                    themePreferences.edit()
                        .putString("themeMap", gson.toJson(Utilities.defaultDarkTheme()))
                        .apply()
                }
            } else {
                themePreferences = activity.getSharedPreferences("lightTheme", MODE_PRIVATE)
                if (themePreferences.getString("themeMap", "") == "") {
                    themePreferences.edit()
                        .putString("themeMap", gson.toJson(Utilities.defaultLightTheme()))
                        .apply()
                }
            }
            val json = themePreferences.getString("themeMap", "")
            val typeOfHashMap: Type = object : TypeToken<Map<String?, String?>?>() {}.type
            val themeMap: Map<String, String> = gson.fromJson(json, typeOfHashMap)
            Theme.bootTheme(themeMap)
        }
    }
}