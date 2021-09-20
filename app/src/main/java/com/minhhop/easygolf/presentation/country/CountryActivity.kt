package com.minhhop.easygolf.presentation.country

import android.app.Activity
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhhop.core.domain.Country
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import kotlinx.android.synthetic.main.activity_country.*
import org.koin.android.ext.android.inject

class CountryActivity : EasyGolfActivity<CountryViewModel>() , CountryAdapter.CountryPickerItemListener{
    override val mViewModel: CountryViewModel
        by inject()
    private val mHandlerSearch = Handler()
    private var mCountryAdapter: CountryAdapter? = null
    override fun setLayout(): Int = R.layout.activity_country

    override fun initView() {
        mCountryAdapter = CountryAdapter(this, this)
        listCountry.layoutManager = LinearLayoutManager(this)
        listCountry.adapter = mCountryAdapter

        mViewModel.countriesLiveData.observe(this, Observer {
            mCountryAdapter?.setDataList(it)
            hideMask()
        })
        editSearch.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mHandlerSearch.removeCallbacksAndMessages(null)
                mHandlerSearch.postDelayed({
                    mViewModel.fetchCountries(s?.toString())
                },300)
            }
        })
    }

    override fun loadData() {
        viewMask()
        mViewModel.fetchCountries(editSearch.text.toString())
    }

    override fun onClick(country: Country) {
        val intent = EasyGolfNavigation.countryResultDirection(country)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}