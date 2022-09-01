package lahds.hasten.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import lahds.hasten.R
import lahds.hasten.databinding.LayoutChatBinding
import lahds.hasten.ui.ChatActivity
import lahds.hasten.ui.LaunchActivity
import lahds.hasten.ui.components.Theme
import lahds.hasten.ui.models.User
import java.text.SimpleDateFormat
import java.util.*

class ChatsAdapter(private var context: Context, private var chats: ArrayList<User>) :
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = chats[position]
        val senderId = FirebaseAuth.getInstance().uid
        val senderRoom = senderId + user.userId

        holder.binding.textName.setTextColor(Theme.title)
        FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(senderRoom)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("lastMessage")
                                .exists() && snapshot.child("lastMessageTime").exists()
                        ) {
                            val lastMessage = snapshot.child("lastMessage").getValue(String::class.java)
                            val time = snapshot.child("lastMessageTime").getValue(Long::class.java)!!
                            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            holder.binding.textMessage.text = lastMessage
                            holder.binding.textTime.text = dateFormat.format(Date(time))
                        }
                    } else {
                        holder.binding.textMessage.text = "No history"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        if (user.username != "") {
            holder.binding.textName.text = user.username
            holder.binding.textAvatar.text = user.username[0].toString().uppercase()
        } else {
            holder.binding.textName.text = user.name
            holder.binding.textAvatar.text = user.name[0].toString().uppercase()
        }

        holder.itemView.setOnClickListener {
            LaunchActivity.arguments = user.userId
            LaunchActivity.presentFragment(ChatActivity())
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: LayoutChatBinding

        init {
            binding = LayoutChatBinding.bind(itemView)
        }
    }
}