package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.data.models.AudioModel
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.databinding.ItemAudioBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class AudioAdapter : ListAdapter<AudioBookmarked, AudioAdapter.ViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((AudioBookmarked) -> Unit)? = null
    private var onPlayClickListener: ((AudioBookmarked) -> Unit)? = null

    fun setOnItemClickListener(block: (AudioBookmarked) -> Unit) {
        onItemClickListener = block
    }

    fun setOnPlayClickListener(block: (AudioBookmarked) -> Unit) {
        onPlayClickListener = block
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemAudioBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    inner class ViewHolder(private val binding: ItemAudioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val d = getItem(absoluteAdapterPosition)
            binding.apply {
                tvTitle.text = d.name
                tvBody.text = d.author
                icImage.setImageWithGlide(binding.root.context,d.image)
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition))
            }

            binding.icPlay.setOnClickListener {
                onPlayClickListener?.invoke(getItem(absoluteAdapterPosition))
            }
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<AudioBookmarked>() {
        override fun areItemsTheSame(
            oldItem: AudioBookmarked, newItem: AudioBookmarked
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: AudioBookmarked, newItem: AudioBookmarked
        ): Boolean = oldItem.name == newItem.name && oldItem.author == newItem.author && oldItem.id==newItem.id
    }
}