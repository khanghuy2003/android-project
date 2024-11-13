package com.example.kotlin_cafeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_cafeshop.Adapter.ListOrderItemAdapter
import com.example.kotlin_cafeshop.CartFragment
import com.example.kotlin_cafeshop.Model.CartItem
import com.example.kotlin_cafeshop.Model.Order
import com.example.kotlin_cafeshop.R
import com.example.kotlin_cafeshop.databinding.ActivityOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    lateinit var orderItemList: ArrayList<CartItem>
    lateinit var adapter:ListOrderItemAdapter
    lateinit var dbRef:DatabaseReference
    var totalPayment:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderItemList = ArrayList()
        adapter = ListOrderItemAdapter(orderItemList)
        binding.recyclerOrderItems.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        loadOrderItems()

        binding.radioCashOnDelivery.isChecked = true

        totalPayment = intent.getIntExtra("totalPayment",123)

        binding.totalPayment.text= "Tổng tiền: " + "${formatCurrency(totalPayment)}"

        binding.btnCancel.setOnClickListener { finish() }

        binding.btnPlaceOrder.setOnClickListener { placeOrder() }

    }

    private fun placeOrder() {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (user == null) {
            Toast.makeText(this@OrderActivity, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

//        dbRef=FirebaseDatabase.getInstance().getReference("orders")

        dbRef=FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("orders")

        var orderId = dbRef.push().key.toString()
        var name = binding.edtName.text.toString()
        var address = binding.edtAddress.text.toString()
        var phone = binding.edtPhone.text.toString()

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()){
            Toast.makeText(this@OrderActivity,"Vui lòng nhập đầy đủ thông tin",Toast.LENGTH_SHORT).show()
            return
        }

        var paymentMethod = checkPaymentMethod()
        var currentDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        var status = "Đang xử lý"
        var totalPaymentOrder = formatCurrency(totalPayment)

        val order=(Order(orderId,name,address,phone,paymentMethod,orderItemList,currentDateTime,status,totalPaymentOrder))

        dbRef.child(orderId).setValue(order).addOnSuccessListener {
            Toast.makeText(this,"Đặt hàng thành công!!",Toast.LENGTH_SHORT).show()
            clearCart()
            finish()
        }.addOnFailureListener { exeption->
            Toast.makeText(this,"Đặt hàng thất bại!! $exeption",Toast.LENGTH_SHORT).show()
        }

    }

    private fun clearCart() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        dbRef=FirebaseDatabase.getInstance().getReference("users").child(user!!.uid).child("cart")
        dbRef.removeValue()
    }

    private fun checkPaymentMethod(): String {
        if (binding.radioCashOnDelivery.isChecked){ return "Thanh toán khi nhận hàng" }
        else { return "Thanh toán qua ví điện tử" }
    }

    private fun loadOrderItems() {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (user == null) {
            Toast.makeText(this@OrderActivity, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        dbRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("cart")

        val listener=object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                orderItemList.clear()

                for (i in snapshot.children){
                    var data = i.getValue(CartItem::class.java)
                    orderItemList.add(data!!)
                }

                binding.recyclerOrderItems.adapter=adapter

            }

            override fun onCancelled(error: DatabaseError) {}

        }

        dbRef.addValueEventListener(listener)

    }

    fun formatCurrency(amount: Int): String {
        val formattedAmount = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(amount)
        return "$formattedAmount đ"
    }
}