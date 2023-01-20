package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.djakonystar.antisihr.data.models.reader.SocialNetwork
import dev.djakonystar.antisihr.databinding.ItemSocialMediaBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class SocialMediaAdapter :
    ListAdapter<SocialNetwork, SocialMediaAdapter.SocialMediaViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((SocialNetwork) -> Unit)? = null

    fun setOnItemClickListener(block: ((SocialNetwork) -> Unit)) {
        onItemClickListener = block
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialMediaViewHolder {
        return SocialMediaViewHolder(
            ItemSocialMediaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SocialMediaViewHolder, position: Int) = holder.bind()

    inner class SocialMediaViewHolder(private val binding: ItemSocialMediaBinding) :
        ViewHolder(binding.root) {
        fun bind() {
            val d = getItem(absoluteAdapterPosition)
            binding.root.setImageWithGlide(binding.root.context, d.type.image)
        }
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<SocialNetwork>() {
        override fun areItemsTheSame(oldItem: SocialNetwork, newItem: SocialNetwork): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SocialNetwork, newItem: SocialNetwork): Boolean {
            return oldItem.url == newItem.url && oldItem.type == newItem.type
        }
    }
}
