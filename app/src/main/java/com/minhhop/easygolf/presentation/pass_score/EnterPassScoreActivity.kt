package com.minhhop.easygolf.presentation.pass_score

import androidx.lifecycle.Observer
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.PassScoreBundle
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import kotlinx.android.synthetic.main.activity_enter_pass_score.*
import org.koin.android.ext.android.inject
import java.util.*

class EnterPassScoreActivity : EasyGolfActivity<PassScoreViewModel>() {


    private var mPassScoreBundle: PassScoreBundle? = null
    private var mResultTime: Calendar? = null
    override val mViewModel: PassScoreViewModel
            by inject()

    override fun setLayout(): Int = R.layout.activity_enter_pass_score

    override fun initView() {
        EasyGolfNavigation.passScoreBundle(intent)?.let {
            mPassScoreBundle = it
        }?:finish()

        mViewModel.passScoreCallbackLive.observe(this, Observer {
            hideMask()
            MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.pass_score_submitted))
                    .setNegativeButton(getString(android.R.string.ok)) { _, _ -> finish() }
                    .show()
        })

        pickerDate.setOnClickListener {
            buildDatePickerDialog(MaterialPickerOnPositiveButtonClickListener {
                buildTimePickerDialog { hour, minute ->
                    if (mResultTime == null) {
                        mResultTime = Calendar.getInstance()
                    }
                    mResultTime?.timeInMillis = it
                    mResultTime?.set(Calendar.HOUR_OF_DAY, hour)
                    mResultTime?.set(Calendar.MINUTE, minute)
                    mResultTime?.let { time->
                        pickerDate.text = AppUtils.formatDateTime(time.timeInMillis)
                    }
                }
            }
            )
        }

        btSubmit.setOnClickListener {
            mPassScoreBundle?.let { passScoreBundle ->
                mResultTime?.let { time ->
                    inputGrossScore.text?.toString()?.toDoubleOrNull()?.let { grossScore ->
                        inputAdjustedScore.text?.toString()?.toDoubleOrNull()?.let { adjustedScore ->
                            viewMask()
                            mViewModel.passScoreForCourse(
                                    passScoreBundle.courseId,
                                    passScoreBundle.teeType,
                                    grossScore,
                                    adjustedScore,
                                    time.timeInMillis
                            )
                        } ?: toast(getString(R.string.missing_filed))
                    } ?: toast(getString(R.string.missing_filed))
                } ?: toast(getString(R.string.missing_filed))


            }
        }

        viewClose.setOnClickListener {
            finish()
        }
    }

    private fun buildDatePickerDialog(callback: MaterialPickerOnPositiveButtonClickListener<Long>) {
        val datePickerDialogBuilder = MaterialDatePicker.Builder.datePicker()
        mResultTime?.let { time ->
            datePickerDialogBuilder.setSelection(time.timeInMillis)
        }
        val datePickerDialog = datePickerDialogBuilder.build()

        datePickerDialog.addOnPositiveButtonClickListener(callback)
        datePickerDialog.show(supportFragmentManager, "MaterialDatePicker")
    }

    private fun buildTimePickerDialog(onSelect: (Int, Int) -> Unit) {
        val datePickerDialogBuilder = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
        mResultTime?.let { time ->
            datePickerDialogBuilder.setHour(time.get(Calendar.HOUR_OF_DAY))
            datePickerDialogBuilder.setMinute(time.get(Calendar.MINUTE))
        }
        val datePickerDialog = datePickerDialogBuilder.build()
        datePickerDialog.addOnPositiveButtonClickListener {
            onSelect(datePickerDialog.hour, datePickerDialog.minute)
        }
        datePickerDialog.show(supportFragmentManager, "MaterialDatePicker")
    }

    override fun loadData() {

    }
}