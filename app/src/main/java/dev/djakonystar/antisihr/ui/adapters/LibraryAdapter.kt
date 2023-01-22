package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.databinding.ItemLibraryBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class LibraryAdapter : ListAdapter<LibraryResultData, LibraryAdapter.ViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((LibraryResultData) -> Unit)? = null

    fun setOnItemClickListener(block: (LibraryResultData) -> Unit) {
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
                icImage.setImageWithGlide(binding.root.context, d.image)
                tvTitle.text = d.name
                tvBody.text = d.description
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition))
            }
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<LibraryResultData>() {
        override fun areItemsTheSame(
            oldItem: LibraryResultData, newItem: LibraryResultData
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: LibraryResultData, newItem: LibraryResultData
        ): Boolean =
            oldItem.id == newItem.id && oldItem.name == newItem.name && oldItem.description == newItem.description && oldItem.image == newItem.image
    }
}