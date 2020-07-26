package com.example.household

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.create_new_household.*

class CreateNewHousehold: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_new_household)
        setupListeners()
    }

    fun setupListeners(){
        submitCreateNewHousehold.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }
    }
}