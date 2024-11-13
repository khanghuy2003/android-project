package com.example.kotlin_cafeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlin_cafeshop.Model.User
import com.example.kotlin_cafeshop.R
import com.example.kotlin_cafeshop.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    lateinit var binding:ActivityRegisterBinding
    lateinit var auth:FirebaseAuth
    lateinit var dbRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { RegisterUser() }

        binding.btnRegisterToLogin.setOnClickListener { startActivity(Intent(this,LoginActivity::class.java)) }

        binding.backToHomeRegister.setOnClickListener { startActivity(Intent(this,MainActivity::class.java)) }

    }

    private fun RegisterUser() {

        auth=FirebaseAuth.getInstance()

        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val confirmPassword = binding.edtConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(this@RegisterActivity,"Vui lòng nhập đầy đủ các trường thông tin!!",Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword){
            Toast.makeText(this@RegisterActivity,"Nhập lại mật khẩu không chính xác!!",Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                val user=auth.currentUser
                saveUserData(user?.uid, email)
                Toast.makeText(this@RegisterActivity,"Đăng ký thành công!!",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun saveUserData(uid: String?, email: String) {
        dbRef=FirebaseDatabase.getInstance().getReference("users")
        val user = User(uid,email, mutableListOf(),mutableListOf())
        dbRef.child(uid!!).setValue(user)
    }
}