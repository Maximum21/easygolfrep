package com.minhhop.easygolf.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.AddPeopleAdapter
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.listeners.EventSelectPeople
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.framework.common.Contains

@Deprecated("not working in v2", ReplaceWith("use AddPlayer in navigation"))
class AddPeopleChatActivity : WozBaseActivity(), EventSelectPeople {
    private lateinit var mCurrentUserEntity: UserEntity
    private var mListIdSender = HashMap<String, UserEntity>()
    private lateinit var mAddPeopleAdapter: AddPeopleAdapter
    private var isReturnValue = false
    private lateinit var mListBlackList: List<String>

    private var mTotalPlayerInBattle = 0

    private lateinit var mEditSearch: EditText
    internal val handler = Handler()
    private val mListDataUser = ArrayList<UserEntity>()

    private lateinit var mActionDone: TextView

    override fun setLayoutView(): Int = R.layout.activity_add_people_chat

    override fun initView() {

        val stringBlackList = intent.getStringExtra(Contains.EXTRA_BLACK_LIST)
        if (stringBlackList.isNullOrEmpty()) {
            mListBlackList = ArrayList()
        } else {
            mListBlackList = stringBlackList.split("-")
        }
        mTotalPlayerInBattle = mListBlackList.size

        Log.e("WOW","mTotalPlayerInBattle: $mTotalPlayerInBattle")
        mListBlackList.forEach {
            Log.e("WOW","back item: $it")
        }

        isReturnValue = intent.getBooleanExtra(Contains.EXTRA_IS_RETURN, false)
        mCurrentUserEntity = DatabaseService.getInstance().currentUserEntity!!

        val isLimit = intent.getBooleanExtra(Contains.EXTRA_LIMIT, false)
        val listFriend: RecyclerView = findViewById(R.id.listFriend)
        mAddPeopleAdapter = AddPeopleAdapter(this, mListBlackList.size, this, isLimit)
        listFriend.apply {
            layoutManager = LinearLayoutManager(this@AddPeopleChatActivity)
            adapter = mAddPeopleAdapter

        }

        mActionDone = findViewById(R.id.actionDone)
        mActionDone.setOnClickListener {
            actionClickDone()
        }

        mEditSearch = findViewById(R.id.editSearch)
        mEditSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val term = s.toString()
                Log.d("onTextChanged", term)
                if (term.isNotEmpty()) {
                    handler.removeMessages(0)
                    handler.postDelayed({
                        val listTemp = mListDataUser.filter {
                            val keyword = mEditSearch.text.toString()
                            if (keyword.isEmpty()) {
                                true
                            } else {
                                val fullName = it.fullName
                                fullName.contains(mEditSearch.text.toString(), true)
                            }
                        }
                        mAddPeopleAdapter.setDataList(listTemp)

                    }, 350)
                } else {
                    mAddPeopleAdapter.setDataList(mListDataUser)
                }

            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun actionClickDone() {
        if (mListIdSender.isEmpty()) {
            Toast.makeText(this@AddPeopleChatActivity,
                    "please choose the people you want !",
                    Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent()
            val bundle = Bundle()
            var data = ""
            for (implement in mListIdSender) {
                data += "${implement.key}$${implement.value.fullName} -"
            }
            data = data.removeRange(data.length - 1, data.length)
            bundle.putString(Contains.EXTRA_IS_RETURN, data)
            intent.putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


    private fun updateShowTotal() {
        mTotalPlayerInBattle = mListIdSender.size

        mActionDone.text = if (mTotalPlayerInBattle > 0) {
            mActionDone.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            getString(R.string.done_format, mTotalPlayerInBattle.toString())
        } else {
            mActionDone.setTextColor(ContextCompat.getColor(this, R.color.colorHint))
            getString(R.string.done)
        }
    }

    override fun loadData() {
        showLoading()
        registerResponse(ApiService.getInstance().generalService.listFriend(),object : HandleResponse<List<UserEntity>>{
            override fun onSuccess(result: List<UserEntity>) {
                var clearList = result
                if (!mListBlackList.isNullOrEmpty()) {
                    for (item in mListBlackList) {
                        clearList = clearList.filter {
                            it.id != item
                        }
                    }
                }

                mListDataUser.addAll(clearList)
                mAddPeopleAdapter.setDataList(clearList)

                hideLoading()
            }

        })
    }

    override fun onSelect(userEntity: UserEntity) {
        mListIdSender[userEntity.id] = userEntity
        updateShowTotal()
    }

    override fun onUnSelect(userEntity: UserEntity) {
        mListIdSender.remove(userEntity.id)
        updateShowTotal()
    }
}