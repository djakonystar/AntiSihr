package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import dev.djakonystar.antisihr.databinding.ItemShopBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class ShopsAdapter : ListAdapter<ShopItemBookmarked, ShopsAdapter.ShopViewHolder>(MyDiffUtil) {

    private var onItemClickListener: ((ShopItemBookmarked) -> Unit)? = null

    fun setOnItemClickListener(block: ((ShopItemBookmarked) -> Unit)) {
        onItemClickListener = block
    }

    inner class ShopViewHolder(val binding: ItemShopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val d = getItem(absoluteAdapterPosition)
            binding.ivProduct.setImageWithGlide(binding.root.context, d.image)
            binding.tvName.text = d.name
            binding.tvPeace.text = d.weight
            binding.tvPrice.text = d.price.toString()
            if (d.isFavourite) {
                binding.ivFavorite.setImageResource(R.drawable.ic_favourites_filled)
            } else {
                binding.ivFavorite.setImageResource(R.drawable.ic_favourites)
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        return ShopViewHolder(
            ItemShopBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind()
    }

    private object MyDiffUtil : DiffUtil.ItemCallback<ShopItemBookmarked>() {
        override fun areItemsTheSame(
            oldItem: ShopItemBookmarked, newItem: ShopItemBookmarked
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ShopItemBookmarked, newItem: ShopItemBookmarked
        ): Boolean = oldItem.name == newItem.name && oldItem.id == newItem.id
    }
}