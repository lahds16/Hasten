package lahds.hasten.ui

import android.util.DisplayMetrics
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import lahds.hasten.databinding.ActivityChatBinding
import lahds.hasten.ui.adapters.MessagesAdapter
import lahds.hasten.ui.components.BaseFragment
import lahds.hasten.ui.models.Message
import lahds.hasten.ui.models.User
import java.text.SimpleDateFormat
import java.util.*


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

        initUser()
        initStatus()
        loadMessages()
        sendMessage()

        binding.toolbar.setNavigationOnClickListener {
            LaunchActivity.activity.onBackPressed()
        }
    }

    private fun initUser() {
        database.reference.child("Users")
            .child(receiverUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(User::class.java)!!
                        if (data.username != "") {
                            binding.textUsername.text = data.username
                            binding.textAvatar.text = data.username[0].toString().uppercase()
                        } else {
                            binding.textUsername.text = data.name
                            binding.textAvatar.text = data.name[0].toString().uppercase()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        database.reference.child("Presence")
            .child(receiverUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(String::class.java)!!

                        if (data != "online" && data != "typing...") {
                            val calendar = Calendar.getInstance()
                            val difference = calendar.timeInMillis.minus(data.toLong())

                            val dateFormat: SimpleDateFormat

                            if (difference < 60000) {
                                binding.textPresence.text = "last seen just now"
                            }
                            else {
                                if (difference < (60 * 60000)) {
                                    val time = difference/60000
                                    if (time > 1) {
                                        dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                        binding.textPresence.text = "last seen today at ${dateFormat.format(data)}"
                                    } else {
                                        binding.textPresence.text = "last seen within a minute"
                                    }
                                }
                                else {
                                    if (difference < (24 * (60 * 60000))) {
                                        val time = difference/(60 * 60000)
                                        if (time > 1) {
                                            dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                            binding.textPresence.text = "last seen today at ${dateFormat.format(data)}"
                                        } else {
                                            binding.textPresence.text = "last seen within an hour"
                                        }
                                    }
                                    else {
                                        if (difference < (48 * (60 * 60000))) {
                                            dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                            binding.textPresence.text = "last seen yesterday at ${dateFormat.format(data)}"
                                        }
                                        else {
                                            dateFormat = SimpleDateFormat("dddd 'at' hh:mm a", Locale.getDefault())
                                            binding.textPresence.text = "last seen on ${dateFormat.format(data)}"

                                            if (difference < 14515200000) {
                                                val time = difference/86400000
                                                if (time < 1) {
                                                    binding.textPresence.text = "last seen within a week"
                                                }
                                            } else {
                                                binding.textPresence.text = "last seen on" + SimpleDateFormat("EEEE, MMMM d").format(data)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            binding.textPresence.text = data
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadMessages() {
        database.reference.child("Chats")
            .child(senderRoom)
            .child("Messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)!!
                    if (message.messageID !in messageIDs) {
                        messageIDs.add(message.messageID)
                        if (message.messageID == receiverRoom && !message.isRead) {
                            message.isRead = true
                        }
                        messages.add(message)
                    }

                    if (adapter.itemCount >= 1) {
                        adapter.notifyItemInserted(adapter.itemCount - 1).let {
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

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)!!
                    adapter.notifyItemChanged(messages.indexOf(message))
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val message = snapshot.getValue(Message::class.java)!!
                    adapter.notifyItemRemoved(messages.indexOf(message))
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun sendMessage() {
        binding.inputSend.setOnClickListener {
            val messageText: String = binding.inputMessage.text.toString()
            if (messageText.isNotEmpty()) {
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