package com.example.kotlin_cafeshop

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_cafeshop.Adapter.AllProductsAdapter
import com.example.kotlin_cafeshop.Adapter.AllProductsAdapter.MyClickListener
import com.example.kotlin_cafeshop.Adapter.CategoryAdapter
import com.example.kotlin_cafeshop.Model.Category
import com.example.kotlin_cafeshop.Model.Products
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var categoryAdapter: CategoryAdapter
    lateinit var allProductsAdapter: AllProductsAdapter
    lateinit var popularProductsAdapter: AllProductsAdapter

    lateinit var arrayCategory: ArrayList<Category>
    lateinit var arrayAllProducts:ArrayList<Products>
    lateinit var arrayPopularProducts:ArrayList<Products>

    lateinit var recycler_category:RecyclerView
    lateinit var recycler_allproduct:RecyclerView
    lateinit var recycler_popular:RecyclerView

    lateinit var helloUser:TextView

    lateinit var dbRef:DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //anh xa
        recycler_category=view.findViewById(R.id.recycler_category)
        recycler_allproduct=view.findViewById(R.id.recycler_allproduct)
        recycler_popular=view.findViewById(R.id.recycler_popular)
        helloUser=view.findViewById(R.id.hellouser)

        //khoi tao array
        arrayCategory=ArrayList()
        arrayAllProducts=ArrayList()
        arrayPopularProducts= ArrayList()

        //load data ban đầu
        loadDataAllProducts()
        loadDataCategory()
        loadPopularProducts()

        recycler_category.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        recycler_allproduct.layoutManager=GridLayoutManager(requireContext(),3)
        recycler_popular.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        //khoi tao Category Adapter
        categoryAdapter= CategoryAdapter(arrayCategory,object :CategoryAdapter.MyClickListener{
            override fun onClick(position: Int) {
                loadSanPhamTheoCategory(arrayCategory[position].id_category!!.toString())
            }
        })

        //khoi tao All Product Adapter
        allProductsAdapter=AllProductsAdapter(arrayAllProducts)

        //khoi tao popular product Adapter
        popularProductsAdapter= AllProductsAdapter(arrayPopularProducts)

        //set Adapter
        recycler_allproduct.adapter=allProductsAdapter
        recycler_category.adapter=categoryAdapter
        recycler_popular.adapter=popularProductsAdapter
    }

    //Load sản phẩm có lượt bán cao nhất
    private fun loadPopularProducts() {
        //add du lieu tu firebase
        dbRef=FirebaseDatabase.getInstance().getReference("products")
        val query=dbRef.orderByChild("product_salescount").limitToLast(5)
        val listener=object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayPopularProducts.clear()
                for (i in snapshot.children){
                    val products=i.getValue(Products::class.java)
                    arrayPopularProducts.add(products!!)
                }
                arrayPopularProducts.reverse()
                popularProductsAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        query.addListenerForSingleValueEvent(listener)
    }

    //Load sản phẩm theo category
    private fun loadSanPhamTheoCategory(id_category:String) {
        //load tat ca neu nguoi dung nhan vao Tat ca
        if(id_category=="0"){
            loadDataAllProducts()
            return
        }
        //add du lieu tu firebase
        dbRef=FirebaseDatabase.getInstance().getReference("products")
        val query=dbRef.orderByChild("product_categoryid").equalTo(id_category.toDouble())
        val listener=object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayAllProducts.clear()
                for (i in snapshot.children){
                    val products=i.getValue(Products::class.java)
                    arrayAllProducts.add(products!!)
                }
                allProductsAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        query.addListenerForSingleValueEvent(listener)
    }

    private fun loadDataAllProducts() {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user==null){
            helloUser.text="Xin chào!! Vui lòng đăng nhập để sử dụng các chức năng"
        }else{
            helloUser.text="Xin chào!! ${user!!.email}"
        }

        //add du lieu tu firebase
        dbRef=FirebaseDatabase.getInstance().getReference("products")
        val listener=object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayAllProducts.clear()
                for (i in snapshot.children){
                    val products=i.getValue(Products::class.java)
                    arrayAllProducts.add(products!!)
                }
                allProductsAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        dbRef.addListenerForSingleValueEvent(listener)
    }

    fun loadDataCategory() {
        //add du lieu tu Firebase
        dbRef=FirebaseDatabase.getInstance().getReference("category")
        val listener=object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayCategory.clear()
                for(i in snapshot.children){
                    val category=i.getValue(Category::class.java)
                    arrayCategory.add(category!!)
                }
                categoryAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        dbRef.addListenerForSingleValueEvent(listener)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}