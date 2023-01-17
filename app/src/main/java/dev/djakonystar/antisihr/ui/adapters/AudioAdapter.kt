package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.data.models.AudioModel
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.databinding.ItemAudioBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class AudioAdapter : ListAdapter<AudioResultData, AudioAdapter.ViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((AudioResultData) -> Unit)? = null
    private var onPlayClickListener: ((AudioResultData) -> Unit)? = null

    fun setOnItemClickListener(block: (AudioResultData) -> Unit) {
        onItemClickListener = block
    }

    fun setOnPlayClickListener(block: (AudioResultData) -> Unit) {
        onItemClickListener = block
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

    private object MyDiffUtil : DiffUtil.ItemCallback<AudioResultData>() {
        override fun areItemsTheSame(
            oldItem: AudioResultData, newItem: AudioResultData
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: AudioResultData, newItem: AudioResultData
        ): Boolean = oldItem.name == newItem.name && oldItem.author == newItem.author
    }
}