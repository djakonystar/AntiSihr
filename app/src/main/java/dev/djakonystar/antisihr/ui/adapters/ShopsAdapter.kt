package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import dev.djakonystar.antisihr.databinding.ItemLibraryBinding
import dev.djakonystar.antisihr.databinding.ItemShopBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class ShopsAdapter : RecyclerView.Adapter<ShopsAdapter.ShopViewHolder>() {
    inner class ShopViewHolder(private val binding: ItemShopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShopItemBookmarked) {
            binding.apply {
                tvName.text = item.name
                tvPeace.isVisible = item.weight != 0.0
                tvPeace.text = (item.weight ?: 0).toString()
                val price = if (item.price % 1.0 == 0.0) item.price.toInt().toString()
                else item.price.toString()
                tvPrice.text = binding.root.context.getString(R.string.rub, price)
                ivProduct.setImageWithGlide(binding.root.context, item.image)
                if (item.isFavourite) {
                    ivFavorite.setImageResource(R.drawable.ic_favourites_filled)
                } else {
                    ivFavorite.setImageResource(R.drawable.ic_favourites)
                }
                binding.root.setOnClickListener {
                    onItemClick.invoke(item)
                }
                binding.ivFavorite.setOnClickListener {
                    if (item.isFavourite) {
                        ivFavorite.setImageResource(R.drawable.ic_favourites)
                    } else {
                        ivFavorite.setImageResource(R.drawable.ic_favourites_filled)
                    }
                    onItemBookmarkClick.invoke(item)
                }
            }
        }
    }

    var shopItems = listOf<ShopItemBookmarked>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return shopItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_shop, parent, false)
        val binding = ItemShopBinding.bind(v)
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(shopItems[position])
    }

    private var onItemClick: (item: ShopItemBookmarked) -> Unit = { _ -> }
    fun setOnItemClickListener(onItemClick: (contact: ShopItemBookmarked) -> Unit) {
        this.onItemClick = onItemClick
    }

    private var onItemBookmarkClick: (item: ShopItemBookmarked) -> Unit = { _ -> }
    fun setOnItemBookmarkClickListener(onItemClick: (contact: ShopItemBookmarked) -> Unit) {
        this.onItemBookmarkClick = onItemClick
    }
}