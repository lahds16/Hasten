package lahds.hasten.ui

import android.view.View
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import lahds.hasten.databinding.ActivityEditProfileBinding
import lahds.hasten.ui.components.BaseFragment
import lahds.hasten.ui.models.User

class EditProfileActivity : BaseFragment() {
    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var user: User

    override fun createView(): View {
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initialize() {
        database.reference.child("Users").child(auth.uid!!).
        addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                binding.textName.setText(user.name)
                binding.textUsername.setText(user.username)
                binding.textBio.setText(user.bio)
            }
            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.root, error.message, Snackbar.LENGTH_LONG).show()
            }
        })

        val userId = auth.uid
        val phoneNo = auth.currentUser!!.phoneNumber

        binding.textContinue.setOnClickListener {

            if (binding.textName.text.isNullOrEmpty()) {
                binding.textName.error = "This is required."
            } else {
                val name = binding.textName.text.toString()

                var username = ""
                var bio = ""
                if (!binding.textUsername.text.isNullOrEmpty()) {
                    username = binding.textUsername.text.toString()
                }
                if (!binding.textBio.text.isNullOrEmpty()) {
                    bio = binding.textUsername.text.toString()
                }

                user = User(name, username, bio, userId!!, phoneNo!!)

                database.reference.child("Users").child(auth.uid!!).setValue(user)
                    .addOnSuccessListener {
                        presentFragment(HomeActivity())
                    }
            }
        }

        binding.textName.addTextChangedListener {
            if (binding.textAvatar.text.isNotEmpty()) {
                binding.textAvatar.text = binding.textName.text.toString()[0].uppercase()
            }
        }
    }
}