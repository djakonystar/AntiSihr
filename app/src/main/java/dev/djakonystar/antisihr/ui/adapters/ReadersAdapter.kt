package dev.djakonystar.antisihr.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.djakonystar.antisihr.data.models.reader.ReaderData
import dev.djakonystar.antisihr.databinding.ItemReaderBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class ReadersAdapter : RecyclerView.Adapter<ReadersAdapter.ReadersViewHolder>() {

    private var onItemClickListener: ((ReaderData) -> Unit)? = null

    fun setOnItemClickListener(block: (ReaderData) -> Unit) {
        this.onItemClickListener = block
    }

    var models = listOf<ReaderData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = models.size

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
            val d = models[absoluteAdapterPosition]
            binding.apply {
                tvAuthor.text = "${d.surname} ${d.name}"
                icPhoto.setImageWithGlide(root.context, d.image)
                if (d.city != null) {
                    tvCityName.text = d.city.name
                }
                tvDescription.text = d.description
            }
        }

        init {
            binding.btnMore.setOnClickListener {
                onItemClickListener?.invoke(models[absoluteAdapterPosition])
            }

            binding.root.setOnClickListener {
                onItemClickListener?.invoke(models[absoluteAdapterPosition])
            }
        }
    }
}
