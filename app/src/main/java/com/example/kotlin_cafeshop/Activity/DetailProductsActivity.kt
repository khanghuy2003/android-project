package com.example.kotlin_cafeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.kotlin_cafeshop.HomeFragment
import com.example.kotlin_cafeshop.Model.CartItem
import com.example.kotlin_cafeshop.Model.Products
import com.example.kotlin_cafeshop.R
import com.example.kotlin_cafeshop.databinding.ActivityDetailProductsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.collection.LLRBNode.Color
import com.google.firebase.storage.FirebaseStorage
import java.io.Serializable
import java.text.NumberFormat
import java.util.Locale
import kotlin.time.times

class DetailProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailProductsBinding
    var products:Products?=null
    var dbRef:DatabaseReference?=null
    var sizeSelected:String?=null
    var quantity:Int?=1
    var totalAmount:Int?=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        products = intent.getSerializableExtra("SelectedProducts") as Products?
        if (products?.product_categoryid == 4){
            binding.textView9.visibility = View.GONE
            binding.linearLayout7.visibility = View.GONE
        }
        binding.btnDetailBackToHome.setOnClickListener { startActivity(Intent(this,MainActivity::class.java)) }

        setupView()

    }

    private fun setupView() {
        val formatDetailProductPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(products!!.product_price)
        //load tên và giá
        binding.txtDetailNameProduct.text = products!!.product_name
        binding.txtDetailPriceProduct.text = "$formatDetailProductPrice đ"
        //load ảnh
        val storageRef=FirebaseStorage.getInstance().getReference()
        val imageuri=storageRef.child(products!!.product_imageurl.toString())
        imageuri.downloadUrl.addOnSuccessListener { uri->
            Glide.with(this).load(uri).into(binding.imgDetailImageProduct)
        }.addOnFailureListener { exeption->
            Toast.makeText(this,"Lỗi không tải được ảnh!",Toast.LENGTH_SHORT).show()
        }

        //Chọn size đồ uống
        selectSize()
        //Chọn số lượng
        selectQuantity()
        //Tổng số tiền cần trả
        updateTotalAmount()

        binding.btnAddToCart.setOnClickListener { AddToCart(products!!,sizeSelected,quantity) }

    }

    private fun AddToCart(products: Products, sizeSelected: String?, quantity: Int?) {
        var cartItemExists = false
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser // Lấy người dùng hiện tại

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (user == null) {
            Toast.makeText(this@DetailProductsActivity, "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show()
            return
        }

        dbRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
        var query= dbRef!!.child("cart").orderByChild("cartItemId").equalTo(products.product_id)

        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    //Lấy data CartItem ra
                    var data=i.getValue(CartItem::class.java)
                    //Kiểm tra xem size của sản phẩm đã tồn tại trong cart chưa?
                    if(data!=null && data.cartItemSize == sizeSelected){
                        //Nếu có rồi cập nhật lại số lượng và giá tiền
                        var newQuantity = quantity!! + data.cartItemQuantity!!
                        var newTotalAmount = (products.product_price!! + getSizePrice(data.cartItemSize.toString())) * newQuantity
                        //Cập nhật trong Firebase
                        i.ref.child("cartItemQuantity").setValue(newQuantity)
                        i.ref.child("cartItemTotalPrice").setValue(newTotalAmount)

                        cartItemExists = true
                        Toast.makeText(this@DetailProductsActivity,
                            "Cap nhat san pham thanh cong!!",
                            Toast.LENGTH_SHORT).show()
                        break
                    }
                }
                //Nếu sản phẩm chưa có trong giỏ
                if(cartItemExists == false){
                    var id = dbRef!!.child("cart").push().key!!
                    var newCartItem = CartItem(
                        id,
                        products.product_id.toString(),
                        products.product_name.toString(),
                        quantity,
                        sizeSelected.toString(),
                        totalAmount,
                        products.product_imageurl.toString()
                        )

                    dbRef!!.child("cart").child(id).setValue(newCartItem).addOnSuccessListener {
                        Toast.makeText(this@DetailProductsActivity,
                            "Thêm sản phẩm thành công!!",
                            Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this@DetailProductsActivity,
                            "Thêm sản phẩm thất bại!!",
                            Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateTotalAmount() {
        var upsizePrice=0
        if(sizeSelected=="M") upsizePrice=7000
        if(sizeSelected=="L") upsizePrice=14000
        totalAmount = (products!!.product_price!! + upsizePrice) * quantity!!

        val formattedAmount = NumberFormat.getNumberInstance(
            java.util.Locale(
                "vi",
                "VN"
            )
        ).format(totalAmount)
        binding.txtTotalAmount.text="$formattedAmount"+"đ"
    }

    private fun selectQuantity() {

        // Lắng nghe thay đổi trong EditText để giới hạn giá trị từ 1 đến 50
        binding.edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Tạm thời loại bỏ TextWatcher để tránh vòng lặp
                binding.edtQuantity.removeTextChangedListener(this)

                val input = s.toString().toIntOrNull()

                if (input == null || input == 0) { // Không cho phép nhập giá trị 0 hoặc trống
                    binding.edtQuantity.setText("1")
                    quantity = 1
                } else if (input > 50) { // Giới hạn tối đa là 50
                    binding.edtQuantity.setText("50")
                    quantity = 50
                    Toast.makeText(
                        this@DetailProductsActivity,
                        "Số lượng sản phẩm không được nhiều hơn 50!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    quantity = input
                }

                binding.edtQuantity.setSelection(binding.edtQuantity.text.length) // Đưa con trỏ về cuối
                updateTotalAmount()

                // Thêm lại TextWatcher sau khi xử lý xong
                binding.edtQuantity.addTextChangedListener(this)
            }
        })

        binding.btnTangSl.setOnClickListener {
            quantity=binding.edtQuantity.text.toString().toInt()
            quantity = quantity!! + 1
            binding.edtQuantity.setText(quantity.toString())
            updateTotalAmount()
        }
        binding.btnGiamSl.setOnClickListener {
            quantity=binding.edtQuantity.text.toString().toInt()
            if(quantity!!<=1){
                Toast.makeText(this,"Số lượng sản phẩm tối thiểu là 1",Toast.LENGTH_SHORT).show()
            }else{
                quantity = quantity!! - 1
                binding.edtQuantity.setText(quantity.toString())
            }
            updateTotalAmount()
        }
        updateTotalAmount()
        quantity=binding.edtQuantity.text.toString().toInt()
    }

    private fun selectSize() {
        //Mặc định size S
        sizeSelected="S"
        binding.btnSizeS.backgroundTintList = getColorStateList(R.color.background_selected_color)
        binding.btnSizeS.setTextColor(getColorStateList(R.color.text_selected_color))

        var buttonSize= listOf(binding.btnSizeS, binding.btnSizeM, binding.btnSizeL)

        buttonSize.forEach { button ->
            button.setOnClickListener {
                // Đặt lại màu nền và màu chữ cho tất cả các nút
                buttonSize.forEach {
                    it.backgroundTintList = getColorStateList(R.color.background_unselected_color)
                    it.setTextColor(getColor(R.color.text_unselected_color))
                }

                // Đặt màu nền và màu chữ cho nút được chọn
                button.backgroundTintList = getColorStateList(R.color.background_selected_color)
                button.setTextColor(getColor(R.color.text_selected_color))

                // Cập nhật sizeSelected dựa trên nút được chọn
                sizeSelected = when (button) {
                    binding.btnSizeS -> "S"
                    binding.btnSizeM -> "M"
                    binding.btnSizeL -> "L"
                    else -> "S" // Giá trị mặc định
                }
                updateTotalAmount()
            }

        }

    }

    private fun getSizePrice(size:String):Int{
        return when(size){
            "M"->7000
            "L"->14000
            else->0
        }
    }
}