package com.minhhop.easygolf.presentation.endgame.chart

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.presentation.endgame.EndGameViewModel
import kotlinx.android.synthetic.main.fragment_green_chart.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class GreenChartFragment : EasyGolfFragment<EndGameViewModel>() {

    override fun setLayout(): Int = R.layout.fragment_green_chart

    override val mViewModel: EndGameViewModel
            by sharedViewModel()

    override fun onResume() {
        super.onResume()
        if (mStatusLoading == StatusLoading.FIRST_LOAD) {
            mStatusLoading = StatusLoading.FINISH_LOAD
        } else {
            if (mStatusLoading == StatusLoading.REFRESH_LOAD) {
                viewRoot.chartGreenInRegulation.invalidate()
                mStatusLoading = StatusLoading.FINISH_LOAD
            }
        }
    }

    private fun initChart() {
        context?.let { context ->
            viewRoot.chartGreenInRegulation.isRotationEnabled = false
            viewRoot.chartGreenInRegulation.description = null
            viewRoot.chartGreenInRegulation.legend.isEnabled = true

            val entries = ArrayList<LegendEntry>()

            val entryMiss = LegendEntry()
            entryMiss.formColor = ContextCompat.getColor(context, R.color.color_chart_miss)
            entryMiss.label = getString(R.string.miss)
            entries.add(entryMiss)

            val entryHit = LegendEntry()
            entryHit.formColor = ContextCompat.getColor(context, R.color.color_chart_hit)
            entryHit.label = getString(R.string.hit)
            entries.add(entryHit)
//
            viewRoot.chartGreenInRegulation.legend.apply {
                this.setCustom(entries)
                this.setDrawInside(true)
                this.orientation = Legend.LegendOrientation.VERTICAL
                this.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                this.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                this.formSize = 13f
                this.textSize = 13f
                this.yOffset = 30f
            }

            viewRoot.chartGreenInRegulation.isDrawHoleEnabled = true
            viewRoot.chartGreenInRegulation.setDrawCenterText(true)
            viewRoot.chartGreenInRegulation.setDrawRoundedSlices(false)
            viewRoot.chartGreenInRegulation.maxAngle = 360f
            viewRoot.chartGreenInRegulation.rotationAngle = 90f
            viewRoot.chartGreenInRegulation.centerText = ""

            viewRoot.chartGreenInRegulation.setDrawEntryLabels(true)
            viewRoot.chartGreenInRegulation.setEntryLabelColor(ContextCompat.getColor(context, R.color.colorBlack))
            viewRoot.chartGreenInRegulation.setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
            viewRoot.chartGreenInRegulation.isHighlightPerTapEnabled = false
        }
    }

    override fun initView(viewRoot: View) {
        initChart()
        mViewModel.listDataScoreGolfLiveData.observe(viewLifecycleOwner, Observer {
            mViewModel.getGreenInRegulation(it) { miss, hit ->
                setDataForChart(miss.toFloat(), hit.toFloat())

                if (mStatusLoading == StatusLoading.FINISH_LOAD) {
                    viewRoot.chartGreenInRegulation.invalidate()
                } else {
                    mStatusLoading = StatusLoading.REFRESH_LOAD
                }
            }
        })
    }

    private fun setDataForChart(miss: Float, hit: Float) {
        val total = miss + hit
        val percentHit = Math.round((hit / total) * 100)
        val percentMiss = 100 - percentHit
        val values = ArrayList<PieEntry>()
        if (miss > 0f) {
            val item1 = PieEntry((miss / total) * 100, getString(R.string.percent, percentMiss.toString()))
            values.add(item1)
        }

        if (hit > 0f) {
            val item = PieEntry((hit / total) * 100, getString(R.string.percent, percentHit.toString()))
            values.add(item)
        }

        val dataSet = PieDataSet(values, "")
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.color_chart_hit)
        dataSet.valueTypeface = Typeface.DEFAULT_BOLD
        dataSet.valueLinePart1OffsetPercentage = 50f
        dataSet.valueLinePart2Length = 1.3f
        dataSet.valueLinePart1Length = 0.7f
        dataSet.sliceSpace = 0f
        dataSet.selectionShift
        dataSet.selectionShift = 30f

        context?.let { context ->
            val colorChart = ContextCompat.getColor(context, R.color.color_chart_hit)
            val colorChartHighLight = ContextCompat.getColor(context, R.color.color_chart_miss)
            val mListColor = ArrayList<Int>()
            if (miss > 0f) {
                mListColor.add(colorChartHighLight)
            }
            if (hit > 0f) {
                mListColor.add(colorChart)
            }

            dataSet.colors = mListColor
        }

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(0f)
        data.setValueTextColor(Color.WHITE)
        viewRoot.chartGreenInRegulation.data = data

    }

    override fun loadData() {

    }
}