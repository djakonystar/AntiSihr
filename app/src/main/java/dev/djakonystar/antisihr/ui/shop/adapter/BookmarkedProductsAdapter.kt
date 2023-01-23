package dev.djakonystar.antisihr.ui.shop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import dev.djakonystar.antisihr.databinding.ItemShopFavoriteBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class BookmarkedProductsAdapter :
    ListAdapter<ShopItemBookmarked, BookmarkedProductsAdapter.ProductsViewHolder>(MyDiffCallbacl) {

    private var onItemLickListener:((ShopItemBookmarked)-> Unit)? = null
    private var onRemoveClickListener: ((ShopItemBookmarked) -> Unit)? = null

    fun setOnRemoveClickListener(block: (ShopItemBookmarked) -> Unit){
        this.onRemoveClickListener = block
    }
    fun setOnClickListener(block: (ShopItemBookmarked) -> Unit){
        this.onItemLickListener = block
    }


    inner class ProductsViewHolder(val binding: ItemShopFavoriteBinding) :
        ViewHolder(binding.root) {
        fun bind() {
            val d = getItem(absoluteAdapterPosition)
            binding.tvName.text = d.name
            binding.tvPrice.text = d.price.toString()
            binding.ivProduct.setImageWithGlide(binding.root.context, d.image)
            binding.tvShop.text = d.sellerName
        }
        init {
            binding.ivDelete.setOnClickListener {
                onRemoveClickListener?.invoke(getItem(absoluteAdapterPosition))
            }

            binding.root.setOnClickListener {
                onItemLickListener?.invoke(getItem(absoluteAdapterPosition))
            }
        }
    }




    private object MyDiffCallbacl : DiffUtil.ItemCallback<ShopItemBookmarked>() {
        override fun areItemsTheSame(
            oldItem: ShopItemBookmarked, newItem: ShopItemBookmarked
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ShopItemBookmarked, newItem: ShopItemBookmarked
        ) =
            oldItem.id == newItem.id && oldItem.name == newItem.name && oldItem.sellerId == newItem.sellerId

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            ItemShopFavoriteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bind()
    }
}