package com.minhhop.easygolf.presentation.club.detail.review

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.presentation.club.detail.EasyGolfClubDetailActivity
import com.minhhop.easygolf.presentation.club.detail.EasyGolfClubDetailViewModel
import kotlinx.android.synthetic.main.fragment_course_review.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ClubReviewFragment : EasyGolfFragment<EasyGolfClubDetailViewModel>(),AddReviewBottomSheetFragment.AddReviewBottomSheetFragmentListener{

    companion object{
        const val TAG = "ClubReviewFragment"
    }
    private var mClubReviewAdapter:ClubReviewAdapter? = null

    override val mViewModel by sharedViewModel<EasyGolfClubDetailViewModel>()

    override fun setLayout(): Int = R.layout.fragment_course_review

    override fun initView(viewRoot: View) {
        context?.let { context ->
            viewRoot.listReview.layoutManager = LinearLayoutManager(context)
            mClubReviewAdapter = ClubReviewAdapter(context)
            viewRoot.listReview.adapter = mClubReviewAdapter
        }

        viewRoot.viewRating.addListener(object : RatingView.RatingViewListener{
            override fun onSelectRating(rate: Int) {
                AddReviewBottomSheetFragment()
                        .setStartRate(rate,mViewModel.currentReviewLiveData.value?.content)
                        .addListener(this@ClubReviewFragment)
                        .show(childFragmentManager,AddReviewBottomSheetFragment.TAG)
            }
        })
    }

    override fun loadData() {
        mViewModel.listReviewsLiveData.observe(viewLifecycleOwner, Observer {
            viewRoot.textTotalReview.text = getString(R.string.counter_review_format,it.size)
            mClubReviewAdapter?.setDataList(it)
        })

        mViewModel.currentReviewLiveData.observe(viewLifecycleOwner, Observer {
            viewRoot.viewRating.setRate(it.rate.toInt())
            mClubReviewAdapter?.addItem(it)
            (activity as? EasyGolfClubDetailActivity)?.hideMask()
        })
    }

    override fun onSubmit(rate: Int, content: String?) {
        (activity as? EasyGolfClubDetailActivity)?.viewMask()
        mViewModel.clubDataLive.value?.let { club->
            mViewModel.addReviewForClub(club.id,rate,content,mViewModel.currentReviewLiveData.value?.id)
        }
    }
}