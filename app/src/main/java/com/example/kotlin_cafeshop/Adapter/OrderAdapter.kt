package com.example.kotlin_cafeshop.Adapter

import android.content.Intent
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_cafeshop.Activity.OrderDetailActivity
import com.example.kotlin_cafeshop.Model.Order
import com.example.kotlin_cafeshop.R

class OrderAdapter(val arr: ArrayList<Order>):RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var name=itemView.findViewById<TextView>(R.id.placed_order_name)
        var time=itemView.findViewById<TextView>(R.id.placed_order_time)
        var status=itemView.findViewById<TextView>(R.id.placed_order_status)
        var placed_order_detail=itemView.findViewById<Button>(R.id.placed_order_detail)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_placed,parent,false))
    }

    override fun getItemCount(): Int = arr.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentItem = arr[position]
        holder.name.text = "Tên người nhận: "+currentItem.name.toString()
        holder.time.text = "Thời gian đặt hàng: "+currentItem.orderDateTime.toString()
        holder.status.text = "Trạng thái đơn hàng: "+currentItem.status.toString()

        holder.placed_order_detail.setOnClickListener { view->
            var intent=Intent( view.context, OrderDetailActivity::class.java )
            intent.putExtra("orderId" , currentItem.orderId.toString())
            view.context.startActivity(intent)
        }

    }
}