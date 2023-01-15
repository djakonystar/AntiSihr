package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.data.models.AudioModel
import dev.djakonystar.antisihr.databinding.ItemAudioBinding

class AudioAdapter : ListAdapter<AudioModel, AudioAdapter.ViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(block: (String) -> Unit) {
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
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition).name)
            }
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<AudioModel>() {
        override fun areItemsTheSame(
            oldItem: AudioModel, newItem: AudioModel
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: AudioModel, newItem: AudioModel
        ): Boolean = oldItem.name == newItem.name && oldItem.author == newItem.author
    }
}