package ru.netology.nmedia.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardJobBinding
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.util.AndroidUtils

interface Listener {
    fun onEdit(job: Job)
    fun onRemove(job: Job)
}

class JobAdapter(
    private val listener: Listener,
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardJobBinding.inflate(inflater, parent, false)
        return JobViewHolder(binding, listener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}

class JobViewHolder(
    private val binding: CardJobBinding,
    private val listener: Listener,
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(job: Job) {
        with(binding) {
            workName.text = job.name
            workPosition.text = job.position
            workStart.text = AndroidUtils.formatDate(job.start)
            jobFinish.text = job.finish?.let { AndroidUtils.formatDate(it) } ?: ""
            menu.isVisible = job.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(job)
                                true
                            }

                            R.id.edit -> {
                                listener.onEdit(job)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}