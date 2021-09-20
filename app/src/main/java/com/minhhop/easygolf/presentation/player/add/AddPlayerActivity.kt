package com.minhhop.easygolf.presentation.player.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhhop.core.domain.User
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.AddPlayerBundle
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import kotlinx.android.synthetic.main.activity_add_player.*
import org.koin.android.ext.android.inject

class AddPlayerActivity : EasyGolfActivity<AddPlayerViewModel>(){
    private var mAddPlayerAdapter:AddPlayerAdapter? = null
    override val mViewModel: AddPlayerViewModel
       by inject()

    internal val mHandlerSearch = Handler()
    private val mBlackList = HashMap<String,User>()
    override fun setLayout(): Int = R.layout.activity_add_player

    override fun initView() {
        val addPlayerBundle = EasyGolfNavigation.addPlayerBundle(intent)
        toolbarBack.title = addPlayerBundle?.title?:getString(R.string.add_player)
        addPlayerBundle?.getBlackList()?.map {
            mBlackList[it.id] = it
        }
        textCounterSelected.text = getString(R.string.done_format,"0")
        textCounterSelected.setOnClickListener {
            val dataIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(AddPlayerBundle::limit.name,addPlayerBundle?.limit?:-1)
            bundle.putString(AddPlayerBundle::result.name,AddPlayerBundle.toData(mAddPlayerAdapter?.getResult()))
            dataIntent.putExtras(bundle)
            setResult(Activity.RESULT_OK,dataIntent)
            finish()
        }

        mAddPlayerAdapter = AddPlayerAdapter(this,addPlayerBundle?.limit?:-1)
        mAddPlayerAdapter?.addListener(object : AddPlayerAdapter.AddPlayerAdapterListener{
            override fun onChange(counter: Int) {
                textCounterSelected.text = getString(R.string.done_format,counter.toString())
            }
        })
        listFriends.layoutManager = LinearLayoutManager(this)
        listFriends.adapter = mAddPlayerAdapter
        mViewModel.listFriends.observe(this, Observer {
            mAddPlayerAdapter?.setDataList(showData(it))
            hideMask()
        })

        editSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val term = s.toString()
                mViewModel.listFriends.value?.let { dataUsers->
                    if (term.isNotEmpty()) {
                        mHandlerSearch.removeMessages(0)
                        mHandlerSearch.postDelayed({
                            val listTemp = dataUsers.filter {
                                val keyword = editSearch.text.toString()
                                if (keyword.isEmpty()) {
                                    true
                                } else {
                                    it.fullName?.contains(editSearch.text.toString(), true)?:true
                                }
                            }

                            mAddPlayerAdapter?.setDataList(showData(listTemp))

                        }, 350)
                    } else {
                        mAddPlayerAdapter?.setDataList(showData(dataUsers))
                    }
                }


            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun showData(data:List<User>):List<User>{
        return if(mBlackList.isEmpty()){
            data
        }else{
            val result = ArrayList<User>()
            data.map {  user->
                if(!mBlackList.containsKey(user.id)){
                    result.add(user)
                }
            }
            result
        }
    }

    override fun loadData() {
        viewMask()
        mViewModel.fetchFriends()
    }
}