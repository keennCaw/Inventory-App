package com.keennhoward.inventoryapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.keennhoward.inventoryapp.databinding.ProductItemBinding
import com.keennhoward.inventoryapp.model.Product

class MainAdapter(private val listener: ItemClickListener, private val context:Context):
    ListAdapter<Product, MainViewHolder>(MainDiffUtil()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val mainBinding = ProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MainViewHolder(mainBinding,listener, context)

    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getUserAt(position: Int):Product{
        return getItem(position)
    }

}

interface ItemClickListener{
    fun onProductItemClickListener(product: Product)
}

class MainViewHolder(
    private val binding: ProductItemBinding,
    private val listener: ItemClickListener,
    private val context: Context
): RecyclerView.ViewHolder(binding.root){

    fun bind(product: Product){

        binding.tvName.text = product.name
        binding.tvUnit.text = product.unit
        binding.tvExpDate.text = "exp: ${product.date}"
        binding.tvPrice.text = "₱${product.price.toString()}"
        binding.tvQuantity.text = "Qty ${product.qty.toString()}"

        val total = product.price*product.qty

        binding.tvTotalPrice.text = "₱${total.toString()}"


        Glide.with(context)
            .load(Uri.parse(product.image))
            .into(binding.image)

        binding.root.setOnClickListener {
            listener.onProductItemClickListener(product)
        }
    }
}

class MainDiffUtil: DiffUtil.ItemCallback<Product>(){
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}