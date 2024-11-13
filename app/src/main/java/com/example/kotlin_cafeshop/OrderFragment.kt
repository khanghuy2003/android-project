package com.example.kotlin_cafeshop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_cafeshop.Adapter.OrderAdapter
import com.example.kotlin_cafeshop.Model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OrderFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var array_placed_order:ArrayList<Order>
    lateinit var adapter:OrderAdapter
    lateinit var recycler_placed_order:RecyclerView
    lateinit var dbRef:DatabaseReference
    lateinit var thongBaoDanhSachDonHang: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_placed_order = view.findViewById(R.id.recycler_placed_order)
        thongBaoDanhSachDonHang = view.findViewById(R.id.textView18)

        array_placed_order = ArrayList()
        adapter= OrderAdapter(array_placed_order)

        loadPlacedOrder()

        recycler_placed_order.layoutManager=LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL, false)


    }

    private fun loadPlacedOrder() {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (user == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        dbRef=FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("orders")
        val listener=object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                array_placed_order.clear()
                for (i in snapshot.children){
                    var data=i.getValue(Order::class.java)
                    array_placed_order.add(data!!)
                }

                if (array_placed_order.isEmpty()){
                    thongBaoDanhSachDonHang.visibility = View.VISIBLE
                }else{
                    thongBaoDanhSachDonHang.visibility = View.GONE
                }

                recycler_placed_order.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        dbRef.addValueEventListener(listener)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}