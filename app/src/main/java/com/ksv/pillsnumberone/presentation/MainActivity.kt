package com.ksv.pillsnumberone.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ksv.pillsnumberone.MyApp
import com.ksv.pillsnumberone.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MyApp.init(applicationContext)
    }
}