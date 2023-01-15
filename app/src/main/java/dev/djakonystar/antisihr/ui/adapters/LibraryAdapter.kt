package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.data.models.AudioModel
import dev.djakonystar.antisihr.data.models.LibraryModel
import dev.djakonystar.antisihr.databinding.ItemAudioBinding
import dev.djakonystar.antisihr.databinding.ItemLibraryBinding

class LibraryAdapter : ListAdapter<LibraryModel, LibraryAdapter.ViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(block: (String) -> Unit) {
        onItemClickListener = block
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemLibraryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    inner class ViewHolder(private val binding: ItemLibraryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val d = getItem(absoluteAdapterPosition)
            binding.apply {
                tvTitle.text = d.name
                tvBody.text = d.body
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition).name)
            }
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<LibraryModel>() {
        override fun areItemsTheSame(
            oldItem: LibraryModel, newItem: LibraryModel
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: LibraryModel, newItem: LibraryModel
        ): Boolean = oldItem.name == newItem.name && oldItem.body == newItem.body
    }
}