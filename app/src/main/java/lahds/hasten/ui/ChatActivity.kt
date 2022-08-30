package lahds.hasten.ui

import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import lahds.hasten.databinding.ActivityChatBinding
import lahds.hasten.ui.adapters.MessagesAdapter
import lahds.hasten.ui.components.BaseFragment
import lahds.hasten.ui.models.Message


class ChatActivity : BaseFragment() {
    private lateinit var binding: ActivityChatBinding

    private lateinit var receiverUid: String
    private lateinit var senderUid: String
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    val messageIDs = ArrayList<String>()

    private var messages = ArrayList<Message>()
    private lateinit var adapter: MessagesAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var slowSmoothScroller: LinearSmoothScroller
    private lateinit var fastSmoothScroller: LinearSmoothScroller

    companion object {
        var currentPosition = 2
    }

    override fun createView(): View {
        binding = ActivityChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initialize() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        slowSmoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 1f
            }
        }

        fastSmoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 0.2f
            }
        }

        senderUid = auth.uid!!
        receiverUid = LaunchActivity.arguments as String
        senderRoom =  senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        adapter = MessagesAdapter(requireContext(), messages, senderRoom, receiverRoom)
        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.listMessages.layoutManager = layoutManager
        binding.listMessages.adapter = adapter

        initStatus()
        loadMessages()
        sendMessage()
    }

    private fun loadMessages() {
        database.reference.child("Chats")
            .child(senderRoom)
            .child("Messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        val message = item.getValue(Message::class.java)!!
                        if (message.messageID !in messageIDs) {
                            messageIDs.add(message.messageID)
                            if (message.messageID == receiverUid && !message.isRead) {
                                message.isRead = true
                            }
                            messages.add(message)
                        }
                    }
                    if (messages.isNotEmpty()) {
                        adapter.notifyItemInserted(adapter.itemCount - 1).let {
                            Toast.makeText(requireContext(), "${currentPosition}, ${adapter.itemCount}", Toast.LENGTH_LONG).show()
                            if (currentPosition == adapter.itemCount) {
                                slowSmoothScroller.targetPosition = adapter.itemCount - 1
                                layoutManager.startSmoothScroll(slowSmoothScroller)
                            } else {
                                fastSmoothScroller.targetPosition = adapter.itemCount - 1
                                layoutManager.startSmoothScroll(fastSmoothScroller)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun sendMessage() {
        binding.inputSend.setOnClickListener {
            val messageText: String = binding.inputMessage.text.toString()
            binding.inputMessage.setText("")

            val randomKey = database.reference.push().key
            val message = Message(randomKey!!, messageText, senderUid, System.currentTimeMillis())
            database.reference.child("Chats")
                .child(senderRoom)
                .child("Messages")
                .child(randomKey)
                .setValue(message)
                .addOnSuccessListener {
                    database.reference.child("Chats")
                        .child(receiverRoom)
                        .child("Messages")
                        .child(randomKey)
                        .setValue(message)
                        .addOnSuccessListener {
                            val lastMessageObject: HashMap<String, Any> = HashMap()
                            lastMessageObject["lastMessage"] = message.message
                            lastMessageObject["lastMessageTime"] = System.currentTimeMillis()
                            database.reference.child("Chats").child(senderRoom)
                                .updateChildren(lastMessageObject)
                            database.reference.child("Chats").child(receiverRoom)
                                .updateChildren(lastMessageObject)
                        }
                }
        }
    }

    private fun initStatus() {
        binding.inputMessage.addTextChangedListener {
            if (binding.inputMessage.text.isNotEmpty()) {
                database.reference.child("Presence").child(senderUid).setValue("typing...")
            } else {
                database.reference.child("Presence").child(senderUid).setValue("online")
            }
        }
    }
}