package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.databinding.ItemLibraryBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class LibraryAdapter : RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {
    inner class LibraryViewHolder(private val binding: ItemLibraryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LibraryResultData) {
            binding.apply {
                tvTitle.text = item.name
                tvBody.text = item.description
                icImage.setImageWithGlide(binding.root.context, item.image)

                binding.root.setOnClickListener {
                    onItemClick.invoke(item)
                }
            }
        }
    }

    var librarySections = listOf<LibraryResultData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return librarySections.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_library, parent, false)
        val binding = ItemLibraryBinding.bind(v)
        return LibraryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(librarySections[position])
    }

    private var onItemClick: (item: LibraryResultData) -> Unit = { _ -> }
    fun setOnItemClickListener(onItemClick: (contact: LibraryResultData) -> Unit) {
        this.onItemClick = onItemClick
    }
}