package lahds.hasten.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import lahds.hasten.app.utils.Utilities
import lahds.hasten.databinding.ActivityHomeBinding
import lahds.hasten.ui.adapters.ChatsAdapter
import lahds.hasten.ui.components.BaseFragment
import lahds.hasten.ui.components.Theme
import lahds.hasten.ui.models.User

class HomeActivity : BaseFragment() {
    private lateinit var binding: ActivityHomeBinding

    private var chats = ArrayList<User>()

    override fun createView(): View {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initialize() {
        initUser()
        initAnimations()

        binding.fab.setOnClickListener {
            LaunchActivity.presentFragment(CreateChatActivity())
        }

        val chatsAdapter = ChatsAdapter(requireContext(), chats)
        binding.listChats.layoutManager = LinearLayoutManager(requireContext())
        binding.listChats.adapter = chatsAdapter
        database.reference.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val user = item.getValue(User::class.java)
                    if (user!!.userId != auth.uid) {
                        chats.add(user)
                        chatsAdapter.notifyItemInserted(chats.size)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initUser() {
        database.reference.child("Users").child(auth.uid!!).
        addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)!!
                binding.textAvatar.text = user.name[0].toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initAnimations() {
        Utilities.animateClick(binding.fab)
        Utilities.animateClick(binding.avatar)
    }

    override fun updateViews() {
        LaunchActivity.activity.window.statusBarColor = Theme.toolbar
        LaunchActivity.activity.window.navigationBarColor = Theme.background
    }
}