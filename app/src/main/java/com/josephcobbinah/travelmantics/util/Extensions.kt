package com.josephcobbinah.travelmantics.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

fun Context.toast(message:String, duration:Int=Toast.LENGTH_SHORT){
    Toast.makeText(this,message,duration).show()
}

fun Context.debug(message:String){
    Log.d("${this.javaClass.simpleName} -> ",message)
}

