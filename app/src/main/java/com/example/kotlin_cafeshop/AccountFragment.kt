package com.example.kotlin_cafeshop

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.IntervalTree
import com.example.kotlin_cafeshop.Activity.LoginActivity
import com.example.kotlin_cafeshop.Activity.MainActivity
import com.example.kotlin_cafeshop.Activity.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AccountFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var btnDangNhap:Button
    lateinit var btnDangKy:Button
    lateinit var btnDangXuat:Button
    lateinit var txtUserEmail:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnDangNhap=view.findViewById(R.id.loginButton)
        btnDangKy=view.findViewById(R.id.registerButton)
        btnDangXuat=view.findViewById(R.id.signOut)
        txtUserEmail=view.findViewById(R.id.usernameTextView)


        loadData()

        btnDangXuat.setOnClickListener { SignOut() }

        btnDangKy.setOnClickListener { startActivity(Intent(requireContext(),RegisterActivity::class.java))}

        btnDangNhap.setOnClickListener { startActivity(Intent(requireContext(),LoginActivity::class.java))}

    }

    private fun SignOut() {
        val auth = FirebaseAuth.getInstance()

        auth.signOut()
        Toast.makeText(requireContext(),"Đăng xuất thành công!!",Toast.LENGTH_SHORT).show()

        // Chuyển hướng về màn hình đăng nhập
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun loadData() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        // Kiểm tra nếu người dùng chưa đăng nhập
        if (user == null) {
            txtUserEmail.text="Vui lòng đăng nhập để sử dụng các chức năng!"
            btnDangXuat.visibility=View.GONE
            btnDangNhap.visibility=View.VISIBLE
            btnDangKy.visibility=View.VISIBLE
        }else{
            txtUserEmail.text = user.email.toString()
            btnDangXuat.visibility=View.VISIBLE
            btnDangNhap.visibility=View.GONE
            btnDangKy.visibility=View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}