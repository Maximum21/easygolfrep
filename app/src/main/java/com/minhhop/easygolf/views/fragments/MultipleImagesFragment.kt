package com.minhhop.easygolf.views.fragments

import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.minhhop.core.domain.feed.PostImage
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.MultipleImagesAdapter
import com.minhhop.easygolf.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_multiple_images.view.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MultipleImagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MultipleImagesFragment : BaseFragment(){


    private var imagesList: List<PostImage> = ArrayList()
    private var newsFeedAdapter: MultipleImagesAdapter? = null
    override fun setLayout(): Int = R.layout.fragment_multiple_images

    override fun initView(viewRoot: View) {
        context?.let { context->
            val arg = arguments
            val gson = GsonBuilder().create()
            val myString:String = arguments!!.getString("images","")
            Log.e("moreImages","===${myString}")
            imagesList = gson.fromJson<List<PostImage>>(myString, object :TypeToken<List<PostImage>>(){}.type)
            Log.e("moreImages","===${imagesList}")
            viewRoot.recycler_view_multiple_images.layoutManager = LinearLayoutManager(context)
            newsFeedAdapter = MultipleImagesAdapter(context)
            viewRoot.recycler_view_multiple_images.adapter = newsFeedAdapter
            newsFeedAdapter!!.setDataList(imagesList)

        }
    }
}