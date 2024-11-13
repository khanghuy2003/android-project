package com.example.kotlin_cafeshop.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlin_cafeshop.Activity.DetailProductsActivity
import com.example.kotlin_cafeshop.Model.Products
import com.example.kotlin_cafeshop.R
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat
import java.util.Locale

class AllProductsAdapter(val arrayAllProducts: ArrayList<Products>):RecyclerView.Adapter<AllProductsAdapter.ViewHolder>() {
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

        var product_image=itemView.findViewById<ImageView>(R.id.product_image)
        var product_name=itemView.findViewById<TextView>(R.id.product_name)
        var product_price=itemView.findViewById<TextView>(R.id.product_price)

        init {
            itemView.setOnClickListener{ view ->
                val product:Products = arrayAllProducts[adapterPosition]
                val intent= Intent(view.context, DetailProductsActivity::class.java)
                intent.putExtra("SelectedProducts",product)
                view.context.startActivity(intent)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_allproducts,parent,false))
    }

    override fun getItemCount(): Int = arrayAllProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem=arrayAllProducts[position]

        holder.product_name.text=currentItem.product_name.toString()
        val formatProductPrice= NumberFormat.getNumberInstance(Locale("vi", "VN")).format(currentItem.product_price)
        holder.product_price.text="$formatProductPrice"+"đ"

        val storageRef=FirebaseStorage.getInstance().getReference()
        val imageuri=storageRef.child(currentItem.product_imageurl.toString())
        imageuri.downloadUrl.addOnSuccessListener { uri->
            Glide.with(holder.itemView.context).load(uri).into(holder.product_image)
        }.addOnFailureListener { exeption->
            Toast.makeText(holder.itemView.context,"Lỗi không thể tải được ảnh",Toast.LENGTH_SHORT).show()
        }
    }
    interface MyClickListener{
        fun onClick(position: Int)
    }
}