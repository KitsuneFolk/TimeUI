package com.pandacorp.timeui.presentation.utils.countdownview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import com.pandacorp.timeui.R
import kotlin.math.ceil

internal class BackgroundCountdown : BaseCountdown() {
    private var isDrawBg = false
    private var isShowTimeBgDivisionLine = false
    private var mTimeBgDivisionLineColor = 0
    private var mTimeBgDivisionLineSize = 0f
    private var mTimeBgRadius = 0f
    private var mTimeBgSize = 0f
    private var mTimeBgColor = 0
    private var mTimeBgPaint: Paint? = null
    private var mTimeBgBorderPaint: Paint? = null
    private var mTimeBgDivisionLinePaint: Paint? = null
    private var mDefSetTimeBgSize = 0f
    private var mDayTimeBgWidth = 0f
    private var mDayBgRectF: RectF? = null
    private var mHourBgRectF: RectF? = null
    private var mMinuteBgRectF: RectF? = null
    private var mSecondBgRectF: RectF? = null
    private var mMillisecondBgRectF: RectF? = null
    private var mDayBgBorderRectF: RectF? = null
    private var mHourBgBorderRectF: RectF? = null
    private var mMinuteBgBorderRectF: RectF? = null
    private var mSecondBgBorderRectF: RectF? = null
    private var mMillisecondBgBorderRectF: RectF? = null
    private var mTimeBgDivisionLineYPos = 0f
    private var mTimeTextBaseY = 0f
    private var isShowTimeBgBorder = false
    private var mTimeBgBorderSize = 0f
    private var mTimeBgBorderRadius = 0f
    private var mTimeBgBorderColor = 0

    override fun initStyleAttr(context: Context?, ta: TypedArray) {
        super.initStyleAttr(context, ta)
        mTimeBgColor = ta.getColor(R.styleable.CountdownView_timeBgColor, -0xbbbbbc)
        mTimeBgRadius = ta.getDimension(R.styleable.CountdownView_timeBgRadius, 0f)
        isShowTimeBgDivisionLine = ta.getBoolean(R.styleable.CountdownView_isShowTimeBgDivisionLine, true)
        mTimeBgDivisionLineColor =
            ta.getColor(R.styleable.CountdownView_timeBgDivisionLineColor, Color.parseColor("#30FFFFFF"))
        mTimeBgDivisionLineSize = ta.getDimension(
            R.styleable.CountdownView_timeBgDivisionLineSize,
            Utils.dp2px(mContext, DEFAULT_TIME_BG_DIVISION_LINE_SIZE).toFloat()
        )
        mTimeBgSize = ta.getDimension(R.styleable.CountdownView_timeBgSize, 0f)
        mDefSetTimeBgSize = mTimeBgSize
        mTimeBgBorderSize = ta.getDimension(
            R.styleable.CountdownView_timeBgBorderSize,
            Utils.dp2px(mContext, DEFAULT_TIME_BG_BORDER_SIZE).toFloat()
        )
        mTimeBgBorderRadius = ta.getDimension(R.styleable.CountdownView_timeBgBorderRadius, 0f)
        mTimeBgBorderColor = ta.getColor(R.styleable.CountdownView_timeBgBorderColor, -0x1000000)
        isShowTimeBgBorder = ta.getBoolean(R.styleable.CountdownView_isShowTimeBgBorder, false)
        isDrawBg = ta.hasValue(R.styleable.CountdownView_timeBgColor) || !isShowTimeBgBorder
    }

    override fun initPaint() {
        super.initPaint()
        // time background
        mTimeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTimeBgPaint!!.style = Paint.Style.FILL
        mTimeBgPaint!!.color = mTimeBgColor

        // time background border
        if (isShowTimeBgBorder) {
            initTimeBgBorderPaint()
        }

        // time background division line
        if (isShowTimeBgDivisionLine) {
            initTimeTextBgDivisionLinePaint()
        }
    }

    private fun initTimeBgBorderPaint() {
        if (null != mTimeBgBorderPaint) return
        mTimeBgBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTimeBgBorderPaint!!.color = mTimeBgBorderColor
        if (!isDrawBg) {
            mTimeBgBorderPaint!!.strokeWidth = mTimeBgBorderSize
            mTimeBgBorderPaint!!.style = Paint.Style.STROKE
        }
    }

    private fun initTimeTextBgDivisionLinePaint() {
        if (null != mTimeBgDivisionLinePaint) return
        mTimeBgDivisionLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTimeBgDivisionLinePaint!!.color = mTimeBgDivisionLineColor
        mTimeBgDivisionLinePaint!!.strokeWidth = mTimeBgDivisionLineSize
    }

    override fun initTimeTextBaseInfo() {
        super.initTimeTextBaseInfo()
        if (mDefSetTimeBgSize == 0f || mTimeBgSize < mTimeTextWidth) {
            mTimeBgSize = mTimeTextWidth + Utils.dp2px(mContext, 2f) * 4
        }
    }

    /**
     * initialize time initialize rectF
     */
    private fun initTimeBgRect(topPaddingSize: Float) {
        val mHourLeft: Float
        val mMinuteLeft: Float
        val mSecondLeft: Float
        var isInitHasBackgroundTextBaseY = false
        if (isShowDay) {
            // initialize day background and border rectF
            if (isShowTimeBgBorder) {
                mDayBgBorderRectF = RectF(
                    mLeftPaddingSize,
                    topPaddingSize,
                    mLeftPaddingSize + mDayTimeBgWidth + mTimeBgBorderSize * 2,
                    topPaddingSize + mTimeBgSize + mTimeBgBorderSize * 2
                )
                mDayBgRectF = RectF(
                    mLeftPaddingSize + mTimeBgBorderSize,
                    topPaddingSize + mTimeBgBorderSize,
                    mLeftPaddingSize + mDayTimeBgWidth + mTimeBgBorderSize,
                    topPaddingSize + mTimeBgSize + mTimeBgBorderSize
                )
            } else {
                mDayBgRectF = RectF(
                    mLeftPaddingSize,
                    topPaddingSize,
                    mLeftPaddingSize + mDayTimeBgWidth,
                    topPaddingSize + mTimeBgSize
                )
            }
            // hour left point
            mHourLeft =
                mLeftPaddingSize + mDayTimeBgWidth + mSuffixDayTextWidth + mSuffixDayLeftMargin + mSuffixDayRightMargin + mTimeBgBorderSize * 2
            if (!isShowHour && !isShowMinute && !isShowSecond) {
                isInitHasBackgroundTextBaseY = true
                initHasBackgroundTextBaseY(mDayBgRectF!!)
            }
        } else {
            // hour left point
            mHourLeft = mLeftPaddingSize
        }
        if (isShowHour) {
            // initialize hour background border rectF
            if (isShowTimeBgBorder) {
                mHourBgBorderRectF = RectF(
                    mHourLeft,
                    topPaddingSize,
                    mHourLeft + mTimeBgSize + mTimeBgBorderSize * 2,
                    topPaddingSize + mTimeBgSize + mTimeBgBorderSize * 2
                )
                mHourBgRectF = RectF(
                    mHourLeft + mTimeBgBorderSize,
                    topPaddingSize + mTimeBgBorderSize,
                    mHourLeft + mTimeBgSize + mTimeBgBorderSize,
                    topPaddingSize + mTimeBgSize + mTimeBgBorderSize
                )
            } else {
                mHourBgRectF =
                    RectF(mHourLeft, topPaddingSize, mHourLeft + mTimeBgSize, topPaddingSize + mTimeBgSize)
            }
            // minute left point
            mMinuteLeft =
                mHourLeft + mTimeBgSize + mSuffixHourTextWidth + mSuffixHourLeftMargin + mSuffixHourRightMargin + mTimeBgBorderSize * 2
            if (!isInitHasBackgroundTextBaseY) {
                isInitHasBackgroundTextBaseY = true
                initHasBackgroundTextBaseY(mHourBgRectF!!)
            }
        } else {
            // minute left point
            mMinuteLeft = mHourLeft
        }
        if (isShowMinute) {
            // initialize minute background border rectF
            if (isShowTimeBgBorder) {
                mMinuteBgBorderRectF = RectF(
                    mMinuteLeft,
                    topPaddingSize,
                    mMinuteLeft + mTimeBgSize + mTimeBgBorderSize * 2,
                    topPaddingSize + mTimeBgSize + mTimeBgBorderSize * 2
                )
                mMinuteBgRectF = RectF(
                    mMinuteLeft + mTimeBgBorderSize,
                    topPaddingSize + mTimeBgBorderSize,
                    mMinuteLeft + mTimeBgSize + mTimeBgBorderSize,
                    topPaddingSize + mTimeBgSize + mTimeBgBorderSize
                )
            } else {
                mMinuteBgRectF =
                    RectF(mMinuteLeft, topPaddingSize, mMinuteLeft + mTimeBgSize, topPaddingSize + mTimeBgSize)
            }
            // second left point
            mSecondLeft =
                mMinuteLeft + mTimeBgSize + mSuffixMinuteTextWidth + mSuffixMinuteLeftMargin + mSuffixMinuteRightMargin + mTimeBgBorderSize * 2
            if (!isInitHasBackgroundTextBaseY) {
                isInitHasBackgroundTextBaseY = true
                initHasBackgroundTextBaseY(mMinuteBgRectF!!)
            }
        } else {
            // second left point
            mSecondLeft = mMinuteLeft
        }
        if (isShowSecond) {
            // initialize second background border rectF
            if (isShowTimeBgBorder) {
                mSecondBgBorderRectF = RectF(
                    mSecondLeft,
                    topPaddingSize,
                    mSecondLeft + mTimeBgSize + mTimeBgBorderSize * 2,
                    topPaddingSize + mTimeBgSize + mTimeBgBorderSize * 2
                )
                mSecondBgRectF = RectF(
                    mSecondLeft + mTimeBgBorderSize,
                    topPaddingSize + mTimeBgBorderSize,
                    mSecondLeft + mTimeBgSize + mTimeBgBorderSize,
                    topPaddingSize + mTimeBgSize + mTimeBgBorderSize
                )
            } else {
                mSecondBgRectF =
                    RectF(mSecondLeft, topPaddingSize, mSecondLeft + mTimeBgSize, topPaddingSize + mTimeBgSize)
            }
            if (isShowMillisecond) {
                // millisecond left point
                val mMillisecondLeft =
                    mSecondLeft + mTimeBgSize + mSuffixSecondTextWidth + mSuffixSecondLeftMargin + mSuffixSecondRightMargin + mTimeBgBorderSize * 2

                // initialize millisecond background border rectF
                if (isShowTimeBgBorder) {
                    mMillisecondBgBorderRectF = RectF(
                        mMillisecondLeft,
                        topPaddingSize,
                        mMillisecondLeft + mTimeBgSize + mTimeBgBorderSize * 2,
                        topPaddingSize + mTimeBgSize + mTimeBgBorderSize * 2
                    )
                    mMillisecondBgRectF = RectF(
                        mMillisecondLeft + mTimeBgBorderSize,
                        topPaddingSize + mTimeBgBorderSize,
                        mMillisecondLeft + mTimeBgSize + mTimeBgBorderSize,
                        topPaddingSize + mTimeBgSize + mTimeBgBorderSize
                    )
                } else {
                    mMillisecondBgRectF = RectF(
                        mMillisecondLeft,
                        topPaddingSize,
                        mMillisecondLeft + mTimeBgSize,
                        topPaddingSize + mTimeBgSize
                    )
                }
            }
            if (!isInitHasBackgroundTextBaseY) {
                initHasBackgroundTextBaseY(mSecondBgRectF!!)
            }
        }
    }

    private fun getSuffixTextBaseLine(suffixText: String, topPaddingSize: Float): Float {
        val tempRect = Rect()
        mSuffixTextPaint!!.getTextBounds(suffixText, 0, suffixText.length, tempRect)
        val ret: Float = when (mSuffixGravity) {
            0 ->                 // top
                topPaddingSize - tempRect.top

            1 ->                 // center
                topPaddingSize + mTimeBgSize - mTimeBgSize / 2 + tempRect.height() / 2 + mTimeBgBorderSize

            2 ->                 // bottom
                topPaddingSize + mTimeBgSize - tempRect.bottom + mTimeBgBorderSize * 2

            else -> topPaddingSize + mTimeBgSize - mTimeBgSize / 2 + tempRect.height() / 2 + mTimeBgBorderSize
        }
        return ret
    }

    private fun initHasBackgroundTextBaseY(rectF: RectF) {
        // time text baseline
        val timeFontMetrics = mTimeTextPaint!!.fontMetrics
        mTimeTextBaseY =
            rectF.top + (rectF.bottom - rectF.top - timeFontMetrics.bottom + timeFontMetrics.top) / 2 - timeFontMetrics.top - mTimeTextBottom
        // initialize background division line y point
        mTimeBgDivisionLineYPos = rectF.centerY() + if (mTimeBgDivisionLineSize == Utils.dp2px(
                mContext,
                DEFAULT_TIME_BG_DIVISION_LINE_SIZE
            ).toFloat()
        ) mTimeBgDivisionLineSize else mTimeBgDivisionLineSize / 2
    }

    /**
     * initialize time text baseline
     * and
     * time background top padding
     */
    private fun initTimeTextBaselineAndTimeBgTopPadding(
        viewHeight: Int,
        viewPaddingTop: Int,
        viewPaddingBottom: Int,
        contentAllHeight: Int
    ): Float {
        val topPaddingSize: Float = if (viewPaddingTop == viewPaddingBottom) {
            // center
            ((viewHeight - contentAllHeight) / 2).toFloat()
        } else {
            // padding top
            viewPaddingTop.toFloat()
        }
        if (isShowDay && mSuffixDayTextWidth > 0) {
            mSuffixDayTextBaseline = getSuffixTextBaseLine(mSuffixDay!!, topPaddingSize)
        }
        if (isShowHour && mSuffixHourTextWidth > 0) {
            mSuffixHourTextBaseline = getSuffixTextBaseLine(mSuffixHour!!, topPaddingSize)
        }
        if (isShowMinute && mSuffixMinuteTextWidth > 0) {
            mSuffixMinuteTextBaseline = getSuffixTextBaseLine(mSuffixMinute!!, topPaddingSize)
        }
        if (mSuffixSecondTextWidth > 0) {
            mSuffixSecondTextBaseline = getSuffixTextBaseLine(mSuffixSecond!!, topPaddingSize)
        }
        if (isShowMillisecond && mSuffixMillisecondTextWidth > 0) {
            mSuffixMillisecondTextBaseline = getSuffixTextBaseLine(mSuffixMillisecond!!, topPaddingSize)
        }
        return topPaddingSize
    }

    override val allContentWidth: Int
        get() {
            var width = getAllContentWidthBase(mTimeBgSize + mTimeBgBorderSize * 2)
            if (isShowDay) {
                if (isDayLargeNinetyNine) {
                    val rect = Rect()
                    val tempDay = mDay.toString()
                    mTimeTextPaint!!.getTextBounds(tempDay, 0, tempDay.length, rect)
                    mDayTimeBgWidth = (rect.width() + Utils.dp2px(mContext, 2f) * 4).toFloat()
                    width += mDayTimeBgWidth
                } else {
                    mDayTimeBgWidth = mTimeBgSize
                    width += mTimeBgSize
                }
                width += mTimeBgBorderSize * 2
            }
            return ceil(width.toDouble()).toInt()
        }
    override val allContentHeight: Int
        get() = (mTimeBgSize + mTimeBgBorderSize * 2).toInt()

    override fun onMeasure(
        v: View,
        viewWidth: Int,
        viewHeight: Int,
        allContentWidth: Int,
        allContentHeight: Int
    ) {
        val retTopPaddingSize =
            initTimeTextBaselineAndTimeBgTopPadding(viewHeight, v.paddingTop, v.paddingBottom, allContentHeight)
        mLeftPaddingSize =
            (if (v.paddingLeft == v.paddingRight) (viewWidth - allContentWidth) / 2 else v.paddingLeft).toFloat()
        initTimeBgRect(retTopPaddingSize)
    }

    override fun onDraw(canvas: Canvas) {
        val mHourLeft: Float = if (isShowDay) {
            // draw day background border
            if (isShowTimeBgBorder) {
                canvas.drawRoundRect(
                    mDayBgBorderRectF!!,
                    mTimeBgBorderRadius,
                    mTimeBgBorderRadius,
                    mTimeBgBorderPaint!!
                )
            }
            if (isDrawBg) {
                // draw day background
                canvas.drawRoundRect(mDayBgRectF!!, mTimeBgRadius, mTimeBgRadius, mTimeBgPaint!!)
                if (isShowTimeBgDivisionLine) {
                    // draw day background division line
                    canvas.drawLine(
                        mLeftPaddingSize + mTimeBgBorderSize,
                        mTimeBgDivisionLineYPos,
                        mLeftPaddingSize + mDayTimeBgWidth + mTimeBgBorderSize,
                        mTimeBgDivisionLineYPos,
                        mTimeBgDivisionLinePaint!!
                    )
                }
            }
            // draw day text
            canvas.drawText(
                Utils.formatNum(mDay),
                mDayBgRectF!!.centerX(),
                mTimeTextBaseY,
                mTimeTextPaint!!
            )
            if (mSuffixDayTextWidth > 0) {
                // draw day suffix
                canvas.drawText(
                    mSuffixDay!!,
                    mLeftPaddingSize + mDayTimeBgWidth + mSuffixDayLeftMargin + mTimeBgBorderSize * 2,
                    mSuffixDayTextBaseline,
                    mSuffixTextPaint!!
                )
            }

            // hour left point
            mLeftPaddingSize + mDayTimeBgWidth + mSuffixDayTextWidth + mSuffixDayLeftMargin + mSuffixDayRightMargin + mTimeBgBorderSize * 2
        } else {
            // hour left point
            mLeftPaddingSize
        }
        val mMinuteLeft: Float = if (isShowHour) {
            // draw hour background border
            if (isShowTimeBgBorder) {
                canvas.drawRoundRect(
                    mHourBgBorderRectF!!,
                    mTimeBgBorderRadius,
                    mTimeBgBorderRadius,
                    mTimeBgBorderPaint!!
                )
            }
            if (isDrawBg) {
                // draw hour background
                canvas.drawRoundRect(mHourBgRectF!!, mTimeBgRadius, mTimeBgRadius, mTimeBgPaint!!)
                if (isShowTimeBgDivisionLine) {
                    // draw hour background division line
                    canvas.drawLine(
                        mHourLeft + mTimeBgBorderSize,
                        mTimeBgDivisionLineYPos,
                        mTimeBgSize + mHourLeft + mTimeBgBorderSize,
                        mTimeBgDivisionLineYPos,
                        mTimeBgDivisionLinePaint!!
                    )
                }
            }
            // draw hour text
            canvas.drawText(
                Utils.formatNum(mHour),
                mHourBgRectF!!.centerX(),
                mTimeTextBaseY,
                mTimeTextPaint!!
            )
            if (mSuffixHourTextWidth > 0) {
                // draw hour suffix
                canvas.drawText(
                    mSuffixHour!!,
                    mHourLeft + mTimeBgSize + mSuffixHourLeftMargin + mTimeBgBorderSize * 2,
                    mSuffixHourTextBaseline,
                    mSuffixTextPaint!!
                )
            }

            // minute left point
            mHourLeft + mTimeBgSize + mSuffixHourTextWidth + mSuffixHourLeftMargin + mSuffixHourRightMargin + mTimeBgBorderSize * 2
        } else {
            // minute left point
            mHourLeft
        }
        val mSecondLeft: Float = if (isShowMinute) {
            // draw minute background border
            if (isShowTimeBgBorder) {
                canvas.drawRoundRect(
                    mMinuteBgBorderRectF!!,
                    mTimeBgBorderRadius,
                    mTimeBgBorderRadius,
                    mTimeBgBorderPaint!!
                )
            }
            if (isDrawBg) {
                // draw minute background
                canvas.drawRoundRect(mMinuteBgRectF!!, mTimeBgRadius, mTimeBgRadius, mTimeBgPaint!!)
                if (isShowTimeBgDivisionLine) {
                    // draw minute background division line
                    canvas.drawLine(
                        mMinuteLeft + mTimeBgBorderSize,
                        mTimeBgDivisionLineYPos,
                        mTimeBgSize + mMinuteLeft + mTimeBgBorderSize,
                        mTimeBgDivisionLineYPos,
                        mTimeBgDivisionLinePaint!!
                    )
                }
            }
            // draw minute text
            canvas.drawText(
                Utils.formatNum(mMinute),
                mMinuteBgRectF!!.centerX(),
                mTimeTextBaseY,
                mTimeTextPaint!!
            )
            if (mSuffixMinuteTextWidth > 0) {
                // draw minute suffix
                canvas.drawText(
                    mSuffixMinute!!,
                    mMinuteLeft + mTimeBgSize + mSuffixMinuteLeftMargin + mTimeBgBorderSize * 2,
                    mSuffixMinuteTextBaseline,
                    mSuffixTextPaint!!
                )
            }

            // second left point
            mMinuteLeft + mTimeBgSize + mSuffixMinuteTextWidth + mSuffixMinuteLeftMargin + mSuffixMinuteRightMargin + mTimeBgBorderSize * 2
        } else {
            // second left point
            mMinuteLeft
        }
        if (isShowSecond) {
            // draw second background border
            if (isShowTimeBgBorder) {
                canvas.drawRoundRect(
                    mSecondBgBorderRectF!!,
                    mTimeBgBorderRadius,
                    mTimeBgBorderRadius,
                    mTimeBgBorderPaint!!
                )
            }
            if (isDrawBg) {
                // draw second background
                canvas.drawRoundRect(mSecondBgRectF!!, mTimeBgRadius, mTimeBgRadius, mTimeBgPaint!!)
                if (isShowTimeBgDivisionLine) {
                    // draw second background division line
                    canvas.drawLine(
                        mSecondLeft + mTimeBgBorderSize,
                        mTimeBgDivisionLineYPos,
                        mTimeBgSize + mSecondLeft + mTimeBgBorderSize,
                        mTimeBgDivisionLineYPos,
                        mTimeBgDivisionLinePaint!!
                    )
                }
            }
            // draw second text
            canvas.drawText(
                Utils.formatNum(mSecond),
                mSecondBgRectF!!.centerX(),
                mTimeTextBaseY,
                mTimeTextPaint!!
            )
            if (mSuffixSecondTextWidth > 0) {
                // draw second suffix
                canvas.drawText(
                    mSuffixSecond!!,
                    mSecondLeft + mTimeBgSize + mSuffixSecondLeftMargin + mTimeBgBorderSize * 2,
                    mSuffixSecondTextBaseline,
                    mSuffixTextPaint!!
                )
            }
            if (isShowMillisecond) {
                // draw millisecond background border
                if (isShowTimeBgBorder) {
                    canvas.drawRoundRect(
                        mMillisecondBgBorderRectF!!,
                        mTimeBgBorderRadius,
                        mTimeBgBorderRadius,
                        mTimeBgBorderPaint!!
                    )
                }
                // millisecond left point
                val mMillisecondLeft =
                    mSecondLeft + mTimeBgSize + mSuffixSecondTextWidth + mSuffixSecondLeftMargin + mSuffixSecondRightMargin + mTimeBgBorderSize * 2
                if (isDrawBg) {
                    // draw millisecond background
                    canvas.drawRoundRect(mMillisecondBgRectF!!, mTimeBgRadius, mTimeBgRadius, mTimeBgPaint!!)
                    if (isShowTimeBgDivisionLine) {
                        // draw millisecond background division line
                        canvas.drawLine(
                            mMillisecondLeft + mTimeBgBorderSize,
                            mTimeBgDivisionLineYPos,
                            mTimeBgSize + mMillisecondLeft + mTimeBgBorderSize,
                            mTimeBgDivisionLineYPos,
                            mTimeBgDivisionLinePaint!!
                        )
                    }
                }
                // draw millisecond text
                canvas.drawText(
                    Utils.formatMillisecond(mMillisecond),
                    mMillisecondBgRectF!!.centerX(),
                    mTimeTextBaseY,
                    mTimeTextPaint!!
                )
                if (mSuffixMillisecondTextWidth > 0) {
                    // draw millisecond suffix
                    canvas.drawText(
                        mSuffixMillisecond!!,
                        mMillisecondLeft + mTimeBgSize + mSuffixMillisecondLeftMargin + mTimeBgBorderSize * 2,
                        mSuffixMillisecondTextBaseline,
                        mSuffixTextPaint!!
                    )
                }
            }
        }
    }

    fun setTimeBgSize(size: Float) {
        mTimeBgSize = Utils.dp2px(mContext, size).toFloat()
    }

    fun setTimeBgColor(color: Int) {
        mTimeBgColor = color
        mTimeBgPaint!!.color = mTimeBgColor
        if (color == Color.TRANSPARENT && isShowTimeBgBorder) {
            isDrawBg = false
            mTimeBgBorderPaint!!.strokeWidth = mTimeBgBorderSize
            mTimeBgBorderPaint!!.style = Paint.Style.STROKE
        } else {
            isDrawBg = true
            if (isShowTimeBgBorder) {
                mTimeBgBorderPaint!!.strokeWidth = 0f
                mTimeBgBorderPaint!!.style = Paint.Style.FILL
            }
        }
    }

    fun setTimeBgRadius(radius: Float) {
        mTimeBgRadius = Utils.dp2px(mContext, radius).toFloat()
    }

    fun setIsShowTimeBgDivisionLine(isShow: Boolean) {
        isShowTimeBgDivisionLine = isShow
        if (isShowTimeBgDivisionLine) {
            initTimeTextBgDivisionLinePaint()
        } else {
            mTimeBgDivisionLinePaint = null
        }
    }

    fun setTimeBgDivisionLineColor(color: Int) {
        mTimeBgDivisionLineColor = color
        if (null != mTimeBgDivisionLinePaint) {
            mTimeBgDivisionLinePaint!!.color = mTimeBgDivisionLineColor
        }
    }

    fun setTimeBgDivisionLineSize(size: Float) {
        mTimeBgDivisionLineSize = Utils.dp2px(mContext, size).toFloat()
        if (null != mTimeBgDivisionLinePaint) {
            mTimeBgDivisionLinePaint!!.strokeWidth = mTimeBgDivisionLineSize
        }
    }

    fun setIsShowTimeBgBorder(isShow: Boolean) {
        isShowTimeBgBorder = isShow
        if (isShowTimeBgBorder) {
            initTimeBgBorderPaint()
        } else {
            mTimeBgBorderPaint = null
            mTimeBgBorderSize = 0f
        }
    }

    fun setTimeBgBorderColor(color: Int) {
        mTimeBgBorderColor = color
        if (null != mTimeBgBorderPaint) {
            mTimeBgBorderPaint!!.color = mTimeBgBorderColor
        }
    }

    fun setTimeBgBorderSize(size: Float) {
        mTimeBgBorderSize = Utils.dp2px(mContext, size).toFloat()
        if (null != mTimeBgBorderPaint && !isDrawBg) {
            mTimeBgBorderPaint!!.strokeWidth = mTimeBgBorderSize
            mTimeBgBorderPaint!!.style = Paint.Style.STROKE
        }
    }

    fun setTimeBgBorderRadius(size: Float) {
        mTimeBgBorderRadius = Utils.dp2px(mContext, size).toFloat()
    }

    companion object {
        private const val DEFAULT_TIME_BG_DIVISION_LINE_SIZE = 0.5f // dp
        private const val DEFAULT_TIME_BG_BORDER_SIZE = 1f // dp
    }
}