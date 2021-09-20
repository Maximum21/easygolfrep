package com.minhhop.easygolf.presentation.scorecard

import android.animation.LayoutTransition
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.core.domain.golf.Hole
import com.minhhop.core.domain.golf.Scorecard
import com.minhhop.core.domain.golf.ScorecardModel
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.models.ScoringTable
import com.minhhop.easygolf.presentation.custom.EasyGolfAvatarView
import com.minhhop.easygolf.presentation.golf.component.score.picker.EasyGolfPickerScoreView
import com.minhhop.easygolf.presentation.scorecard.EasyGolfScoringTableView.TypeRow.*
import com.minhhop.easygolf.widgets.WozFairwayGreen
import kotlin.math.roundToInt


class EasyGolfScoringTableView : RelativeLayout {

    enum class TypeRow {
        PAR_ROW,
        SI_ROW,
        SCORE_ROW,
        FAIRWAY_HIT_ROW,
        GIR_ROW,
        PUTT_ROW
    }

    enum class TypeView {
        SCORE,
        OVER
    }

    /**
     * Layout ne
     *                     @mScrollContainerRow (HorizontalScrollView)
     *                     @mContainerTableMain (TableLayout)
     *                         @rowHeader
     *                         @mTableMainRow->mNestScrollVerticalContainerRow->TableLayout->row
     *
     * */

    /**
     * column start header
     * */
    private var mContainerHeaderStartColumn: LinearLayout? = null
    private lateinit var mNestedScrollVerticalHeaderStartColumn: NestedScrollView
    private lateinit var mContainerHeaderStartScrollView: LinearLayout

    /**
     * column end header
     * */
    private lateinit var mContainerHeaderEndColumn: LinearLayout
    private lateinit var mNestedScrollVerticalHeaderEndColumn: NestedScrollView
    private lateinit var mContainerHeaderEndScrollView: LinearLayout

    /**
     * data view
     * */
    private lateinit var mTableMainRow: TableRow
    private lateinit var mContainerTableMain: TableLayout
    private lateinit var mScrollContainerRow: HorizontalScrollView
    private lateinit var mNestScrollVerticalContainerRow: NestedScrollView

    private var mSizeUserAvatar = 0
    private var mHeightRowScoreVertical = 0
    private var mOrientation = Configuration.ORIENTATION_PORTRAIT
    private var mSmallMargin = 0
    private var mColorHighlightHeaderRow: Int = 0
    private var mColorHighlightUserRow: Int = 0
    private var isHighlightRow = false
    private var isNeedToExpandView = true

    private var mListCacheScoreUser = HashMap<String, ArrayList<ScoringTable>>()
    private var mCacheStartHalfTotal: Int? = null
    private var mCacheEndHalfTotal: Int? = null
    private var mCurrentUserId: String? = null
    private var totalHalfScore: Int? = null
    private var totalEndScore: Int? = null

    /**
     * expand views
     * **/
    private var mListHoldExpandView = HashMap<String, ArrayList<View>>()
    private var mTagHoldViewNeedCollapse: String? = null

    constructor(context: Context) : super(context) {
        initView(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EasyGolfScoringTableView)
            isHighlightRow = typedArray.getBoolean(R.styleable.EasyGolfScoringTableView_showHighlight, false)
            isNeedToExpandView = typedArray.getBoolean(R.styleable.EasyGolfScoringTableView_expandView, false)

            typedArray.recycle()
        }

        mColorHighlightHeaderRow = ContextCompat.getColor(context, R.color.colorHighlightHeaderRow)
        mColorHighlightUserRow = ContextCompat.getColor(context, R.color.colorHighlightUserRow)

        mOrientation = if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mSizeUserAvatar = context.resources.getDimension(R.dimen.size_avatar_user_scorecard_horizontal_default).toInt()
            mHeightRowScoreVertical = context.resources.getDimension(R.dimen.height_row_scorecard_horizontal_default).toInt()
            Configuration.ORIENTATION_LANDSCAPE
        } else {
            mSizeUserAvatar = context.resources.getDimension(R.dimen.size_avatar_user_scorecard_vertical_default).toInt()
            mHeightRowScoreVertical = context.resources.getDimension(R.dimen.height_row_scorecard_vertical_default).toInt()
            Configuration.ORIENTATION_PORTRAIT
        }
        mSmallMargin = (context.resources.getDimension(R.dimen.small_margin) * 1.3).toInt()

        mHeightRowScoreVertical = context.resources.getDimension(R.dimen.height_row_scorecard_vertical_default).toInt()


        mContainerHeaderStartColumn = LinearLayout(context)
        mContainerHeaderStartScrollView = LinearLayout(context)
        mContainerHeaderEndColumn = LinearLayout(context)
        mContainerHeaderEndScrollView = LinearLayout(context)
        mContainerTableMain = TableLayout(context)
        mTableMainRow = TableRow(context)
        mScrollContainerRow = HorizontalScrollView(context)

        mNestScrollVerticalContainerRow = object : NestedScrollView(context) {
            override fun onScrollChanged(scrollX: Int, scrollY: Int, oldl: Int, oldt: Int) {
                super.onScrollChanged(scrollX, scrollY, oldl, oldt)
                mNestedScrollVerticalHeaderStartColumn.scrollY = scrollY
                mNestedScrollVerticalHeaderEndColumn.scrollY = scrollY
            }
        }

        mNestedScrollVerticalHeaderStartColumn = object : NestedScrollView(context) {
            override fun onScrollChanged(scrollX: Int, scrollY: Int, oldl: Int, oldt: Int) {
                super.onScrollChanged(scrollX, scrollY, oldl, oldt)
                mNestScrollVerticalContainerRow.scrollY = scrollY
                mNestedScrollVerticalHeaderEndColumn.scrollY = scrollY
            }
        }

        mNestedScrollVerticalHeaderEndColumn = object : NestedScrollView(context) {
            override fun onScrollChanged(scrollX: Int, scrollY: Int, oldl: Int, oldt: Int) {
                super.onScrollChanged(scrollX, scrollY, oldl, oldt)
                mNestedScrollVerticalHeaderStartColumn.scrollY = scrollY
                mNestScrollVerticalContainerRow.scrollY = scrollY
            }
        }

        mContainerHeaderStartColumn?.layoutTransition = LayoutTransition()
        mContainerHeaderStartColumn?.id = View.generateViewId()
        mContainerHeaderStartColumn?.gravity = Gravity.START or Gravity.CENTER
        mContainerHeaderStartColumn?.orientation = LinearLayout.VERTICAL
        addView(mContainerHeaderStartColumn)


        mContainerHeaderStartScrollView.layoutTransition = LayoutTransition()
        mContainerHeaderStartScrollView.orientation = LinearLayout.VERTICAL
        mNestedScrollVerticalHeaderStartColumn.addView(mContainerHeaderStartScrollView)


        mContainerHeaderEndColumn.layoutTransition = LayoutTransition()
        mContainerHeaderEndColumn.id = View.generateViewId()
        mContainerHeaderEndColumn.orientation = LinearLayout.VERTICAL
        addView(mContainerHeaderEndColumn)
        (mContainerHeaderEndColumn.layoutParams as? LayoutParams)?.addRule(ALIGN_PARENT_END)


        mContainerHeaderEndScrollView.layoutTransition = LayoutTransition()
        mContainerHeaderEndScrollView.orientation = LinearLayout.VERTICAL
        mNestedScrollVerticalHeaderEndColumn.addView(mContainerHeaderEndScrollView)

        mScrollContainerRow.isHorizontalScrollBarEnabled = false
        addView(mScrollContainerRow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mContainerHeaderStartColumn?.let { anchorStartView ->
            (mScrollContainerRow.layoutParams as? LayoutParams)?.addRule(END_OF, anchorStartView.id)
        }
        (mScrollContainerRow.layoutParams as? LayoutParams)?.addRule(START_OF, mContainerHeaderEndColumn.id)

        mContainerTableMain.layoutTransition = LayoutTransition()
        mScrollContainerRow.addView(mContainerTableMain)

    }

    fun clearAllView() {
        mOrientation = if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mSizeUserAvatar = context.resources.getDimension(R.dimen.size_avatar_user_scorecard_horizontal_default).toInt()
            mHeightRowScoreVertical = context.resources.getDimension(R.dimen.height_row_scorecard_horizontal_default).toInt()
            Configuration.ORIENTATION_LANDSCAPE
        } else {
            mSizeUserAvatar = context.resources.getDimension(R.dimen.size_avatar_user_scorecard_vertical_default).toInt()
            mHeightRowScoreVertical = context.resources.getDimension(R.dimen.height_row_scorecard_vertical_default).toInt()
            Configuration.ORIENTATION_PORTRAIT
        }
    }

    private fun buildHeaderRow(sizeHole: Int, widthCell: Int): TableRow {
        val rowContainer = TableRow(context)
        val linearLayoutHeaderRow = LinearLayout(context)
        linearLayoutHeaderRow.orientation = LinearLayout.HORIZONTAL
        rowContainer.addView(linearLayoutHeaderRow)
        val count = sizeHole + (sizeHole / 9) + 1
        var styleText: Int
        for (index in 1 until count) {
            val title = when (index) {
                10 -> {
                    styleText = R.style.bold
                    context.getString(R.string.out)
                }
                20 -> {
                    styleText = R.style.bold
                    context.getString(R.string._in)
                }
                else -> {
                    styleText = R.style.normal
                    if (index > 10) {
                        (index - 1).toString()
                    } else {
                        index.toString()
                    }
                }
            }
            linearLayoutHeaderRow.addView(
                    createWidgetText(
                            title,
                            ContextCompat.getColor(context, R.color.textColorDark)
                            , styleText,
                            widthCell
                    )
            )
        }
        return rowContainer
    }

    private fun buildTotalRow(listValue: List<Int>, widthCell: Int, typeRow: TypeRow? = null): TableRow {
        val rowContainer = TableRow(context)
        val count = listValue.size + (listValue.size / 9)
        var totalResult = 0
        var styleText: Int
        for (index in 0 until count) {
            val title = when (index) {
                9, 19 -> {
                    styleText = R.style.bold
                    val temp = totalResult
                    if (typeRow == PAR_ROW) {
                        if (mCacheStartHalfTotal == null) {
                            mCacheStartHalfTotal = totalResult
                        } else {
                            mCacheEndHalfTotal = totalResult
                        }
                    }
                    totalResult = 0
                    temp.toString()
                }
                else -> {
                    styleText = R.style.normal
                    val itemValue = if (index >= 10) {
                        listValue[index - 1]
                    } else {
                        listValue[index]
                    }
                    totalResult += itemValue
                    itemValue.toString()
                }
            }
            rowContainer.addView(
                    createWidgetText(
                            title,
                            ContextCompat.getColor(context, R.color.textColorDark)
                            , styleText,
                            widthCell
                    )
            )
        }
        return rowContainer
    }

    private fun getScoringTableByType(typeRow: TypeRow, holes: List<Hole>, dataScoreGolfModel: ScorecardModel?): ArrayList<ScoringTable> {
        val scoreRowList = ArrayList<ScoringTable>()
        totalHalfScore = 0
        totalEndScore = 0
        holes.forEachIndexed { index, element ->
            var valuePlusInRound: Int? = null
            when (typeRow) {
                PAR_ROW -> {
                    scoreRowList.add(ScoringTable(element.par))
                    valuePlusInRound = element.par
                }
                SI_ROW -> {
                    scoreRowList.add(ScoringTable(element.index))
                    valuePlusInRound = element.index
                }
                SCORE_ROW -> {
                    val dataScore = getScoreAtHole(element.number, dataScoreGolfModel?.scorecard)
                    if (dataScore != null) {
                        scoreRowList.add(ScoringTable(
                                dataScore.score, element.par
                        ))
                        valuePlusInRound = dataScore.score
                    } else {
                        valuePlusInRound = 0
                        scoreRowList.add(ScoringTable(
                                null, element.par
                        ))
                    }

                }
                FAIRWAY_HIT_ROW -> {
                    val dataScore = getScoreAtHole(element.number, dataScoreGolfModel?.scorecard)
                    if (dataScore != null) {
                        scoreRowList.add(ScoringTable(
                                if (dataScore.getFairwayHit() != DataScoreGolf.FairwayHit.NONE) dataScore.fairwayHit else null, element.par
                        ))
                        valuePlusInRound = if (dataScore.getFairwayHit() == DataScoreGolf.FairwayHit.CENTER) {
                            1
                        } else 0
                    } else {
                        valuePlusInRound = 0
                        scoreRowList.add(ScoringTable(
                                null, element.par
                        ))
                    }
                }
                GIR_ROW -> {
                    val dataScore = getScoreAtHole(element.number, dataScoreGolfModel?.scorecard)
                    if (dataScore != null) {
                        scoreRowList.add(ScoringTable(
                                if (dataScore.getGreenInRegulation() != DataScoreGolf.GreenInRegulation.NONE) dataScore.greenInRegulation else null,
                                element.par
                        ))
                        valuePlusInRound = if (dataScore.getGreenInRegulation() == DataScoreGolf.GreenInRegulation.HIT) {
                            1
                        } else 0

                    } else {
                        valuePlusInRound = 0
                        scoreRowList.add(ScoringTable(
                                null, element.par
                        ))
                    }

                }
                PUTT_ROW -> {
                    val dataScore = getScoreAtHole(element.number, dataScoreGolfModel?.scorecard)
                    if (dataScore != null) {
                        scoreRowList.add(ScoringTable(
                                if (dataScore.getPutt() == DataScoreGolf.PUTT.EXIT) dataScore.putts else null, element.par
                        ))
                        valuePlusInRound = if (dataScore.putts != -1) {
                            dataScore.putts
                        } else 0
                    } else {
                        valuePlusInRound = 0
                        scoreRowList.add(ScoringTable(
                                null, element.par
                        ))
                    }
                }
            }

            if (index < 9) {
                totalHalfScore = (totalHalfScore ?: 0) + (valuePlusInRound ?: 0)
            } else {
                totalEndScore = (totalEndScore ?: 0) + (valuePlusInRound ?: 0)
            }
        }
        totalHalfScore = if (totalHalfScore == 0) {
            null
        } else totalHalfScore

        totalEndScore = if (totalEndScore == 0) {
            null
        } else totalEndScore

        return scoreRowList
    }

    private fun isNeedToShowHighlight(typeRow: TypeRow, userId: String?): Boolean = (userId == mCurrentUserId && isHighlightRow && typeRow == SCORE_ROW)


    private fun buildTotalScoreRow(holes: List<Hole>, dataScoreGolfModel: ScorecardModel?,
                                   widthCell: Int, typeRow: TypeRow, title: String? = null, callback: (TableRow, ViewGroup) -> Unit) {
        /**
         * title view row
         * */
        var titleStartCell: View? = null
        if (typeRow == SCORE_ROW) {
            val avatarUserView = createWidgetAvatar(holes, dataScoreGolfModel, widthCell, isNeedToShowHighlight(typeRow, dataScoreGolfModel?.userId))
            avatarUserView.tag = dataScoreGolfModel?.userId
            avatarUserView.setOnClickListener {
                dataScoreGolfModel?.userId?.let { userId ->
                    if (userId != mTagHoldViewNeedCollapse) {
                        mListHoldExpandView[mTagHoldViewNeedCollapse]?.forEach { itemView ->
                            collapseAction(itemView)
                        }
                        mTagHoldViewNeedCollapse = userId
                    }
                    mListHoldExpandView[userId]?.let { listExpand ->
                        listExpand.forEach { itemView ->
                            if (itemView.visibility == View.VISIBLE) {
                                collapseAction(itemView)
                            } else {
                                expandAction(itemView)
                            }
                        }
                    }
                }
            }
        } else {
            titleStartCell = createWidgetTextStartRow(title, heightDefault = widthCell.coerceAtLeast(mSizeUserAvatar) + mSmallMargin * 2)
        }

        val rowContainer = TableRow(context)
        val marginStartEnd = mSmallMargin / 4
        val widthScoreCell = if (holes.size > 9) {
            (widthCell - (marginStartEnd * 2))
        } else {
            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                mSizeUserAvatar
            } else {
                widthCell / 2
            }
        }

        getScoringTableByType(typeRow, holes, dataScoreGolfModel).let { data ->
            if (typeRow == SCORE_ROW) {
                dataScoreGolfModel?.userId?.let { userId ->
                    mListCacheScoreUser[userId] = ArrayList(data)
                    Log.e("WOW","mListCacheScoreUser.size ${mListCacheScoreUser.size}")
                    if(holes.size >= 9) {
                        mListCacheScoreUser[userId]?.add(9, ScoringTable(totalHalfScore, mCacheStartHalfTotal))
                    }
                    if(holes.size >= 18) {
                        mListCacheScoreUser[userId]?.add(19, ScoringTable(totalEndScore, mCacheEndHalfTotal))
                    }
                }
            }
            val count = holes.size + (holes.size / 9)
            for (index in 0 until count) {
                val cellScoreView: View = when (index) {
                    9 -> {
                        val totalOut = if (typeRow == FAIRWAY_HIT_ROW || typeRow == GIR_ROW) {
                            context.getString(R.string.percent, ((totalHalfScore
                                    ?: 0) * 100.0 / 9).roundToInt().toString())

                        } else totalHalfScore?.toString()
                        createWidgetText(
                                totalOut,
                                ContextCompat.getColor(context, R.color.textColorDark)
                                , R.style.bold,
                                widthScoreCell,
                                widthScoreCell
                        )
                    }
                    19 -> {
                        val totalIn = if (typeRow == FAIRWAY_HIT_ROW || typeRow == GIR_ROW) {
                            context.getString(R.string.percent, ((totalEndScore
                                    ?: 0) * 100.0 / 9).roundToInt().toString())
                        } else totalEndScore?.toString()
                        createWidgetText(
                                totalIn,
                                ContextCompat.getColor(context, R.color.textColorDark)
                                , R.style.bold,
                                widthScoreCell,
                                widthScoreCell
                        )
                    }
                    else -> {
                        val itemValue = if (index >= 10) {
                            data[index - 1]
                        } else {
                            data[index]
                        }
                        val cellView = if ((typeRow == FAIRWAY_HIT_ROW || typeRow == GIR_ROW) && itemValue.value != null) {
                            val iconImageType = if (typeRow == FAIRWAY_HIT_ROW) {
                                WozFairwayGreen.TYPE.FAIRWAY
                            } else {
                                WozFairwayGreen.TYPE.GREEN_IN_REGULATION
                            }
                            createWidgetImage(
                                    WozFairwayGreen.getResIdFairway(itemValue.value, iconImageType),
                                    widthScoreCell,
                                    widthScoreCell
                            )
                        } else {
                            createWidgetText(
                                    itemValue.value?.toString(),
                                    ContextCompat.getColor(context, R.color.textColorDark)
                                    , if (typeRow == SCORE_ROW) R.style.bold else R.style.normal,
                                    widthScoreCell,
                                    widthScoreCell
                            )
                        }
                        if (typeRow == SCORE_ROW || typeRow == FAIRWAY_HIT_ROW || typeRow == GIR_ROW) {
                            itemValue.value?.let { score ->
                                itemValue.par?.let { par ->
                                    val backgroundResource = if (typeRow == SCORE_ROW) {
                                        EasyGolfPickerScoreView.getResResource(score, par)
                                    } else {
                                        R.drawable.circle_single_point
                                    }
                                    cellView.setBackgroundResource(backgroundResource)
                                }
                            }
                        }
                        cellView
                    }
                }
                val containerCell = FrameLayout(context)
                containerCell.addView(cellScoreView, widthScoreCell, widthScoreCell)
                val sizeContainerCellScore = widthCell.coerceAtLeast(mSizeUserAvatar)
                rowContainer.addView(containerCell, widthCell, sizeContainerCellScore)

                (containerCell.layoutParams as? MarginLayoutParams)?.let { layoutMargin ->
                    layoutMargin.topMargin = mSmallMargin
                    layoutMargin.bottomMargin = mSmallMargin
                }
                if (isNeedToShowHighlight(typeRow, dataScoreGolfModel?.userId)) {
                    rowContainer.setBackgroundColor(mColorHighlightUserRow)
                }
                (cellScoreView.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.CENTER
                (cellScoreView.layoutParams as? MarginLayoutParams)?.let { layoutMargin ->
                    layoutMargin.marginStart = marginStartEnd
                    layoutMargin.marginEnd = marginStartEnd
                }
                cellScoreView.requestLayout()
            }
            /**
             * create cell end view
             * */
            var sumTotal: Int? = (totalHalfScore ?: 0) + (totalEndScore ?: 0)
            if (sumTotal == 0) {
                sumTotal = null
            }
            val sumTotalResult = if (typeRow == FAIRWAY_HIT_ROW || typeRow == GIR_ROW) {
                sumTotal?.let { temp ->
                    context.getString(R.string.percent, (temp * 100.0 / holes.size).roundToInt().toString())
                }
            } else sumTotal?.toString()

            val titleEndCell = createWidgetTextEndRow(
                    sumTotalResult,
                    widthCell.coerceAtLeast(mSizeUserAvatar) + mSmallMargin * 2,
                    showHighlightColor = if (isNeedToShowHighlight(typeRow, dataScoreGolfModel?.userId)) mColorHighlightUserRow else null
            )

            if ((typeRow == FAIRWAY_HIT_ROW || typeRow == GIR_ROW || typeRow == PUTT_ROW) && titleStartCell != null) {
                dataScoreGolfModel?.userId?.let { userId ->
                    if (!mListHoldExpandView.containsKey(userId)) {
                        mListHoldExpandView[userId] = ArrayList()
                    }
                    rowContainer.visibility = View.GONE
                    titleEndCell.visibility = View.GONE
                    titleStartCell.visibility = View.GONE

                    mListHoldExpandView[userId]?.add(rowContainer)
                    mListHoldExpandView[userId]?.add(titleEndCell)
                    mListHoldExpandView[userId]?.add(titleStartCell)
                }
            }
            callback(rowContainer, titleEndCell)
        }
    }

    private var mTypeView = TypeView.SCORE
    private var mHoldRowScoreList: ArrayList<View> = ArrayList()
    private fun updateScoreToOverElse() {
        mTypeView = if (mTypeView == TypeView.SCORE) {
            TypeView.OVER
        } else {
            TypeView.SCORE
        }
        mHoldRowScoreList.forEach { itemRow ->
            (itemRow.tag as? String)?.let { userId ->
                mListCacheScoreUser[userId]?.let { itemValueList ->
                    if (itemRow is TableRow) {
                        Log.e("WOW", "itemRow.childCount: ${itemRow.childCount}")
                        Log.e("WOW", "itemValueList: ${itemValueList.size}")

                        for (index in 0 until itemRow.childCount) {
                            (itemRow.getChildAt(index) as? ViewGroup)?.let { itemCell ->
                                if (itemCell.childCount > 0) {
                                    itemValueList.getOrNull(index)?.let { itemValue ->
                                        itemValue.value.let { score ->
                                            itemValue.par.let { par ->
                                                (itemCell.getChildAt(0) as? MaterialTextView)?.let { viewTextScore ->
                                                    viewTextScore.text = if (mTypeView == TypeView.SCORE) {
                                                        if (score != null && par != null) {
                                                            if (index != 9 && index != 19) {
                                                                viewTextScore.setBackgroundResource(EasyGolfPickerScoreView.getResResource(score, par))
                                                            } else {
                                                                viewTextScore.setBackgroundResource(0)
                                                            }
                                                        } else {
                                                            viewTextScore.setBackgroundResource(0)
                                                        }
                                                        score?.toString()
                                                                ?: context.getString(R.string.dash)
                                                    } else {
                                                        viewTextScore.setBackgroundResource(0)
                                                        if (score == null) {
                                                            context.getString(R.string.dash)
                                                        } else {
                                                            if (index != 9 && index != 19) {
                                                                val over = (score - (par ?: 0))
                                                                over.toString()
                                                            } else {
                                                                var totalOver = 0
                                                                for (indexTotal in Math.max(0, index - 9) until index) {
                                                                    val scoringTable = itemValueList[indexTotal]
                                                                    totalOver += if (scoringTable.value != null) {
                                                                        scoringTable.value - (scoringTable.par
                                                                                ?: 0)
                                                                    } else 0
                                                                }
                                                                Log.e("WOW", "over : $totalOver")
                                                                if (totalOver == 0) {
                                                                    context.getString(R.string.dash)
                                                                } else {
                                                                    totalOver.toString()
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (itemRow is ViewGroup) {
                            if (itemRow.childCount > 0) {
                                (itemRow.getChildAt(0) as? MaterialTextView)?.let { viewTextScoreTotal ->
                                    viewTextScoreTotal.text = if (mTypeView == TypeView.SCORE) {
                                        if (itemValueList.size < 18) {
                                            itemValueList[itemValueList.size - 1].let { valueItem ->
                                                valueItem.value?.toString()
                                                        ?: context.getString(R.string.dash)
                                            }
                                        } else {
                                            val valueStartTotal = (itemValueList[9].value
                                                    ?: 0) + (itemValueList.last().value ?: 0)
                                            if (valueStartTotal == 0) {
                                                context.getString(R.string.dash)
                                            } else valueStartTotal.toString()
                                        }

                                    } else {
                                        var totalOver = 0
                                        for (indexTotal in 0 until itemValueList.size) {
                                            val scoringTable = itemValueList[indexTotal]
                                            if (indexTotal != 9 && indexTotal != 19) {
                                                totalOver += if (scoringTable.value != null) {
                                                    scoringTable.value - (scoringTable.par ?: 0)
                                                } else 0
                                            }
                                        }
                                        if (totalOver == 0) {
                                            context.getString(R.string.dash)
                                        } else {
                                            totalOver.toString()
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

            }

        }
    }

    private var mScorecardData: Scorecard? = null
    fun initTable(holes: List<Hole>, dataScorecardModel: List<ScorecardModel?>?,
                  userIdDefault: String? = null, scorecard: Scorecard? = null) {
        mScorecardData = scorecard
        mHoldRowScoreList.clear()
        mListCacheScoreUser.clear()
        mCurrentUserId = userIdDefault
        mListHoldExpandView.clear()
        val sizeColumnHeader = context.resources.getDimension(R.dimen.size_avatar_user_scorecard_horizontal_default).toInt() + mSmallMargin * 2
        createWidgetTextStartRow(context.getString(R.string.hole), sizeColumnHeader, showHighlightColor = if (isHighlightRow) mColorHighlightHeaderRow else null)
        val totView = createWidgetTextEndRow(context.getString(R.string.tot), showHighlightColor = if (isHighlightRow) mColorHighlightHeaderRow else null)
        totView.setOnClickListener {
            updateScoreToOverElse()
        }
        mContainerTableMain.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mContainerTableMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val totalCell = if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    10
                } else {
                    holes.size + (holes.size / 9)
                }

                val sizeSingleCell = ((width - sizeColumnHeader - mContainerHeaderEndColumn.measuredWidth) / totalCell)

                mContainerTableMain.removeAllViews()
                mContainerHeaderStartScrollView.removeAllViews()
                mContainerHeaderEndScrollView.removeAllViews()

                mContainerHeaderEndColumn.layoutParams.width = (width - (sizeSingleCell * totalCell) - sizeColumnHeader)
                mContainerHeaderEndColumn.requestLayout()
                val headerHoleRow = buildHeaderRow(holes.size, sizeSingleCell)
                if (isHighlightRow) {
                    headerHoleRow.setBackgroundColor(mColorHighlightHeaderRow)
                }

                mContainerTableMain.addView(headerHoleRow)
                /**
                 * row pars
                 * */
                mTableMainRow = TableRow(context)
                mContainerTableMain.addView(mTableMainRow)

                (mNestScrollVerticalContainerRow.parent as? ViewGroup)?.let {
                    it.removeView(mNestScrollVerticalContainerRow)
                    it.removeAllViews()
                }
                mTableMainRow.addView(mNestScrollVerticalContainerRow)

                val tableData = TableLayout(context)
                (tableData.parent as? ViewGroup)?.let {
                    it.removeView(tableData)
                    it.removeAllViews()
                }
                tableData.layoutTransition = LayoutTransition()
                mNestScrollVerticalContainerRow.removeAllViews()
                mNestScrollVerticalContainerRow.addView(tableData)

                createWidgetTextStartRow(context.getString(R.string.par_no_caps_lock))
                var totalPar = 0
                val listValuePar = ArrayList<Int>()

                holes.forEach { itemHole ->
                    totalPar += itemHole.par
                    listValuePar.add(itemHole.par)
                }

                tableData.addView(buildTotalRow(listValuePar, sizeSingleCell, PAR_ROW))
                createWidgetTextEndRow(totalPar.toString())

                /***
                 * S.I
                 * */
                if (isNeedToExpandView) {
                    createWidgetTextStartRow(context.getString(R.string.stroke_index))
                    var totalSI = 0
                    val listValueStrokeIndex = ArrayList<Int>()

                    holes.forEach { itemHole ->
                        totalSI += itemHole.index
                        listValueStrokeIndex.add(itemHole.index)
                    }

                    tableData.addView(buildTotalRow(listValueStrokeIndex, sizeSingleCell))
                    createWidgetTextEndRow(totalSI.toString())
                }
                /**
                 * Score users
                 * */
                dataScorecardModel?.forEach { scorecardModelUser ->
                    buildTotalScoreRow(
                            holes, scorecardModelUser, sizeSingleCell,
                            SCORE_ROW) { scoreRow, widgetEndText ->
                        tableData.addView(scoreRow)
                        scoreRow.tag = scorecardModelUser?.userId
                        mHoldRowScoreList.add(scoreRow)
                        widgetEndText.tag = scorecardModelUser?.userId
                        mHoldRowScoreList.add(widgetEndText)
                    }

                    /**
                     * expand views
                     * */
                    if (isNeedToExpandView) {
                        buildTotalScoreRow(
                                holes, scorecardModelUser, sizeSingleCell, FAIRWAY_HIT_ROW, context.getString(R.string.fairway)) { expandFairwayView, _ ->
                            tableData.addView(expandFairwayView)
                        }


                        buildTotalScoreRow(
                                holes, scorecardModelUser, sizeSingleCell, GIR_ROW, context.getString(R.string.green_in_regulation_short)) { expandGIRView, _ ->
                            tableData.addView(expandGIRView)
                        }


                        buildTotalScoreRow(
                                holes, scorecardModelUser, sizeSingleCell, PUTT_ROW, context.getString(R.string.putts)) { expandPuttView, _ ->
                            tableData.addView(expandPuttView)
                        }

                    }
                }
            }
        })
    }


    private fun getScoreAtHole(number: Int, dataScoreGolf: List<DataScoreGolf?>?): DataScoreGolf? {
        return dataScoreGolf?.findLast { it?.number == number }
    }

    private fun createWidgetText(title: String?, colorText: Int, style: Int, widthCell: Int? = null,
                                 heightCell: Int? = null): MaterialTextView {
        val headerCell = MaterialTextView(context)
        headerCell.text = title ?: context.getString(R.string.dash)
        headerCell.setTextColor(colorText)
        TextViewCompat.setTextAppearance(headerCell, style)
        headerCell.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.max_limit_small_font_size))
        //TODO when is vertical headerCell.gravity = Gravity.CENTER or Gravity.START
        headerCell.gravity = Gravity.CENTER
        val size = context.resources.getDimension(R.dimen.table_view_cell_view_size_height).toInt()
        headerCell.height = heightCell ?: size
        headerCell.width = widthCell ?: size
        return headerCell
    }

    private fun createWidgetImage(icon: Int, widthCell: Int? = null,
                                  heightCell: Int? = null): ImageView {
        val widgetCell = ImageView(context)
        widgetCell.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.textColorDark))
        widgetCell.setImageResource(icon)
        val size = context.resources.getDimension(R.dimen.table_view_cell_view_size_height).toInt()
        val layoutParams = ViewGroup.LayoutParams(widthCell ?: size, heightCell ?: size)
        widgetCell.layoutParams = layoutParams
        val valuePadding = ((widthCell ?: size) * 0.3).toInt()
        widgetCell.setPadding(valuePadding, valuePadding, valuePadding, valuePadding)
        return widgetCell
    }

    private fun createWidgetTextStartRow(title: String?, widthDefault: Int? = null, heightDefault: Int? = null, showHighlightColor: Int? = null): View {
        val itemChild = createWidgetText(title, ContextCompat.getColor(context, R.color.textColorDark), R.style.normal, widthDefault, heightDefault)
        showHighlightColor?.let { color ->
            itemChild.setBackgroundColor(color)
        }
        if ((mContainerHeaderStartColumn?.childCount ?: 0) > 0) {
            mContainerHeaderStartScrollView.addView(itemChild)
        } else {
            mContainerHeaderStartColumn?.addView(itemChild)
            mContainerHeaderStartColumn?.addView(mNestedScrollVerticalHeaderStartColumn)
        }

        return itemChild
    }

    private fun createWidgetTextEndRow(title: String?, heightDefault: Int? = null, showHighlightColor: Int? = null): ViewGroup {

        val itemChild = createWidgetText(title, ContextCompat.getColor(context, R.color.colorPrimary), R.style.bold)
        val containerParent = FrameLayout(context)
        containerParent.addView(itemChild)
        showHighlightColor?.let { color ->
            containerParent.setBackgroundColor(color)
        }

        (itemChild.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.CENTER
        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, heightDefault
                ?: LayoutParams.WRAP_CONTENT)

        if (mContainerHeaderEndColumn.childCount > 0) {
            mContainerHeaderEndScrollView.addView(containerParent, params)
        } else {
            mContainerHeaderEndColumn.addView(containerParent, params)
            mContainerHeaderEndColumn.addView(mNestedScrollVerticalHeaderEndColumn)
        }

        return containerParent
    }

    private fun createWidgetAvatar(holes: List<Hole>, dataScoreGolfModel: ScorecardModel?, defaultSize: Int, showHighlight: Boolean): ViewGroup {
        val headerCellAvatarUser = EasyGolfAvatarView(context, isNeedToExpandView)
        val sizeAvatar = if (holes.size > 9) {
            mSizeUserAvatar.coerceAtLeast(defaultSize)
        } else mSizeUserAvatar

        val containerAvatar = FrameLayout(context)
        containerAvatar.addView(headerCellAvatarUser, sizeAvatar, sizeAvatar)

        val sizeAvatarParent = mSizeUserAvatar.coerceAtLeast(defaultSize)

        if ((mContainerHeaderStartColumn?.childCount ?: 0) > 0) {
            mContainerHeaderStartScrollView.addView(containerAvatar, LayoutParams.MATCH_PARENT, sizeAvatarParent + mSmallMargin * 2)
        } else {
            mContainerHeaderStartColumn?.addView(containerAvatar, LayoutParams.MATCH_PARENT, sizeAvatarParent + mSmallMargin * 2)
            mContainerHeaderStartColumn?.addView(mNestedScrollVerticalHeaderStartColumn)
        }
        (headerCellAvatarUser.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.CENTER

        if (showHighlight) {
            containerAvatar.setBackgroundColor(mColorHighlightUserRow)
        }
        headerCellAvatarUser.dispatchUserProfileFromFirebase(dataScoreGolfModel?.userId, isNeedToExpandView, mScorecardData)

        return containerAvatar
    }


    private fun expandAction(view: View) {
        view.visibility = View.VISIBLE
    }

    private fun collapseAction(view: View) {
        view.visibility = View.GONE
    }

}