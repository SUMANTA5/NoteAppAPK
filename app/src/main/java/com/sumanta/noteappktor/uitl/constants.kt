package com.sumanta.noteappktor.uitl

import android.content.Context
import android.widget.Toast

object constants {

    const val JWT_TOKEN_KEY = "JWT_TOKEN_KEY"

    const val BASE_URL = "https://fierce-waters-90770.herokuapp.com"
    const val API_VERSION = "/v1"

    fun Context.showMsg(msg: String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

}