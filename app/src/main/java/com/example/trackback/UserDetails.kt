package com.example.trackback

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.trackback.databinding.ActivityUserDetailsBinding

class UserDetails : AppCompatActivity() {
    lateinit var binding:ActivityUserDetailsBinding
    lateinit var userDetails: SharedPreferences
    var isFirstTime:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TrackBack)
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setData()
    }

    private fun setData() {
        userDetails = this.getSharedPreferences("UserDetails", AppCompatActivity.MODE_PRIVATE)
        isFirstTime = userDetails.getBoolean("isFirstTime",true)
        if (!isFirstTime){
            goToNextScreen()
        }else {
            binding.next.setOnClickListener {
                saveUserData()
            }
        }
    }

    private fun goToNextScreen() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveUserData() {
        val name = binding.editName.text.toString()
        val monthly_budget = binding.editMoney.text.toString()
        if(name.equals("") || monthly_budget.equals("")) {
            Toast.makeText(this, "Enter all details to continue...", Toast.LENGTH_SHORT).show()
        }else{
            val editor: SharedPreferences.Editor = userDetails.edit()
            editor.putBoolean("isFirstTime", false)
            editor.putString("Name", name)
            editor.putString("MonthlyBudget", monthly_budget)
            editor.apply()
            goToNextScreen()
        }
    }
}