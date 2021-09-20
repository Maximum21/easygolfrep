package com.minhhop.easygolf.presentation.notification
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.transition.Explode
import android.util.Log
import android.view.Window
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhhop.core.domain.notifications.GolfNotification
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.listeners.OnSwipeTouchListener
import kotlinx.android.synthetic.main.activity_notification.*
import org.koin.android.ext.android.inject

class NotificationActivity : EasyGolfActivity<NotificationViewModel>(), NotificationAdapter.NotificationEvent{


    private var isMonthSet: Boolean = true
    private var isWeekSet: Boolean = true
    private lateinit var itemTouchHelperCallback : OnSwipeTouchListener
    lateinit var notificationAdapter: NotificationAdapter
    private var readItem : Int = -1
    internal val mHandlerSearch = Handler()
    private var deleteItem : Int = -1
    override val mViewModel: NotificationViewModel
            by inject()

    override fun setLayout(): Int {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        window.exitTransition = Explode()


        return R.layout.activity_notification
    }

    override fun initView() {
        back_btn.setOnClickListener {
            finish()
        }

        notificationAdapter = NotificationAdapter(this, this)
        val layoutManager = LinearLayoutManager(this)
        notifications_week_rv.layoutManager = layoutManager
        notifications_week_rv.adapter = notificationAdapter
        notificationAdapter?.registerLoadMore(layoutManager, notifications_week_rv) {
            if (mStatusLoading === StatusLoading.FINISH_LOAD) {
                mViewModel.getNotifications(toolbarSearch.text.toString().trim())
            }
        }
        itemTouchHelperCallback = OnSwipeTouchListener(this,notificationAdapter)

        mViewModel.notifications.observe(this, Observer { itnotif ->
            hideMask()
            if (itnotif?.data?.isNotEmpty() == true) {
                if (mStatusLoading == StatusLoading.REFRESH_LOAD
                        || mStatusLoading == StatusLoading.FIRST_LOAD) {
                    setData(itnotif.data)
                    hideRefreshLoading()
                } else {
                    setData(itnotif.data)
                }
                if (itnotif.paginator.start <= -1) {
                    notificationAdapter?.onReachEnd()
                }
            } else {
                hideRefreshLoading()
                notificationAdapter?.onReachEnd()
                if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                    notificationAdapter?.clearAll()
                    notificationAdapter?.onReachEnd()
                }
            }

            if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                mStatusLoading = StatusLoading.FINISH_LOAD
            }
            hideRefreshLoading()
        })

        mViewModel.readNotify.observe(this, Observer {
            it.date?.let{date->
                if (readItem!=-1) {
                    notificationAdapter.replaceItem(it,readItem)
                }

            }
        })
        toolbarSearch.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mHandlerSearch.removeCallbacksAndMessages(null)
                mHandlerSearch.postDelayed({
                    notificationAdapter?.clearAll()
                    mViewModel.startS=0
                    mViewModel.getNotifications( s.toString().trim())
                }, 500)
            }

        })
        ItemTouchHelper(itemTouchHelperCallback.itemTouchHelperCallback).attachToRecyclerView(notifications_week_rv)
    }

    private fun setData(items: List<GolfNotification>) {
        Log.e("notifcheck","=1=${items.size}")
        val tempData : ArrayList<GolfNotification> = notificationAdapter.mDataList
        Log.e("notifcheck","=2=${tempData.size}")
        val weekList : ArrayList<GolfNotification> = ArrayList()
        val monthList : ArrayList<GolfNotification> = ArrayList()
        for(notif in tempData){
            notif?.date?.let{
                when(AppUtils.isWeek(it.toLong())){
                    0->{
                        weekList.add(notif)
                    }
                    1->{
                        monthList.add(notif)
                    }
                    else -> {}
                }
            }
        }
        for(temp in items){
            temp?.date?.let{
                when(AppUtils.isWeek(it.toLong())){
                    0->{
                        weekList.add(temp)
                    }
                    1->{
                        monthList.add(temp)
                    }
                    else -> {}
                }
            }
        }
        val finalData : ArrayList<GolfNotification> =  ArrayList()
        if(weekList.isNotEmpty()){
            weekList[0].heading = "This Week"
        }
        if(monthList.isNotEmpty()){
            monthList[0].heading = "This Month"
        }
        finalData.addAll(weekList)
        finalData.addAll(monthList)
//        if (notificationAdapter.itemCount<=0) {
            notificationAdapter.setDataList(finalData)
//        }else{
//            notificationAdapter.addListItem(items)
//        }
    }


    override fun loadData() {
        mViewModel.getNotifications("")
    }


    override fun onClickListener(data: GolfNotification, adapterPosition: Int) {
        readItem = adapterPosition
        mViewModel.readNotifications(data.id)
    }

    override fun onDeleteListener(positon: GolfNotification) {
        Log.e("psojhskdf","${positon.id}")
        mViewModel.deleteNotifications(positon)
    }
}