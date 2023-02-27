package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.library.InnerLibraryBookmarkData
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.databinding.ItemInnerLibraryBinding

class InnerLibraryAdapter : RecyclerView.Adapter<InnerLibraryAdapter.ViewHolder>() {

    private var onItemClickListener: ((InnerLibraryBookmarkData) -> Unit)? = null
    private var onBookmarkClickListener: ((InnerLibraryBookmarkData) -> Unit)? = null


    fun setOnItemClickListener(block: (InnerLibraryBookmarkData) -> Unit) {
        onItemClickListener = block
    }

    fun setOnBookmarkClickListener(block: (InnerLibraryBookmarkData) -> Unit) {
        onBookmarkClickListener = block
    }

    var models = listOf<InnerLibraryBookmarkData>()
        set(value) {
            field = value
            notifyDataSetChanged()
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

    override fun getItemCount() = models.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    inner class ViewHolder(private val binding: ItemInnerLibraryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val d = models[absoluteAdapterPosition]
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
                onItemClickListener?.invoke(models[absoluteAdapterPosition])
            }

            binding.icBookmarked.setOnClickListener {
                models[absoluteAdapterPosition].isBookmarked =
                    !models[absoluteAdapterPosition].isBookmarked
                if (models[absoluteAdapterPosition].isBookmarked) {
                    binding.icBookmarked.setImageResource(R.drawable.ic_saved_filled)
                } else {
                    binding.icBookmarked.setImageResource(R.drawable.ic_saved)
                }
                onBookmarkClickListener?.invoke(models[absoluteAdapterPosition])
            }
        }
    }
}