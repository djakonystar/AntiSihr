package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.data.models.AudioModel
import dev.djakonystar.antisihr.data.models.InnerLibraryModel
import dev.djakonystar.antisihr.data.models.LibraryModel
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import dev.djakonystar.antisihr.databinding.ItemAudioBinding
import dev.djakonystar.antisihr.databinding.ItemInnerLibraryBinding
import dev.djakonystar.antisihr.databinding.ItemLibraryBinding

class InnerLibraryAdapter :
    ListAdapter<InnerLibraryResultData, InnerLibraryAdapter.ViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((InnerLibraryResultData) -> Unit)? = null

    fun setOnItemClickListener(block: (InnerLibraryResultData) -> Unit) {
        onItemClickListener = block
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
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition))
            }
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<InnerLibraryResultData>() {
        override fun areItemsTheSame(
            oldItem: InnerLibraryResultData, newItem: InnerLibraryResultData
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: InnerLibraryResultData, newItem: InnerLibraryResultData
        ): Boolean = oldItem.title == newItem.title && oldItem.id== newItem.id
    }
}