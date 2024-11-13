package com.example.kotlin_cafeshop.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.kotlin_cafeshop.AccountFragment
import com.example.kotlin_cafeshop.CartFragment
import com.example.kotlin_cafeshop.HomeFragment
import com.example.kotlin_cafeshop.OrderFragment
import com.example.kotlin_cafeshop.R
import com.example.kotlin_cafeshop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var fragment_container:FragmentContainerView
    lateinit var binding:ActivityMainBinding

    private var homeFragment: HomeFragment?=null
    private var cartFragment: CartFragment?=null
    private var orderFragment: OrderFragment?=null
    private var accountFragment: AccountFragment?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fragment_container=findViewById(R.id.fragment_container)

        loadHomePage()

        binding.homeButton.setOnClickListener { loadHomePage() }
        binding.cartButton.setOnClickListener { loadCartPage() }
        binding.orderButton.setOnClickListener { loadOrderPage() }
        binding.accButton.setOnClickListener { loadAccountPage() }

    }

    private fun loadHomePage() {
        if (homeFragment==null){
            homeFragment= HomeFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container,homeFragment!!).commit()
        }
        showFragment(homeFragment!!)
    }
    private fun loadCartPage() {
        if (cartFragment==null){
            cartFragment= CartFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container,cartFragment!!).commit()
        }
        showFragment(cartFragment!!)
    }
    private fun loadOrderPage() {
        if (orderFragment==null){
            orderFragment= OrderFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container,orderFragment!!).commit()
        }
        showFragment(orderFragment!!)
    }
    private fun loadAccountPage() {
        if (accountFragment==null){
            accountFragment= AccountFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container,accountFragment!!).commit()
        }
        showFragment(accountFragment!!)
    }

    //ham kiem tra co phai la fragment hien tai hay khong
    private fun isCurrentFragment(fragment:Fragment):Boolean{
        val currentFragment=supportFragmentManager.findFragmentById(R.id.fragment_container)
        return currentFragment==fragment
    }

    //ham show fragment
    private fun showFragment(fragmentWantToShow: Fragment) {
        supportFragmentManager.fragments.forEach { fragment->
            if(fragment == fragmentWantToShow){
                supportFragmentManager.beginTransaction().show(fragment).commit()
            }else{
                supportFragmentManager.beginTransaction().hide(fragment).commit()
            }
        }
    }


}