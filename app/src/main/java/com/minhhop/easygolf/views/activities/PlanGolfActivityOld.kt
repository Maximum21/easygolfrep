package com.minhhop.easygolf.views.activities

import android.content.DialogInterface
import android.view.View
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS
import com.minhhop.easygolf.framework.models.DataLocation
import com.minhhop.easygolf.framework.models.Match
import com.minhhop.easygolf.presentation.golf.PlayGolfActivityOld
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.framework.common.Contains
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PlanGolfActivityOld : PlayGolfActivityOld() {

    private var mCurrentMath:Match? = null
    override fun getOverrideData(dataLocation: DataLocation) {

        val idRound = intent.getIntExtra(Contains.EXTRA_ID_ROUND, -1)
        if (idRound == -1) {
            finish()
        } else {
            ApiService.getInstance().golfService.getRoundDetail(idRound.toString())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        mRoundMathCurrent = result
                        mCurrentMath = result.matches[0]
                        mHoleCurrent = mCurrentMath?.hole

                        mRoundMathCurrent?.apply {
                            if(matches != null){
                                if (mCurrentMath!!.hole.greens != null){
                                    /**
                                     * set list hole for adapter
                                     * */
                                    mPickerHoleAdapter!!.addListItem(mRoundMathCurrent!!.holes)
                                    mPickerHoleAdapter!!.setSelected(mHoleCurrent!!.number)

                                    if(this.status.equals("Finished",true)){
                                        planIsFinish()
                                    }

                                    getProfileUser()
                                }else{
                                    AlertDialogIOS(this@PlanGolfActivityOld, resources.getString(R.string.error_golf),
                                            DialogInterface.OnCancelListener { onBackPressed() }).show()
                                }

                            }else{
                                AlertDialogIOS(this@PlanGolfActivityOld, resources.getString(R.string.error_golf),
                                        DialogInterface.OnCancelListener { onBackPressed() }).show()
                            }
                        }
                    }, { throwable ->
                        hideLoading()
                        // show back button
                        AlertDialogIOS(this, throwable.localizedMessage) { finish() }.show()
                    })
        }
    }

    private fun planIsFinish(){
        mWozGolfButton?.visibility = View.GONE
//        mDropTrackShot?.visibility = View.GONE
    }

    override fun dataPlanPlayGolf() {
        mHoleCurrent?.apply {
            mCurrentMath = mRoundMathCurrent!!.matches[number - 1]
            mCurrentMath?.apply {
                mDataPlayGolfs[number].mValueScore = score
                mDataPlayGolfs[number].mValuePutt = putts

//                for (e in this.points) {
//                    mHistoryPointMap[mHoleCurrent!!.number - 1].add(LatLng(e.latitude,e.longitude))
//                }
            }

        }

    }
}