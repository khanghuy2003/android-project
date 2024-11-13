package com.example.kotlin_cafeshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_cafeshop.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
lateinit var binding:ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBatdau.setOnClickListener { startActivity(Intent(this@WelcomeActivity, MainActivity::class.java)) }

        binding.btnRegisterWelcome.setOnClickListener { startActivity(Intent(this@WelcomeActivity,RegisterActivity::class.java)) }

        binding.btnLoginWelcome.setOnClickListener { startActivity(Intent(this@WelcomeActivity,LoginActivity::class.java)) }
    }
}