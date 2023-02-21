package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.data.models.ListOfTestsResultData
import dev.djakonystar.antisihr.databinding.ItemTestBinding

class TestAdapter : ListAdapter<ListOfTestsResultData, TestAdapter.ViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((Int,String) -> Unit)? = null

    fun setOnItemClickListener(block: (Int,String) -> Unit) {
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
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition).id,getItem(absoluteAdapterPosition).name)
            }
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<ListOfTestsResultData>() {
        override fun areItemsTheSame(
            oldItem: ListOfTestsResultData, newItem: ListOfTestsResultData
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ListOfTestsResultData, newItem: ListOfTestsResultData
        ): Boolean = oldItem.name == newItem.name && oldItem.id == newItem.id
    }
}