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
import lahds.hasten.ui.models.User

class HomeActivity : BaseFragment() {
    private lateinit var binding: ActivityHomeBinding

    private var chats = ArrayList<User>()

    override fun createView(): View {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initialize() {
        initWindow()
        initAnimations()

        binding.fab.setOnClickListener {
            presentFragment(CreateChatActivity())
        }

        val chatsAdapter = ChatsAdapter(requireContext(), chats)
        binding.listChats.layoutManager = LinearLayoutManager(requireContext())
        binding.listChats.adapter = chatsAdapter
        database.reference.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val user = item.getValue(User::class.java)
                    if (user!!.userId == auth.uid) {
                        chats.add(user)
                        chatsAdapter.notifyItemInserted(chats.size)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initWindow() {
    }

    private fun initAnimations() {
        Utilities.animateClick(binding.fab)
        Utilities.animateClick(binding.avatar)
    }
}