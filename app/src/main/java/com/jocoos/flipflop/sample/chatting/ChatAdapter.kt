package com.jocoos.flipflop.sample.chatting

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.FFMessage
import com.jocoos.flipflop.FFMessageType
import com.jocoos.flipflop.sample.R
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class ChatAdapter : RecyclerView.Adapter<ChatItemViewHolder>() {
    private var items: List<ChatItem> = emptyList()

    private val infoResource = R.layout.chat_info_item
    private val messageResource = R.layout.chat_message_item

    companion object {
        private const val MAX_COUNT = 100
        private const val REMOVE_COUNT = 10
    }

    fun updateItems(items: List<ChatItem>) {
        this.items = items
    }

    fun addItems(items: List<ChatItem>) {
        this.items += items
        notifyItemRangeInserted(items.size - 1, items.size)
    }

    fun removeAll() {
        this.items = emptyList()
        notifyDataSetChanged()
    }

    fun addItem(item: FFMessage) {
        when (item.messageType) {
            FFMessageType.JOIN -> {
                addItem(ChatItem.Info(message = "${item.username} joined the room."))
            }
            FFMessageType.MESSAGE -> {
                addItem(ChatItem.Message(mainMessage = item.message, subMessage = item.username, imageUrl = item.profileUrl))
            }
            FFMessageType.LEAVE -> {
                addItem(ChatItem.Info(message = "${item.username} left the room."))
            }
            FFMessageType.ADMIN -> {
                addItem(ChatItem.Info(message = item.message))
            }
            else -> {
                addItem(ChatItem.Info(message = item.message))
            }
        }
    }

    fun addItem(item: ChatItem) {
        if (this.items.size >= MAX_COUNT) {
            this.items = this.items.drop(REMOVE_COUNT)
            notifyItemRangeRemoved(0, REMOVE_COUNT)
        }

        this.items += item
        notifyItemInserted(items.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        return when (viewType) {
            infoResource -> ChatInfoViewHolder(view)
            messageResource -> ChatMessageViewHolder(view)
            else -> ChatInfoViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.setItem(items[position])
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is ChatItem.Info -> infoResource
        is ChatItem.Message -> messageResource
    }
}

abstract class ChatItemViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    abstract fun setItem(item: ChatItem)
}

class ChatInfoViewHolder(root: View) : ChatItemViewHolder(root) {
    private val message: TextView = root.findViewById(R.id.system_message)

    private var item: ChatItem.Info? = null
    override fun setItem(item: ChatItem) {
        if (item is ChatItem.Info) {
            this.item = item
            render()
        }
    }

    private fun render() {
        val item = this.item!!

        message.text = item.message
    }
}

class ChatMessageViewHolder(private val root: View) : ChatItemViewHolder(root) {
    private val avatar: ImageView = root.findViewById(R.id.avatar)
    private val mainMessage: TextView = root.findViewById(R.id.main_message)
    private val subMessage: TextView = root.findViewById(R.id.sub_message)

    private var item: ChatItem.Message? = null

    override fun setItem(item: ChatItem) {
        if (item is ChatItem.Message) {
            this.item = item
            render()
        }
    }

    private fun render() {
        val item = this.item!!

        mainMessage.text = item.mainMessage
        subMessage.text = item.subMessage

        if (!item.imageUrl.isNullOrEmpty()) {
            val radius = getDimensionSize(32)

            Picasso.with(root.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.img_profile_default)
                .transform(CropCircleTransformation())
                .resize(radius, radius)
                .centerCrop()
                .into(avatar)
        }
    }

    fun getDimensionSize(size: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            size.toFloat(), root.context.resources.displayMetrics).toInt()
    }
}
