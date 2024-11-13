package com.example.kotlin_cafeshop

import android.content.Intent
import android.os.Bundle
import android.provider.Contacts.Intents
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_cafeshop.Activity.OrderActivity
import com.example.kotlin_cafeshop.Adapter.CartItemAdapter
import com.example.kotlin_cafeshop.Model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CartFragment : Fragment() {

    lateinit var recycler_cart_item:RecyclerView
    lateinit var total_payment_cartitem:TextView
    lateinit var txt_giohangtrong:TextView
    lateinit var btn_dat_hang:Button
    lateinit var linearLayout2:LinearLayout
    var totalPayment: Int?=0
    var arrayList_cart:ArrayList<CartItem>?=null
    lateinit var adapter:CartItemAdapter
    lateinit var dbRef:DatabaseReference
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_cart_item=view.findViewById(R.id.recycler_cart_item)
        total_payment_cartitem=view.findViewById(R.id.total_payment_cartitem)
        txt_giohangtrong=view.findViewById(R.id.textView3)
        btn_dat_hang=view.findViewById(R.id.btn_dat_hang)
        linearLayout2=view.findViewById(R.id.linearLayout2)


        btn_dat_hang.setOnClickListener {
            var intent=Intent(requireContext(),OrderActivity::class.java)
            intent.putExtra("totalPayment",totalPayment)
            startActivity(intent)
        }

        arrayList_cart = ArrayList()
        adapter = CartItemAdapter(arrayList_cart!!)
        recycler_cart_item.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        loadCartItem()

        recycler_cart_item.adapter = adapter
    }

    private fun loadCartItem() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (user == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            txt_giohangtrong.visibility=View.VISIBLE
            linearLayout2.visibility=View.GONE
            return
        }

        dbRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid).child("cart")
        var listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                totalPayment=0
                arrayList_cart!!.clear()

                for (i in snapshot.children){
                    var data = i.getValue(CartItem::class.java)
                    arrayList_cart!!.add(data!!)

                    //Tổng tiền tất cả sp trong giỏ hàng
                    totalPayment = totalPayment!! + data.cartItemTotalPrice!!
                }

                val formatTotalPayment = NumberFormat.getNumberInstance(java.util.Locale("vi", "VN")).format(totalPayment)
                total_payment_cartitem.text = "Tổng tiền: $formatTotalPayment"+"đ"
                adapter.notifyDataSetChanged()

                //Khi giỏ hàng trống
                if (arrayList_cart!!.isEmpty()) {
                    txt_giohangtrong.visibility=View.VISIBLE
                    linearLayout2.visibility=View.GONE
                }
                else {
                    txt_giohangtrong.visibility=View.GONE
                    linearLayout2.visibility=View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        dbRef.addValueEventListener(listener)
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}