package camp.codelab.firebase

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class MessagesAdapter(val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    public fun newMessage(message: Message) {
        messages.add(0, message)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            1 -> {
                MessageTextViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_message_text,
                        parent,
                        false
                    )
                )
            }
            2 -> {
                MessageImageViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_message_image,
                        parent,
                        false
                    )
                )

            }
            else -> {
                MessageTextViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_message_text,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return when (messages[position].type) {
            Message.Types.TEXT -> 1
            Message.Types.IMAGE -> 2
            else -> -1
        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(viewHolder: MessageViewHolder, position: Int) {

        if (viewHolder is MessageTextViewHolder) {
            viewHolder.setTextMessage(messages[position])
        } else if (viewHolder is MessageImageViewHolder) {
            viewHolder.setImageMessage(messages[position])
        }

    }


    open inner class MessageViewHolder(v: View) : RecyclerView.ViewHolder(v)

    inner class MessageTextViewHolder(val v: View) : MessageViewHolder(v) {

        fun setTextMessage(message: Message) {
            v.findViewById<TextView>(R.id.messageTextView).text = message.text
        }

    }

    inner class MessageImageViewHolder(val v: View) : MessageViewHolder(v) {

        fun setImageMessage(message: Message) {
            val imageView = v.findViewById<ImageView>(R.id.messageImageView)

            Picasso.get()
                .load(message.text)
                .into(imageView)
        }

    }


}
