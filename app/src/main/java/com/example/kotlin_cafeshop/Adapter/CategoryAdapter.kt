package com.example.kotlin_cafeshop.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlin_cafeshop.Model.Category
import com.example.kotlin_cafeshop.R
import com.google.firebase.storage.FirebaseStorage

class CategoryAdapter(val arrayCategory: ArrayList<Category>,val listener:MyClickListener):RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    inner class ViewHolder (itemView: View):RecyclerView.ViewHolder(itemView) {
        var name_category=itemView.findViewById<TextView>(R.id.name_category)
        var imageUrl_category=itemView.findViewById<ImageView>(R.id.imageUrl_category)
        init{
            itemView.setOnClickListener {
                listener.onClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_item,parent,false))
    }

    override fun getItemCount(): Int = arrayCategory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentItem=arrayCategory[position]
        holder.name_category.text=currentItem.name_category

        val storageRef=FirebaseStorage.getInstance().getReference()

        val imageurl=storageRef.child(currentItem.imageURL_category.toString())

        imageurl.downloadUrl.addOnSuccessListener { uri->
            Glide.with(holder.itemView.context)
                .load(uri)
                .into(holder.imageUrl_category)
        }.addOnFailureListener {
                exception ->
            Toast.makeText(holder.itemView.context, "Lỗi không tải được ảnh: ${exception.message}",
                Toast.LENGTH_SHORT).show()
        }
    }
    interface MyClickListener{
        fun onClick(position: Int)
    }
}