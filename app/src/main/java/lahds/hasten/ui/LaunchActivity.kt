package lahds.hasten.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import lahds.hasten.R
import lahds.hasten.app.utils.Utilities
import lahds.hasten.ui.components.BaseFragment

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        keyboardAnimation()

        activity = this
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        if (auth.currentUser != null) {
            /*database.reference.child("Users").child(auth.uid!!).
            addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        presentFragment(HomeActivity())
                    } else {
                        presentFragment(EditProfileActivity(), false)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })*/
            presentFragment(HomeActivity())
        } else {
            presentFragment(LoginActivity(), false)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 2) {
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
    }
}