package com.minhhop.easygolf.presentation.country

import android.content.Context
import android.view.View
import com.minhhop.core.domain.Country
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.models.entity.CountryEntity
import kotlinx.android.synthetic.main.item_country.view.*

class CountryAdapter(context: Context, private val mCountryPickerItemListener: CountryPickerItemListener?)
    : EasyGolfRecyclerViewAdapter<Country>(context) {

    override fun setLayout(viewType: Int): Int {
        return R.layout.item_country
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder {
        return CountryViewHolder(viewRoot)
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? CountryViewHolder)?.bindData(position)
    }

    inner class CountryViewHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView) {
        init {
            itemView.viewRoot.setOnClickListener {
                mCountryPickerItemListener?.onClick(getItem(adapterPosition))
            }
        }
        fun bindData(position: Int){
            val model = getItem(position)
            itemView.txtNameCountry.text = context.getString(R.string.format_name_country, model.nice_name, model.phone_code?.toString())
            AppUtils.setFlagDrawableAssets(context,model.iso,itemView.imgFlag)
        }
    }
    interface CountryPickerItemListener {
        fun onClick(country: Country)
    }
}