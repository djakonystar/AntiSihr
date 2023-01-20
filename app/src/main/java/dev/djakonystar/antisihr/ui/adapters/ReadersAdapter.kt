package dev.djakonystar.antisihr.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.djakonystar.antisihr.data.models.reader.ReaderData
import dev.djakonystar.antisihr.databinding.ItemReaderBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class ReadersAdapter : ListAdapter<ReaderData, ReadersAdapter.ReadersViewHolder>(MyDiffUtil) {

    private var onDetailButtonClickListener: ((ReaderData) -> Unit)? = null

    fun setOnDetailButtonClickListener(block: (ReaderData) -> Unit) {
        this.onDetailButtonClickListener = block
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadersViewHolder {
        return ReadersViewHolder(
            ItemReaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReadersViewHolder, position: Int) {
        holder.bind()
    }

    inner class ReadersViewHolder(private val binding: ItemReaderBinding) :
        ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind() {
            val d = getItem(absoluteAdapterPosition)
            binding.apply {
                tvAuthor.text = "${d.surname} ${d.name}"
                icPhoto.setImageWithGlide(root.context, d.image)
                tvCityName.text = d.city.name
                tvDescription.text = d.description
            }
        }

        init {
            binding.btnMore.setOnClickListener {
                onDetailButtonClickListener?.invoke(getItem(absoluteAdapterPosition))
            }
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<ReaderData>() {
        override fun areItemsTheSame(oldItem: ReaderData, newItem: ReaderData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ReaderData, newItem: ReaderData): Boolean {
            return oldItem.name == newItem.name && oldItem.surname == newItem.surname &&
                    oldItem.description == newItem.description && oldItem.image == newItem.image
        }
    }
}
