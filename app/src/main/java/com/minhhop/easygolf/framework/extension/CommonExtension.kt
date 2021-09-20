package com.minhhop.easygolf.framework.extension

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

fun ImageView.loadImage(url:String?){
    if(!url.isNullOrEmpty()) {
        Picasso.get().load(url)
                .centerCrop()
                .fit()
                .into(this)
    }
}

fun ImageView.loadImage(url:String?,onError:()->Unit){
    if(!url.isNullOrEmpty()) {
        Picasso.get().load(url)
                .centerCrop()
                .fit()
                .into(this,object : Callback{
                    override fun onSuccess() {}
                    override fun onError(e: Exception?) {
                        onError()
                    }
                })
    }else{
        onError()
    }
}

fun ImageView.loadImage(uri:Uri){
    Picasso.get().load(uri)
            .centerCrop()
            .fit()
            .into(this)
}
fun ImageView.loadImage(url:String?,callback: Callback? = null){
    Picasso.get().load(url)
            .centerCrop()
            .fit()
            .into(this,callback)
}

fun ImageView.loadImage(url:String?, @DrawableRes placeHolder:Int){
    if(!url.isNullOrEmpty()){
        Picasso.get().load(url)
                .centerCrop()
                .fit()
                .placeholder(placeHolder)
                .into(this)
    }else{
        Picasso.get().load(placeHolder)
                .centerCrop()
                .fit()
                .into(this)
    }

}