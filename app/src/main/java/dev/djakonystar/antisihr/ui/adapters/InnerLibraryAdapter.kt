package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.library.InnerLibraryBookmarkData
import dev.djakonystar.antisihr.databinding.ItemInnerLibraryBinding

class InnerLibraryAdapter :
    ListAdapter<InnerLibraryBookmarkData, InnerLibraryAdapter.ViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((InnerLibraryBookmarkData) -> Unit)? = null
    private var onBookmarkClickListener: ((InnerLibraryBookmarkData) -> Unit)? = null


    fun setOnItemClickListener(block: (InnerLibraryBookmarkData) -> Unit) {
        onItemClickListener = block
    }

    fun setOnBookmarkClickListener(block: (InnerLibraryBookmarkData) -> Unit) {
        onBookmarkClickListener = block
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemInnerLibraryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    inner class ViewHolder(private val binding: ItemInnerLibraryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val d = getItem(absoluteAdapterPosition)
            binding.apply {
                tvTitle.text = d.title
                tvBody.text = d.lead
                if (d.isBookmarked) {
                    icBookmarked.setImageResource(R.drawable.ic_saved_filled)
                } else {
                    icBookmarked.setImageResource(R.drawable.ic_saved)
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition))
            }

            binding.icBookmarked.setOnClickListener {
                getItem(absoluteAdapterPosition).isBookmarked =
                    !getItem(absoluteAdapterPosition).isBookmarked
                if (getItem(absoluteAdapterPosition).isBookmarked) {
                    binding.icBookmarked.setImageResource(R.drawable.ic_saved_filled)
                } else {
                    binding.icBookmarked.setImageResource(R.drawable.ic_saved)
                }
                onBookmarkClickListener?.invoke(getItem(absoluteAdapterPosition))
            }
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<InnerLibraryBookmarkData>() {
        override fun areItemsTheSame(
            oldItem: InnerLibraryBookmarkData, newItem: InnerLibraryBookmarkData
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: InnerLibraryBookmarkData, newItem: InnerLibraryBookmarkData
        ): Boolean = oldItem.title == newItem.title && oldItem.id == newItem.id
    }
}