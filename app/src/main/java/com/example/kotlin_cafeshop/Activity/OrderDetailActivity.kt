package com.example.kotlin_cafeshop.Activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_cafeshop.Adapter.ListOrderItemAdapter
import com.example.kotlin_cafeshop.Model.CartItem
import com.example.kotlin_cafeshop.Model.Order
import com.example.kotlin_cafeshop.R
import com.example.kotlin_cafeshop.databinding.ActivityOrderBinding
import com.example.kotlin_cafeshop.databinding.ActivityOrderDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class OrderDetailActivity : AppCompatActivity() {

    lateinit var binding:ActivityOrderDetailBinding
    lateinit var dbRef:DatabaseReference
    lateinit var adapter:ListOrderItemAdapter
    lateinit var arrayList: ArrayList<CartItem>
    lateinit var orderId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        orderId = intent.getStringExtra("orderId")!!

        // Khởi tạo danh sách và adapter
        arrayList = ArrayList()
        adapter = ListOrderItemAdapter(arrayList)

        // Thiết lập RecyclerView với LayoutManager và adapter
        binding.recyclerOrderItemsPlacedOrder.layoutManager = LinearLayoutManager(this)
        binding.recyclerOrderItemsPlacedOrder.adapter = adapter

        setUp()

        binding.btnPlacedOrderEdit.setOnClickListener { chinhSuaDonHang() }

        binding.btnPlacedOrderDelete.setOnClickListener { huyDonHang() }

        binding.btnPlacedOrderBack.setOnClickListener { finish() }
    }

    private fun chinhSuaDonHang() {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (user == null) {
            Toast.makeText(this@OrderDetailActivity, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        val buider = AlertDialog.Builder(this)
        buider.setTitle("Chỉnh sửa thông tin đơn hàng!")
        val dialogView = layoutInflater.inflate(R.layout.edit_order_information, null)
        buider.setView(dialogView)

        val edit_name_order=dialogView.findViewById<EditText>(R.id.edit_name_order)
        val edit_phone_order=dialogView.findViewById<EditText>(R.id.edit_phone_order)
        val edit_address_order=dialogView.findViewById<EditText>(R.id.edit_address_order)

        dbRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("orders")
        var query = dbRef.orderByChild("orderId").equalTo(orderId)
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    var data = i.getValue(Order::class.java)
                    edit_name_order.setText(data!!.name)
                    edit_phone_order.setText(data!!.phone)
                    edit_address_order.setText(data!!.address)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        buider.setPositiveButton("Sửa"){ dialog,which->

            query.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children){
                        var data = i.getValue(Order::class.java)
                        if (data!=null){
                            data.name=edit_name_order.text.toString()
                            data.phone=edit_phone_order.text.toString()
                            data.address=edit_address_order.text.toString()
                        }
                        i.ref.setValue(data)
                            .addOnSuccessListener { Toast.makeText(this@OrderDetailActivity,"Sửa thành công!!",Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { Toast.makeText(this@OrderDetailActivity,"Sửa không thành công!!",Toast.LENGTH_SHORT).show() }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        }

        buider.setNegativeButton("Hủy"){ dialog,which->
            dialog.dismiss()
        }

        var alert=buider.create()
        alert.show()
    }

    private fun huyDonHang() {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (user == null) {
            Toast.makeText(this@OrderDetailActivity, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        val buider = AlertDialog.Builder(this)
        buider.setTitle("Thông báo!")
        buider.setMessage("Bạn có chắc chắn muốn hủy đơn hàng này không?")

        buider.setPositiveButton("Có"){ dialog,which->

            dbRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("orders")

            var query = dbRef.orderByChild("orderId").equalTo(orderId)
            query.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children){
                        val data=i.getValue(Order::class.java)
                        data!!.status = "Đã Hủy"
                        i.ref.setValue(data)
                            .addOnSuccessListener {
                                Toast.makeText(this@OrderDetailActivity,"Hủy đơn hàng thành công!!",Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { Toast.makeText(this@OrderDetailActivity,"Hủy đơn hàng không thành công!!",Toast.LENGTH_SHORT).show() }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        buider.setNegativeButton("Không"){ dialog,which->
            dialog.dismiss()
        }

        var alert=buider.create()
        alert.show()
    }

    private fun setUp() {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (user == null) {
            Toast.makeText(this@OrderDetailActivity, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        dbRef=FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("orders")
        var query = dbRef.orderByChild("orderId").equalTo(orderId)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList.clear()
                for (i in snapshot.children){
                    var data = i.getValue(Order::class.java)
                    binding.detailPlacedOrderId.text = "ID đơn hàng: "+ data!!.orderId
                    binding.detailPlacedOrderName.text = "Tên người nhận: "+ data!!.name
                    binding.detailPlacedOrderTime.text = "Thời gian đặt hàng: "+ data!!.orderDateTime
                    binding.detailPlacedOrderPhone.text = "Số điện thoại người nhận: "+ data!!.phone
                    binding.detailPlacedOrderStatus.text = "Trạng thái đơn hàng: "+ data!!.status
                    binding.detailPlacedOrderAddress.text = "Địa chỉ người nhận: "+ data!!.address
                    binding.btnPlacedOrderTotalPayment.text = "Tổng tiền: "+ data!!.totalPaymentOrder
                    //add danh sách sản phẩm
                    arrayList.addAll(data.orderItems)

                    if (data.status == "Đã Hủy"){
                        binding.btnPlacedOrderEdit.visibility = View.GONE
                        binding.btnPlacedOrderDelete.visibility = View.GONE
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}