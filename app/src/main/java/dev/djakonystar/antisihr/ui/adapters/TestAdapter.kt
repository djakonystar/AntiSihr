package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.data.models.AudioModel
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.databinding.ItemAudioBinding
import dev.djakonystar.antisihr.databinding.ItemTestBinding

class TestAdapter : ListAdapter<TestData, TestAdapter.ViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(block: (String) -> Unit) {
        onItemClickListener = block
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemTestBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    inner class ViewHolder(private val binding: ItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val d = getItem(absoluteAdapterPosition)
            binding.apply {
                tvNumber.text = d.id.toString()
                tvTitle.text = d.name
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition).name)
            }
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<TestData>() {
        override fun areItemsTheSame(
            oldItem: TestData, newItem: TestData
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: TestData, newItem: TestData
        ): Boolean = oldItem.name == newItem.name && oldItem.id == newItem.id
    }
}