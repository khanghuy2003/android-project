package com.example.kotlin_cafeshop.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlin_cafeshop.CartFragment
import com.example.kotlin_cafeshop.Model.CartItem
import com.example.kotlin_cafeshop.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat
import java.util.Locale

class CartItemAdapter(val arrCartItem:ArrayList<CartItem>):RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {
    lateinit var cartFragment: CartFragment
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val cart_product_image=itemView.findViewById<ImageView>(R.id.cart_product_image)
        val cart_product_name=itemView.findViewById<TextView>(R.id.cart_product_name)
        val cart_product_size=itemView.findViewById<TextView>(R.id.cart_product_size)
        val cart_product_quantity=itemView.findViewById<TextView>(R.id.cart_product_quantity)
        val cart_product_total_amount=itemView.findViewById<TextView>(R.id.cart_product_total_amount)
        val delete_cart_item=itemView.findViewById<Button>(R.id.delete_cart_item)
        init {
            delete_cart_item.setOnClickListener {
                //Tạo AlertDialog
                val buider=AlertDialog.Builder(itemView.context)
                buider.setTitle("Thông báo")
                buider.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                buider.setPositiveButton("Xóa"){ dialog,which->
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {

                        val auth = FirebaseAuth.getInstance()
                        val user = auth.currentUser

                        val currentItem = arrCartItem[position]
                        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid).child("cart")
                        dbRef.child(currentItem.id!!).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(itemView.context, "Xóa thành công!!!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(itemView.context, "Xóa thất bại!!!", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                buider.setNegativeButton("Hủy"){ dialog,which->
                    dialog.dismiss()
                }
                val alertDialog=buider.create()
                alertDialog.show()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cart,parent,false))
    }

    override fun getItemCount(): Int = arrCartItem.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = arrCartItem[position]

        holder.cart_product_name.text = currentItem.cartItemName.toString()
        holder.cart_product_size.text = "Size: " + currentItem.cartItemSize.toString()
        holder.cart_product_quantity.text = "Số lượng: " + currentItem.cartItemQuantity.toString()

        val formatCartItemPrice= NumberFormat.getNumberInstance(Locale("vi", "VN")).format(currentItem.cartItemTotalPrice)
        holder.cart_product_total_amount.text="Giá tiền: $formatCartItemPrice"+"đ"

        val storageRef = FirebaseStorage.getInstance().getReference()
        val imageuri = storageRef.child(currentItem.cartItemImageUrl.toString())
        imageuri.downloadUrl.addOnSuccessListener { uri->
            Glide.with(holder.itemView.context).load(uri).into(holder.cart_product_image)
        }.addOnFailureListener { exeption->
            Toast.makeText(holder.itemView.context,"Lỗi không thể tải được ảnh", Toast.LENGTH_SHORT).show()
        }

    }
}