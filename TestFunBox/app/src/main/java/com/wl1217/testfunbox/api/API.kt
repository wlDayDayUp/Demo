package com.wl1217.testfunbox.api

class API {
    companion object {
        private const val baseUrl = "http://192.168.0.166:3000"

        const val getCs = "$baseUrl/users/login"

        fun getCsParm() = hashMapOf<String,String>(
            "username" to "wwwwwww",
            "age" to "123"
        )
    }
}