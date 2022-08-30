package lahds.hasten.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import lahds.hasten.R
import lahds.hasten.databinding.LayoutReceiveBinding
import lahds.hasten.databinding.LayoutSentBinding
import lahds.hasten.ui.ChatActivity
import lahds.hasten.ui.models.Message

class MessagesAdapter(
    private var context: Context,
    messages: ArrayList<Message>,
    senderRoom: String,
    receiverRoom: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messages: ArrayList<Message>
    private val itemSent = 1
    private val itemReceive = 2
    private var senderRoom: String
    private var receiverRoom: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == itemSent) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.layout_sent, parent, false)
            SentViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.layout_receive, parent, false)
            ReceiveViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message: Message = messages[position]
        return if (FirebaseAuth.getInstance().uid == message.senderID) {
            itemSent
        } else {
            itemReceive
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        ChatActivity.currentPosition = position + 2
        val message: Message = messages[position]
        val viewHolder: RecyclerView.ViewHolder

        if (holder.javaClass == SentViewHolder::class.java) {
            viewHolder = holder as SentViewHolder
            viewHolder.binding.textMessage.text = message.message

            if (message.isRead) {
                viewHolder.binding.icRead.setImageResource(R.drawable.message_read)
            } else {
                viewHolder.binding.icRead.setImageResource(R.drawable.message_sent)
            }

        } else {
            viewHolder = holder as ReceiveViewHolder
            viewHolder.binding.textMessage.text = message.message
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: LayoutSentBinding

        init {
            binding = LayoutSentBinding.bind(itemView)
        }
    }

    inner class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: LayoutReceiveBinding

        init {
            binding = LayoutReceiveBinding.bind(itemView)
        }
    }

    init {
        this.messages = messages
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }
}