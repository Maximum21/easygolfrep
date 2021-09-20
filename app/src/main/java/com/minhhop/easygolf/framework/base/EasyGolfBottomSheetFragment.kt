package com.minhhop.easygolf.framework.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhhop.easygolf.R

abstract class EasyGolfBottomSheetFragment<out T : EasyGolfViewModel> : BottomSheetDialogFragment() {
    abstract val mViewModel: T?
    internal lateinit var viewRoot: View
    private var mBottomSheetFragmentListener: BottomSheetFragmentListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel?.mCommonErrorLive?.observe(viewLifecycleOwner, Observer {
            (activity as? EasyGolfActivity<*>)?.showCommonMessage(it.message)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewRoot = inflater.inflate(setLayout(), container, false)
        return viewRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(viewRoot)
        loadData()
    }

    open fun onAddCallback(bottomSheetFragmentListener: BottomSheetFragmentListener) {
        mBottomSheetFragmentListener = bottomSheetFragmentListener
    }

    fun collapse(onCollapse: () -> Unit) {
        (dialog as? BottomSheetDialog)?.let { bottomSheetDialog ->
            bottomSheetDialog.behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset <= -1) {
                        onCollapse()
                    }
                }

                @SuppressLint("SwitchIntDef")
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {

                        }
                    }
                }
            })
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
        } ?: dismiss()
    }

    fun collapse() {
        (dialog as? BottomSheetDialog)?.let { bottomSheetDialog ->
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
        } ?: dismiss()
    }

    open fun stateHidden() {
        Log.e("WOW","stateHidden")
        mBottomSheetFragmentListener?.onDisappear()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { setupBottomSheet(it) }
        if (dialog is BottomSheetDialog) {
            dialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet)?.setBackgroundColor(Color.TRANSPARENT)
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialog.behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        stateHidden()
                    }
                }
            })
        }
        dialog.setCanceledOnTouchOutside(canCanceledOnTouchOutside())
        return dialog
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        (dialogInterface as? BottomSheetDialog)?.let { bottomSheetDialog ->
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet)
                    ?: return
            bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    open fun canCanceledOnTouchOutside() = true

    @LayoutRes
    abstract fun setLayout(): Int

    abstract fun initView(viewRoot: View)

    abstract fun loadData()

    abstract fun tag(): String

    fun show(manager: FragmentManager) {
        show(manager, tag)
    }

    interface BottomSheetFragmentListener {
        fun onDisappear()
    }
}