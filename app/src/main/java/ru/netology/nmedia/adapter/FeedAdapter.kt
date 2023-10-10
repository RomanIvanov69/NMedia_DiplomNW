package ru.netology.nmedia.adapter

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.enumeration.EventType
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadAttachment

interface FeedOnInteractionListener {
    fun onLike(feedItem: FeedItem)
    fun onRemove(id: Int)
    fun onEdit(feedItem: FeedItem)
    fun onUser(userId: Int)
    fun onPlay(feedItem: FeedItem)
    fun onVideo(url: String)
    fun onImage(url: String)
}

class FeedAdapter(
    private val onInteractionListener: FeedOnInteractionListener,
) : PagingDataAdapter<FeedItem, FeedViewHolder>(FeedDiffCallback()) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: FeedViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach {
                (it as? Payload)?.let { payload ->
                    holder.bind(payload)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardPostBinding.inflate(inflater, parent, false)
        return FeedViewHolder(binding, onInteractionListener)
    }
}

class FeedViewHolder(
    private val binding: CardPostBinding,
    private val listener: FeedOnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(feedItem: FeedItem) {
        binding.apply {
            feedItem.authorAvatar?.let { avatar.load(it) }
                ?: avatar.setImageResource(R.drawable.ic_empty_avatar)
            author.text = feedItem.author
            authorJob.isVisible = feedItem.authorJob != null
            authorJob.text = feedItem.authorJob
            published.text = AndroidUtils.formatDateTime(feedItem.published)
            content.text = feedItem.content

            like.isChecked = feedItem.likedByMe
            like.text = feedItem.likeOwnerIds.size.toString()
            like.setOnClickListener { listener.onLike(feedItem) }

            menu.isVisible = feedItem.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(feedItem.id)
                                true
                            }

                            R.id.edit -> {
                                listener.onEdit(feedItem)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            attachmentImage.visibility = View.GONE
            music.visibility = View.GONE
            attachmentVideo.visibility = View.GONE
            if (feedItem.attachment != null) {
                when (feedItem.attachment?.type) {
                    AttachmentType.AUDIO -> {
                        music.visibility = View.VISIBLE
                        playButton.setOnClickListener { listener.onPlay(feedItem) }
                        musicTitle.text = feedItem.attachment?.url
                    }

                    AttachmentType.IMAGE -> {
                        attachmentImage.visibility = View.VISIBLE
                        feedItem.attachment?.url?.let { url ->
                            attachmentImage.loadAttachment(url)
                            attachmentImage.setOnClickListener { listener.onImage(url) }
                        }

                    }

                    AttachmentType.VIDEO -> {
                        attachmentVideo.visibility = View.VISIBLE
                        feedItem.attachment?.url?.let { url ->
                            attachmentVideo.setOnClickListener { listener.onVideo(url) }
                            val uri = Uri.parse(url)
                            attachmentVideo.setVideoURI(uri)
                            attachmentVideo.setOnPreparedListener { mp ->
                                mp?.setVolume(0F, 0F)
                                mp?.isLooping = true
                                attachmentVideo.start()
                            }
                        }
                    }

                    else -> Unit
                }
            }

            link.isVisible = feedItem.link != null
            link.text = feedItem.link

            if (feedItem is Event) {
                eventGroup.visibility = View.VISIBLE
                eventDate.text = AndroidUtils.formatDateTime(feedItem.datetime)
                eventType.text = feedItem.type.toString()
                when (feedItem.type) {
                    EventType.ONLINE -> formatOfEventIcon.setImageResource(R.drawable.is_rss_feed_24)
                    EventType.OFFLINE -> formatOfEventIcon.setImageResource(R.drawable.baseline_signal_wifi_off_24)
                }
            } else {
                eventGroup.visibility = View.GONE
            }

            avatar.setOnClickListener {
                listener.onUser(feedItem.authorId)
            }
        }

    }

    fun bind(payload: Payload) {
        payload.likeByMe?.let {
            binding.like.isChecked = it
        }

        payload.content?.let {
            binding.content.text = it
        }
    }
}

data class Payload(
    val likeByMe: Boolean? = null,
    val content: String? = null,
)


class FeedDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}

