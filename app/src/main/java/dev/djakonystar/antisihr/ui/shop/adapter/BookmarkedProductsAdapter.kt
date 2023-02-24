package dev.djakonystar.antisihr.ui.shop.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import dev.djakonystar.antisihr.databinding.ItemShopFavoriteBinding
import dev.djakonystar.antisihr.ui.adapters.LibraryAdapter
import dev.djakonystar.antisihr.utils.setImageWithGlide

class BookmarkedProductsAdapter : RecyclerView.Adapter<BookmarkedProductsAdapter.ProductsViewHolder>() {

    private var onItemLickListener:((ShopItemBookmarked)-> Unit)? = null
    private var onRemoveClickListener: ((ShopItemBookmarked,Int) -> Unit)? = null

    fun setOnRemoveClickListener(block: (ShopItemBookmarked, Int) -> Unit){
        this.onRemoveClickListener = block
    }
    fun setOnClickListener(block: (ShopItemBookmarked) -> Unit){
        this.onItemLickListener = block
    }

    var models = mutableListOf<ShopItemBookmarked>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    inner class ProductsViewHolder(val binding: ItemShopFavoriteBinding) :
        ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind() {
            val d = models[absoluteAdapterPosition]
            binding.tvName.text = d.name
            binding.tvPrice.text = d.price.toString()
            binding.ivProduct.setImageWithGlide(binding.root.context, d.image)
            binding.tvShop.text =
                "${itemView.context.getString(R.string.shop_name)} ${d.sellerName}"
        }
        init {
            binding.ivDelete.setOnClickListener {
                onRemoveClickListener?.invoke(models[absoluteAdapterPosition], absoluteAdapterPosition)
            }

            binding.root.setOnClickListener {
                onItemLickListener?.invoke(models[absoluteAdapterPosition])
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            ItemShopFavoriteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = models.size

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bind()
    }
}