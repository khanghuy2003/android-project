package com.example.kotlin_cafeshop.Adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_cafeshop.Model.CartItem
import com.example.kotlin_cafeshop.R
import java.text.NumberFormat
import java.util.Locale

class ListOrderItemAdapter(val arr:ArrayList<CartItem>):RecyclerView.Adapter<ListOrderItemAdapter.ViewHolder>() {
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val txt_stt_order_item=itemView.findViewById<TextView>(R.id.txt_stt_order_item)
        val txt_name_order_item=itemView.findViewById<TextView>(R.id.txt_name_order_item)
        val txt_size_order_item=itemView.findViewById<TextView>(R.id.txt_size_order_item)
        val txt_quantity_order_item=itemView.findViewById<TextView>(R.id.txt_quantity_order_item)
        val txt_price_order_item=itemView.findViewById<TextView>(R.id.txt_price_order_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_item,parent,false))
    }

    override fun getItemCount(): Int = arr.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem=arr[position]
        holder.txt_stt_order_item.text=position.toString()
        holder.txt_name_order_item.text=currentItem.cartItemName
        holder.txt_size_order_item.text=currentItem.cartItemSize
        holder.txt_quantity_order_item.text = currentItem.cartItemQuantity.toString()
        holder.txt_price_order_item.text = formatCurrency(currentItem.cartItemTotalPrice!!)
    }

    fun formatCurrency(amount: Int): String {
        val formattedAmount = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(amount)
        return "$formattedAmount đ"
    }
}