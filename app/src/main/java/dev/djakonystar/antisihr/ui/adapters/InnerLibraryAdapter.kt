package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.data.models.AudioModel
import dev.djakonystar.antisihr.data.models.InnerLibraryModel
import dev.djakonystar.antisihr.data.models.LibraryModel
import dev.djakonystar.antisihr.databinding.ItemAudioBinding
import dev.djakonystar.antisihr.databinding.ItemInnerLibraryBinding
import dev.djakonystar.antisihr.databinding.ItemLibraryBinding

class InnerLibraryAdapter :
    ListAdapter<InnerLibraryModel, InnerLibraryAdapter.ViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(block: (String) -> Unit) {
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
                tvBody.text = d.body
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition).title)
            }
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<InnerLibraryModel>() {
        override fun areItemsTheSame(
            oldItem: InnerLibraryModel, newItem: InnerLibraryModel
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: InnerLibraryModel, newItem: InnerLibraryModel
        ): Boolean = oldItem.title == newItem.title && oldItem.body == newItem.body
    }
}