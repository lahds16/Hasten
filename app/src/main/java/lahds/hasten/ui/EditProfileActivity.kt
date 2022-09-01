package lahds.hasten.ui

import android.view.View
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import lahds.hasten.app.utils.Utilities
import lahds.hasten.databinding.ActivityEditProfileBinding
import lahds.hasten.ui.components.BaseFragment
import lahds.hasten.ui.components.Theme
import lahds.hasten.ui.models.User

class EditProfileActivity : BaseFragment() {
    private lateinit var binding: ActivityEditProfileBinding

    override fun createView(): View {
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initialize() {
        Utilities.animateClick(binding.textContinue)
        database.reference.child("Users").child(auth.uid!!).
        addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(User::class.java)!!
                    binding.textName.setText(data.name)
                    binding.textUsername.setText(data.username)
                    binding.textBio.setText(data.bio)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.root, error.message, Snackbar.LENGTH_LONG).show()
            }
        })

        val userId = auth.uid
        val phoneNo = auth.currentUser!!.phoneNumber

        binding.textContinue.setOnClickListener {

            var bio = ""
            var username = ""

            if (binding.textName.text.isNullOrEmpty()) {
                binding.textName.error = "This is required."
                binding.textContinue.isEnabled = true
            } else {
                binding.textContinue.isEnabled = false
                val name = binding.textName.text.toString().trim()

                if (!binding.textUsername.text.isNullOrEmpty()) {
                    username = binding.textUsername.text.toString().trim()
                }

                if (!binding.textBio.text.isNullOrEmpty()) {
                    bio = binding.textBio.text.toString().trim()
                }

                val user = User(name, username, bio, userId!!, phoneNo!!)

                database.reference
                    .child("Users")
                    .child(userId)
                    .setValue(user)
                    .addOnSuccessListener {
                        LaunchActivity.presentFragment(HomeActivity())
                    }
            }
        }

        binding.textName.addTextChangedListener {
            if (binding.textAvatar.text.isNotEmpty()) {
                val text = binding.textName.text.toString()
                binding.textAvatar.text = text[0].uppercase()
            }
        }
    }

    override fun updateViews() {
        LaunchActivity.activity.window.statusBarColor = Theme.background
        LaunchActivity.activity.window.navigationBarColor = Theme.background
    }
}