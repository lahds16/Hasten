package lahds.hasten.ui.adapters

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import lahds.hasten.R
import lahds.hasten.databinding.LayoutReceiveBinding
import lahds.hasten.databinding.LayoutSentBinding
import lahds.hasten.ui.ChatActivity
import lahds.hasten.ui.LaunchActivity
import lahds.hasten.ui.components.Theme
import lahds.hasten.ui.models.Message
import java.text.SimpleDateFormat
import java.util.*


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
    private var eraser = Paint()

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

            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            holder.binding.timestamp.text = dateFormat.format(Date(message.timestamp))

            viewHolder.binding.drawable.adaptiveBubble(true, Theme.primary, position)
        } else {
            viewHolder = holder as ReceiveViewHolder
            viewHolder.binding.textMessage.text = message.message
            viewHolder.binding.textMessage.setTextColor(Theme.receiverText)
            viewHolder.binding.drawable.adaptiveBubble(false, Theme.receiverMessage, position)
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

    private fun View.round(color: Int, topLeft: Int, bottomLeft: Int, topRight: Int, bottomRight: Int) {
        val drawable = GradientDrawable()
        drawable.setColor(color)
        drawable.cornerRadii = floatArrayOf(
            topLeft.toFloat(),
            topLeft.toFloat(),
            bottomLeft.toFloat(),
            bottomLeft.toFloat(),
            topRight.toFloat(),
            topRight.toFloat(),
            bottomRight.toFloat(),
            bottomRight.toFloat()
        )

        background = drawable
    }

    private fun View.adaptiveBubble(isOwner: Boolean, color: Int, position: Int) {
        var userId = LaunchActivity.auth.uid

        if (isOwner) {
            if (messages.size == 0  && position == 0) {
                round(color, 40, 40, 40, 40)
            } else {
                if (messages.size > 1) {
                    if (position == 0) {
                        if (messages[position + 1].senderID == userId) {
                            round(color, 40, 40, 20, 40)
                        } else {
                            round(color, 40, 40, 40, 40)
                        }
                    } else {
                        if (position == messages.size - 1 && messages[position - 1].senderID == userId) {
                            round(color, 40, 20, 40, 40)
                        } else {
                            if (messages[position - 1].senderID == userId && messages[position + 1].senderID == userId) {
                                round(color, 40, 20, 20, 40)
                            } else {
                                if (messages[position - 1].senderID != userId) {
                                    round(color, 40, 40, 20, 40)
                                }
                                if (messages[position + 1].senderID != userId) {
                                    round(color, 40, 20, 40, 40)
                                }
                                if (messages[position - 1].senderID != userId && messages[position + 1].senderID != userId) {
                                    round(color, 40, 40, 40, 40)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            userId = messages[position].senderID
            if (messages.size == 0  && position == 0) {
                round(color, 40, 40, 40, 40)
            } else {
                if (messages.size > 1) {
                    if (position == 0) {
                        if (messages[position + 1].senderID == userId) {
                            round(color, 40, 40, 20, 40)
                        } else {
                            round(color, 40, 40, 40, 40)
                        }
                    } else {
                        if (position == messages.size - 1 && messages[position - 1].senderID == userId) {
                            round(color, 20, 40, 40, 40)
                        } else {
                            if (messages[position - 1].senderID == userId && messages[position + 1].senderID == userId) {
                                round(color, 20, 40, 40, 20)
                            } else {
                                if (messages[position - 1].senderID != userId) {
                                    round(color, 40, 40, 40, 20)
                                }
                                if (messages[position + 1].senderID != userId) {
                                    round(color, 20, 40, 40, 40)
                                }
                                if (messages[position - 1].senderID != userId && messages[position + 1].senderID != userId) {
                                    round(color, 40, 40, 40, 40)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupEraser() {
        eraser.color = ContextCompat.getColor(context, android.R.color.transparent)
        eraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        eraser.isAntiAlias = true
    }
}