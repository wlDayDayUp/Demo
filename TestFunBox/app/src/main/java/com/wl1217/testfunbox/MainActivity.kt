package com.wl1217.testfunbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wl1217.testfunbox.api.API
import rxhttp.wrapper.param.Param
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.`RxHttp$NoBodyParam`


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
