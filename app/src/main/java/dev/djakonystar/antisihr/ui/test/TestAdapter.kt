package dev.djakonystar.antisihr.ui.test

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.databinding.ItemTestBinding

class TestAdapter: Adapter<TestAdapter.TestViewHolder>() {
    inner class TestViewHolder(private val binding: ItemTestBinding): ViewHolder(binding.root) {
        fun bind(testData: TestData) {
            binding.apply {
                tvNumber.text = testData.id.toString()
                tvTitle.text = testData.name
            }
        }
    }

    var models = listOf<TestData>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
        val binding = ItemTestBinding.bind(v)
        return TestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(models[position])
    }
}
