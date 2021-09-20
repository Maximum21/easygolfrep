package com.minhhop.easygolf.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.ListMemberAdapter
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.core.Utils
import com.minhhop.easygolf.framework.models.Participant
import com.minhhop.easygolf.framework.models.Tournament
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.widgets.appbar.AppBarStateChangeListener
import com.squareup.picasso.Picasso

class ScheduleEditActivity : WozBaseActivity(){

    companion object{
        private const val EXPAND_AVATAR_SIZE_DP = 44f
        private const val COLLAPSED_AVATAR_SIZE_DP = 33f
    }

    /**
     * Start zone UIView
     * */
    private lateinit var mAppBarLayout: AppBarLayout

    /**
     * Start view for direction add friend
     * */
    private lateinit var mSpaceAddFriend: Space
    private lateinit var mActionAddFriend: View

    private val mAvatarPoint = FloatArray(2)
    private val mSpaceAvatarPoint = FloatArray(2)

    private lateinit var mTxtAddFriend: TextView
    private lateinit var mTxtName: TextView
    private lateinit var mTxtLocation: TextView
    private lateinit var mTxtDate: TextView
    private lateinit var mImgSchedule:ImageView

    private var mModel: Participant? = null
    private var mIdTournament:Int = 0
    private lateinit var mAppBarStateChangeListener: AppBarStateChangeListener
    private lateinit var mListMemberAdapter:ListMemberAdapter

    override fun setLayoutView(): Int = R.layout.activity_edit_schedule

    override fun initView() {
        mIdTournament = intent.getIntExtra(Contains.EXTRA_ID_TOURNAMENT,0)

        mImgSchedule = findViewById(R.id.imgCourse)
        mListMemberAdapter = ListMemberAdapter(this,true,object : ListMemberAdapter.MemberClickListener{
            override fun onRemoveItem(id: Int, index: Int) {
                removeItemFriend(id)
            }

        })
        val listMember = findViewById<RecyclerView>(R.id.listMember)
        listMember.layoutManager = LinearLayoutManager(this)
        listMember.adapter = mListMemberAdapter

        mSpaceAddFriend = findViewById(R.id.spaceAddFriend)

        mAppBarLayout = findViewById(R.id.appBarLayout)

        mTxtAddFriend = findViewById(R.id.txtAddFriend)

        mAppBarStateChangeListener = object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: STATE) {

            }

            override fun onOffsetChanged(state: STATE, offset: Float) {
                translationView(offset)
            }

        }
        mAppBarLayout.addOnOffsetChangedListener(mAppBarStateChangeListener)
        val observer = mAppBarLayout.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mAppBarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                initLayoutPosition()
            }
        })


        mTxtName = findViewById(R.id.txtName)
        mTxtName.text = intent.getStringExtra(Contains.EXTRA_NAME_COURSE)

        mTxtDate = findViewById(R.id.txtDate)
        mTxtLocation = findViewById(R.id.txtLocation)


        mActionAddFriend = findViewById(R.id.actionAddFriend)
        mActionAddFriend.setOnClickListener {
            mModel?.let { safeModel->
                val bundle = Bundle()
                bundle.putString(Contains.EXTRA_BLACK_LIST,getListFriendByString(safeModel.tournament))
                startActivityForResult(AddPeopleChatActivity::class.java, Contains.REQUEST_CODE_ADD_MEMBER_CHAT,bundle)
            }
        }

    }

    private fun removeItemFriend(userId:Int){
        registerResponse(ApiService.getInstance().golfService.removeFriendTournament(mIdTournament,userId),
                object : HandleResponse<Tournament>{
                    override fun onSuccess(result: Tournament) {

                    }

                })
    }

    private fun getListFriendByString(tournament: Tournament):String{
        val result = StringBuilder()
        val mListFriendTournament = tournament.participants
        val mSize = mListFriendTournament.size
        if(mSize > 0) {
            if (mSize in 1..1) {
                result.append(mListFriendTournament[0].id)
            }
            for (index in 0 until mListFriendTournament.size) {
                if (index < mSize - 1) {
                    result.append("${mListFriendTournament[index].id}-")
                } else {
                    result.append("${mListFriendTournament[index].id}")
                }
            }
        }
        return result.toString()
    }

    private fun translationView(offset:Float){
        mTxtAddFriend.alpha = 1-offset

        val newAvatarSize = Utils.convertDpToPixel(
                EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
                this)
        val expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this)
        val xAvatarOffset = (mSpaceAvatarPoint[0] - mAvatarPoint[0] - (expandAvatarSize - newAvatarSize) / 2f) * offset
        val yAvatarOffset = (mSpaceAvatarPoint[1] - mAvatarPoint[1] - (expandAvatarSize - newAvatarSize)) * offset

        mActionAddFriend.translationX = xAvatarOffset
        mActionAddFriend.translationY = yAvatarOffset
        mActionAddFriend.requestLayout()

    }

    private fun initLayoutPosition() {

        val offset = mAppBarStateChangeListener.getCurrentOffset()
        val newAvatarSize = Utils.convertDpToPixel(
                EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
                this)
        val expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this)

        val avatarPoint = IntArray(2)
        mActionAddFriend.getLocationOnScreen(avatarPoint)
        mAvatarPoint[0] = avatarPoint[0].toFloat() - mActionAddFriend.translationX -
                (expandAvatarSize - newAvatarSize) / 2f

        mAvatarPoint[1] = avatarPoint[1].toFloat() - mActionAddFriend.translationY -
                (expandAvatarSize - newAvatarSize)


        val spacePoint = IntArray(2)
        mSpaceAddFriend.getLocationOnScreen(spacePoint)
        mSpaceAvatarPoint[0] = spacePoint[0].toFloat()
        mSpaceAvatarPoint[1] = spacePoint[1].toFloat()

    }
    override fun loadData() {

        registerResponse(ApiService.getInstance().golfService.getTournamentDetail(mIdTournament),
                object : HandleResponse<Participant> {
                    override fun onSuccess(result: Participant) {
                        mModel = result
                        mTxtName.text = result.tournament.name
                        mTxtLocation.text = result.tournament.club.name
                        mTxtDate.text = AppUtil.fromISO8601UTC(result.tournament.scheduleDate,"dd/MM/yyyy HH:mm")
                        mListMemberAdapter.setDataList(result.tournament.participants)

                        if(result.owner){
                            mActionAddFriend.visibility = View.VISIBLE
                        }else{
                            mActionAddFriend.visibility = View.INVISIBLE
                        }

                        Picasso.get().load(result.tournament.photo)
                                .fit().centerCrop()
                                .into(mImgSchedule)
                    }

                })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == Contains.REQUEST_CODE_ADD_MEMBER_CHAT) {
                data?.let { dataSafe ->

                    val members = dataSafe.getStringExtra(Contains.EXTRA_IS_RETURN)
                    val arrayMembers = members.split("-")

                    val mFriends = HashMap<String,ArrayList<String>>()
                    val mListFriendTournament = ArrayList<String>()
                    for (item in arrayMembers) {
                        val target = item.split("$")
                        mListFriendTournament.add(target[0])
                    }
                    mFriends["friends"] = mListFriendTournament
                }
            }
        }
    }

}