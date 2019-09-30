package com.wl1217.testfunbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wl1217.funlib.utils.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        "Welcome on Android".toast(this)
    }
}
