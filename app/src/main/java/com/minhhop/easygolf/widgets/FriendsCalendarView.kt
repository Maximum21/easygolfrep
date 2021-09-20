package com.minhhop.easygolf.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.models.FriendTournament
import com.minhhop.easygolf.utils.AppUtil

class FriendsCalendarView : FrameLayout {

    private var mElevation = 5f

    private var mPaddingView:Int = 0

    private lateinit var mImgUserOne:ImageView
    private lateinit var mImgUserTwo:ImageView
    private lateinit var mImageMore:TextView

    constructor(context: Context) : super(context){ initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){  initView() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){  initView() }

    private fun initView(){
        mPaddingView = AppUtil.dp2px(2f,context).toInt()

        mImgUserOne = ImageView(context)
        addView(mImgUserOne)
        standardizedView(mImgUserOne,0)
        mImgUserOne.background = ContextCompat.getDrawable(context,R.drawable.ic_bg_icon_chat_group)
        mImgUserOne.elevation = mElevation
        mElevation++

        mImgUserTwo = ImageView(context)
        addView(mImgUserTwo)
        mImgUserTwo.background = ContextCompat.getDrawable(context,R.drawable.ic_bg_icon_chat_group)
        standardizedView(mImgUserTwo,1)
        mImgUserTwo.elevation = mElevation
        mElevation++

        mImageMore = TextView(context)
        addView(mImageMore)
        mImageMore.background = ContextCompat.getDrawable(context,R.drawable.img_more_schedule)
        mImageMore.gravity = Gravity.CENTER
        mImageMore.setTextSize(TypedValue.COMPLEX_UNIT_SP,14f)
        mImageMore.setTextColor(ContextCompat.getColor(context,R.color.colorBlack))
        standardizedView(mImageMore,2)
        mImageMore.elevation = mElevation
        mElevation++

        mImgUserOne.visibility = View.GONE
        mImgUserTwo.visibility = View.GONE
        mImageMore.visibility = View.GONE
    }

    private fun standardizedView(target:View,index:Int){

        val param = target.layoutParams as LayoutParams
        val size = AppUtil.convertDpToPixel(35,context)
        param.width = size
        param.height = size

        param.marginEnd = (0.8 * size * index).toInt()
        param.gravity = Gravity.END
        target.setPadding(mPaddingView,mPaddingView,mPaddingView,mPaddingView)
        target.requestLayout()
    }

    fun setData(listFriends:List<FriendTournament>){
        if(listFriends.isNullOrEmpty()){
            mImgUserOne.visibility = View.GONE
            mImgUserTwo.visibility = View.GONE
            mImageMore.visibility = View.GONE
            return
        }
        if(listFriends.size == 1){
            loadImage(listFriends[0],mImgUserOne)
            mImgUserOne.visibility = View.VISIBLE

            mImgUserTwo.visibility = View.GONE
            mImageMore.visibility = View.GONE
        }else{

            loadImage(listFriends[0],mImgUserOne)
            loadImage(listFriends[1],mImgUserTwo)

            if(listFriends.size > 2){
                mImgUserOne.visibility = View.VISIBLE

                mImgUserTwo.visibility = View.VISIBLE
                mImageMore.visibility = View.VISIBLE

                val sizeMore = listFriends.size - 2

                mImageMore.text = context.getString(R.string.value_more,sizeMore.toString())

            }else{
                mImgUserOne.visibility = View.VISIBLE
                mImgUserTwo.visibility = View.VISIBLE
                mImageMore.visibility = View.GONE
            }
        }
    }


    private fun loadImage(friendTournament: FriendTournament,target:ImageView){
        if(friendTournament.avatar.isNullOrEmpty()){
            loadImageByFirebase(friendTournament.id.toString(),target)
        }else{
            loadImageByGlide(friendTournament.avatar,target)
        }
    }

    private fun loadImageByFirebase(idUser:String,target: ImageView){
        FirebaseDatabase.getInstance().getReference("users")
                .child(idUser).child("profile").child("avatar")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        val url = p0.getValue(String::class.java)
                        url?.let { urlSafe->
                            loadImageByGlide(urlSafe,target)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
    }

    private fun loadImageByGlide(urlImage:String,target:ImageView){
        Glide.with(context).load(urlImage)
                .error(R.drawable.ic_icon_user_default)
                .circleCrop()
                .placeholder(R.drawable.ic_icon_user_default)
                .into(target)
    }
}