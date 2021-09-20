package com.minhhop.easygolf.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.RoundMatch
import com.minhhop.easygolf.framework.models.Tee
import com.minhhop.easygolf.listeners.RoundListener
import com.minhhop.easygolf.services.image.RoundedTransformationBuilder
import com.minhhop.easygolf.utils.AppUtil
import com.squareup.picasso.Picasso

class RoundAdapter(context: Context, private val mRoundListener: RoundListener) : BaseRecyclerViewAdapter<RoundMatch>(context) {

    override fun setLayout(viewType: Int): Int {
        return R.layout.item_round
    }
    override fun getCustomItemViewType(position: Int): Int {
        return TYPE_ITEM
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): CoreRecyclerViewHolder {
        return RoundHolder(viewRoot)
    }

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is RoundHolder) {
            holder.apply {
                val model = mDataList[position]
                mTxtNameClub.text = model.clubName
                mTxtCreatedAt.text = AppUtil.fromISO8601UTC(model.date,"MMM/dd/yyyy - HH:mm")

                if(model.course.image.isNotEmpty()) {
                    val transformation = RoundedTransformationBuilder()
                            .cornerRadiusDp(15f)
                            .oval(false)
                            .build()
                    Picasso.get().load(model.course.image)
                            .fit()
                            .placeholder(R.drawable.img_holder_golf_radius)
                            .transform(transformation)
                            .into(this.mImgCourse)
                }else{
                    Picasso.get().load(R.drawable.img_holder_golf_radius)
                            .into(this.mImgCourse)
                }

                val dataScoring = StringBuilder()

                if(model.albatross > 0){
                    if(dataScoring.isNotEmpty()){
                        dataScoring.append(",")
                    }
                    dataScoring.append("<b>${model.albatross}</b> albatross")
                }

                if(model.eagle > 0){
                    if(dataScoring.isNotEmpty()){
                        dataScoring.append(",")
                    }
                    dataScoring.append("<b>${model.eagle}</b> eagle")
                }

                if(model.birdie > 0){
                    if(dataScoring.isNotEmpty()){
                        dataScoring.append(",")
                    }
                    dataScoring.append(" <b>${model.birdie}</b> birdie")
                }

                if(model.par > 0){
                    if(dataScoring.isNotEmpty()){
                        dataScoring.append(",")
                    }
                    dataScoring.append(" <b>${model.par}</b> par")
                }

                if(model.bogey > 0){
                    if(dataScoring.isNotEmpty()){
                        dataScoring.append(",")
                    }
                    dataScoring.append(" <b>${model.bogey}</b> bogey")
                }

                if(model.doubleBogeys > 0){
                    if(dataScoring.isNotEmpty()){
                        dataScoring.append(",")
                    }
                    dataScoring.append(" <b>${model.doubleBogeys}</b> 2bogey")
                }

                if(model.others > 0){
                    if(dataScoring.isNotEmpty()){
                        dataScoring.append(",")
                    }
                    dataScoring.append(" <b>${model.others}</b> others")
                }

                mPointHistory.text = HtmlCompat.fromHtml(dataScoring.toString(),HtmlCompat.FROM_HTML_MODE_LEGACY)
                val tempTee = Tee()
                tempTee.type = model.tee
                mIconTee.setColorFilter(tempTee.resOptionIcon,PorterDuff.Mode.MULTIPLY)

                mTxtRoundHDC.text = model.hcp.toString()
                mTxtScore.text = model.score.toString()
                mTxtOver.text = model.over.toString()
            }
        }
    }


    private inner class RoundHolder(itemView: View) : CoreRecyclerViewHolder(itemView) {

        val mTxtNameClub: TextView = itemView.findViewById(R.id.txtNameClub)
        val mTxtCreatedAt: TextView = itemView.findViewById(R.id.txtCreatedAt)
        val mImgCourse:ImageView = itemView.findViewById(R.id.imgCourse)
        val mPointHistory:TextView = itemView.findViewById(R.id.pointHistory)
        val mIconTee:ImageView = itemView.findViewById(R.id.iconTee)

        val mTxtRoundHDC: TextView = itemView.findViewById(R.id.txtRoundHDC)
        val mTxtScore: TextView = itemView.findViewById(R.id.txtScore)
        val mTxtOver: TextView = itemView.findViewById(R.id.txtOver)


        init {
            itemView.setOnClickListener {
                mRoundListener.onClick(mDataList[adapterPosition], null, mTxtNameClub)
            }
        }
    }
}
