package com.pandacorp.timeui.presentation.utils.countdownview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.view.View
import com.pandacorp.timeui.R
import kotlin.math.ceil

internal open class BaseCountdown {
    @JvmField
    var mDay = 0

    @JvmField
    var mHour = 0

    @JvmField
    var mMinute = 0

    @JvmField
    var mSecond = 0

    @JvmField
    var mMillisecond = 0

    @JvmField
    var isShowDay = false

    @JvmField
    var isShowHour = false

    @JvmField
    var isShowMinute = false

    @JvmField
    var isShowSecond = false

    @JvmField
    var isShowMillisecond = false
    var isConvertDaysToHours = false
    var mHasSetIsShowDay = false
    var mHasSetIsShowHour = false

    @JvmField
    protected var mContext: Context? = null
    private var mSuffix: String? = null

    @JvmField
    protected var mSuffixDay: String? = null

    @JvmField
    protected var mSuffixHour: String? = null

    @JvmField
    protected var mSuffixMinute: String? = null

    @JvmField
    protected var mSuffixSecond: String? = null

    @JvmField
    protected var mSuffixMillisecond: String? = null

    @JvmField
    protected var mSuffixDayTextWidth = 0f

    @JvmField
    protected var mSuffixHourTextWidth = 0f

    @JvmField
    protected var mSuffixMinuteTextWidth = 0f

    @JvmField
    protected var mSuffixSecondTextWidth = 0f

    @JvmField
    protected var mSuffixMillisecondTextWidth = 0f

    @JvmField
    protected var isDayLargeNinetyNine = false

    @JvmField
    protected var mTimeTextPaint: Paint? = null

    @JvmField
    protected var mSuffixTextPaint: Paint? = null
    private var mMeasureHourWidthPaint: Paint? = null

    @JvmField
    protected var mLeftPaddingSize = 0f

    @JvmField
    protected var mSuffixDayLeftMargin = 0f

    @JvmField
    protected var mSuffixDayRightMargin = 0f

    @JvmField
    protected var mSuffixSecondLeftMargin = 0f

    @JvmField
    protected var mSuffixSecondRightMargin = 0f

    @JvmField
    protected var mSuffixHourLeftMargin = 0f

    @JvmField
    protected var mSuffixHourRightMargin = 0f

    @JvmField
    protected var mSuffixMinuteLeftMargin = 0f

    @JvmField
    protected var mSuffixMinuteRightMargin = 0f

    @JvmField
    protected var mSuffixMillisecondLeftMargin = 0f

    @JvmField
    protected var mSuffixDayTextBaseline = 0f

    @JvmField
    protected var mSuffixHourTextBaseline = 0f

    @JvmField
    protected var mSuffixMinuteTextBaseline = 0f

    @JvmField
    protected var mSuffixSecondTextBaseline = 0f

    @JvmField
    protected var mSuffixMillisecondTextBaseline = 0f

    @JvmField
    protected var mTimeTextWidth = 0f
    private var mTimeTextHeight = 0f

    @JvmField
    protected var mTimeTextBottom = 0f

    @JvmField
    protected var mSuffixGravity = 0
    private var hasSetSuffixDay = false
    private var hasSetSuffixHour = false
    private var hasSetSuffixMinute = false
    private var hasSetSuffixSecond = false
    private var hasSetSuffixMillisecond = false
    private var hasCustomSomeSuffix = false
    private var mSuffixLRMargin = 0f
    private var mTimeTextColor = 0
    private var mTimeTextSize = 0f
    private var isTimeTextBold = false
    private var mSuffixTextColor = 0
    private var mSuffixTextSize = 0f
    private var isSuffixTextBold = false
    private var mDayTimeTextWidth = 0f
    private var mHourTimeTextWidth = 0f
    private var mTimeTextBaseline = 0f
    private var mTempSuffixDayLeftMargin = 0f
    private var mTempSuffixDayRightMargin = 0f
    private var mTempSuffixHourLeftMargin = 0f
    private var mTempSuffixHourRightMargin = 0f
    private var mTempSuffixMinuteLeftMargin = 0f
    private var mTempSuffixMinuteRightMargin = 0f
    private var mTempSuffixSecondLeftMargin = 0f
    private var mTempSuffixSecondRightMargin = 0f
    private var mTempSuffixMillisecondLeftMargin = 0f
    private var mTempSuffixMinute: String? = null
    private var mTempSuffixSecond: String? = null
    open fun initStyleAttr(context: Context?, ta: TypedArray) {
        mContext = context
        isTimeTextBold = ta.getBoolean(R.styleable.CountdownView_isTimeTextBold, false)
        mTimeTextSize = ta.getDimension(R.styleable.CountdownView_timeTextSize, Utils.sp2px(mContext, 12f))
        mTimeTextColor = ta.getColor(R.styleable.CountdownView_timeTextColor, -0x1000000)
        isShowDay = ta.getBoolean(R.styleable.CountdownView_isShowDay, false)
        isShowHour = ta.getBoolean(R.styleable.CountdownView_isShowHour, false)
        isShowMinute = ta.getBoolean(R.styleable.CountdownView_isShowMinute, true)
        isShowSecond = ta.getBoolean(R.styleable.CountdownView_isShowSecond, true)
        isShowMillisecond = ta.getBoolean(R.styleable.CountdownView_isShowMillisecond, false)
        if (ta.getBoolean(R.styleable.CountdownView_isHideTimeBackground, true)) {
            isConvertDaysToHours = ta.getBoolean(R.styleable.CountdownView_isConvertDaysToHours, false)
        }
        isSuffixTextBold = ta.getBoolean(R.styleable.CountdownView_isSuffixTextBold, false)
        mSuffixTextSize = ta.getDimension(R.styleable.CountdownView_suffixTextSize, Utils.sp2px(mContext, 12f))
        mSuffixTextColor = ta.getColor(R.styleable.CountdownView_suffixTextColor, -0x1000000)
        mSuffix = ta.getString(R.styleable.CountdownView_suffix)
        mSuffixDay = ta.getString(R.styleable.CountdownView_suffixDay)
        mSuffixHour = ta.getString(R.styleable.CountdownView_suffixHour)
        mSuffixMinute = ta.getString(R.styleable.CountdownView_suffixMinute)
        mSuffixSecond = ta.getString(R.styleable.CountdownView_suffixSecond)
        mSuffixMillisecond = ta.getString(R.styleable.CountdownView_suffixMillisecond)
        mSuffixGravity = ta.getInt(R.styleable.CountdownView_suffixGravity, 1)
        mSuffixLRMargin = ta.getDimension(R.styleable.CountdownView_suffixLRMargin, -1f)
        mSuffixDayLeftMargin = ta.getDimension(R.styleable.CountdownView_suffixDayLeftMargin, -1f)
        mSuffixDayRightMargin = ta.getDimension(R.styleable.CountdownView_suffixDayRightMargin, -1f)
        mSuffixHourLeftMargin = ta.getDimension(R.styleable.CountdownView_suffixHourLeftMargin, -1f)
        mSuffixHourRightMargin = ta.getDimension(R.styleable.CountdownView_suffixHourRightMargin, -1f)
        mSuffixMinuteLeftMargin = ta.getDimension(R.styleable.CountdownView_suffixMinuteLeftMargin, -1f)
        mSuffixMinuteRightMargin = ta.getDimension(R.styleable.CountdownView_suffixMinuteRightMargin, -1f)
        mSuffixSecondLeftMargin = ta.getDimension(R.styleable.CountdownView_suffixSecondLeftMargin, -1f)
        mSuffixSecondRightMargin = ta.getDimension(R.styleable.CountdownView_suffixSecondRightMargin, -1f)
        mSuffixMillisecondLeftMargin =
            ta.getDimension(R.styleable.CountdownView_suffixMillisecondLeftMargin, -1f)
        mHasSetIsShowDay = ta.hasValue(R.styleable.CountdownView_isShowDay)
        mHasSetIsShowHour = ta.hasValue(R.styleable.CountdownView_isShowHour)
        initTempSuffixMargin()

        // time validate
        if (!isShowDay && !isShowHour && !isShowMinute) isShowSecond = true
        if (!isShowSecond) isShowMillisecond = false
    }

    fun initialize() {
        initSuffixBase()

        // initialize
        initPaint()
        initSuffix()

        // regular time data
        // pick one of two (minute and second)
//        if (!isShowMinute && !isShowSecond) isShowSecond = true;
        if (!isShowSecond) isShowMillisecond = false
        initTimeTextBaseInfo()
    }

    private fun initSuffixBase() {
        hasSetSuffixDay = !TextUtils.isEmpty(mSuffixDay)
        hasSetSuffixHour = !TextUtils.isEmpty(mSuffixHour)
        hasSetSuffixMinute = !TextUtils.isEmpty(mSuffixMinute)
        hasSetSuffixSecond = !TextUtils.isEmpty(mSuffixSecond)
        hasSetSuffixMillisecond = !TextUtils.isEmpty(mSuffixMillisecond)
        if (isShowDay && hasSetSuffixDay || isShowHour && hasSetSuffixHour || isShowMinute && hasSetSuffixMinute || isShowSecond && hasSetSuffixSecond || isShowMillisecond && hasSetSuffixMillisecond) {
            hasCustomSomeSuffix = true
        }
        mTempSuffixMinute = mSuffixMinute
        mTempSuffixSecond = mSuffixSecond
    }

    private fun initTempSuffixMargin() {
        // temporarily saved suffix left and right margins
        mTempSuffixDayLeftMargin = mSuffixDayLeftMargin
        mTempSuffixDayRightMargin = mSuffixDayRightMargin
        mTempSuffixHourLeftMargin = mSuffixHourLeftMargin
        mTempSuffixHourRightMargin = mSuffixHourRightMargin
        mTempSuffixMinuteLeftMargin = mSuffixMinuteLeftMargin
        mTempSuffixMinuteRightMargin = mSuffixMinuteRightMargin
        mTempSuffixSecondLeftMargin = mSuffixSecondLeftMargin
        mTempSuffixSecondRightMargin = mSuffixSecondRightMargin
        mTempSuffixMillisecondLeftMargin = mSuffixMillisecondLeftMargin
    }

    /**
     * initialize Paint
     */
    protected open fun initPaint() {
        // time text
        mTimeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTimeTextPaint!!.color = mTimeTextColor
        mTimeTextPaint!!.textAlign = Paint.Align.CENTER
        mTimeTextPaint!!.textSize = mTimeTextSize
        if (isTimeTextBold) {
            mTimeTextPaint!!.isFakeBoldText = true
        }

        // suffix text
        mSuffixTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSuffixTextPaint!!.color = mSuffixTextColor
        mSuffixTextPaint!!.textSize = mSuffixTextSize
        if (isSuffixTextBold) {
            mSuffixTextPaint!!.isFakeBoldText = true
        }
        mMeasureHourWidthPaint = Paint()
        mMeasureHourWidthPaint!!.textSize = mTimeTextSize
        if (isTimeTextBold) {
            mMeasureHourWidthPaint!!.isFakeBoldText = true
        }
    }

    private fun initSuffix() {
        var isSuffixNull = true
        var mSuffixTextWidth = 0f
        val mDefSuffixTextWidth = mSuffixTextPaint!!.measureText(DEFAULT_SUFFIX)
        if (!TextUtils.isEmpty(mSuffix)) {
            isSuffixNull = false
            mSuffixTextWidth = mSuffixTextPaint!!.measureText(mSuffix)
        }
        if (isShowDay) {
            if (hasSetSuffixDay) {
                mSuffixDayTextWidth = mSuffixTextPaint!!.measureText(mSuffixDay)
            } else {
                if (!isSuffixNull) {
                    mSuffixDay = mSuffix
                    mSuffixDayTextWidth = mSuffixTextWidth
                } else if (!hasCustomSomeSuffix) {
                    mSuffixDay = DEFAULT_SUFFIX
                    mSuffixDayTextWidth = mDefSuffixTextWidth
                }
            }
        } else {
            mSuffixDayTextWidth = 0f
        }
        if (isShowHour) {
            if (hasSetSuffixHour) {
                mSuffixHourTextWidth = mSuffixTextPaint!!.measureText(mSuffixHour)
            } else {
                if (!isSuffixNull) {
                    mSuffixHour = mSuffix
                    mSuffixHourTextWidth = mSuffixTextWidth
                } else if (!hasCustomSomeSuffix) {
                    mSuffixHour = DEFAULT_SUFFIX
                    mSuffixHourTextWidth = mDefSuffixTextWidth
                }
            }
        } else {
            mSuffixHourTextWidth = 0f
        }
        if (isShowMinute) {
            if (hasSetSuffixMinute) {
                mSuffixMinuteTextWidth = mSuffixTextPaint!!.measureText(mSuffixMinute)
            } else if (isShowSecond) {
                if (!isSuffixNull) {
                    mSuffixMinute = mSuffix
                    mSuffixMinuteTextWidth = mSuffixTextWidth
                } else if (!hasCustomSomeSuffix) {
                    mSuffixMinute = DEFAULT_SUFFIX
                    mSuffixMinuteTextWidth = mDefSuffixTextWidth
                }
            } else {
                mSuffixMinuteTextWidth = 0f
            }
        } else {
            mSuffixMinuteTextWidth = 0f
        }
        if (isShowSecond) {
            if (hasSetSuffixSecond) {
                mSuffixSecondTextWidth = mSuffixTextPaint!!.measureText(mSuffixSecond)
            } else if (isShowMillisecond) {
                if (!isSuffixNull) {
                    mSuffixSecond = mSuffix
                    mSuffixSecondTextWidth = mSuffixTextWidth
                } else if (!hasCustomSomeSuffix) {
                    mSuffixSecond = DEFAULT_SUFFIX
                    mSuffixSecondTextWidth = mDefSuffixTextWidth
                }
            } else {
                mSuffixSecondTextWidth = 0f
            }
        } else {
            mSuffixSecondTextWidth = 0f
        }
        mSuffixMillisecondTextWidth = if (isShowMillisecond && hasCustomSomeSuffix && hasSetSuffixMillisecond) {
            mSuffixTextPaint!!.measureText(mSuffixMillisecond)
        } else {
            0f
        }
        initSuffixMargin()
    }

    /**
     * initialize suffix margin
     */
    private fun initSuffixMargin() {
        val defSuffixLRMargin = Utils.dp2px(mContext, DEFAULT_SUFFIX_LR_MARGIN)
        var isSuffixLRMarginNull = true
        if (mSuffixLRMargin >= 0) {
            isSuffixLRMarginNull = false
        }
        if (isShowDay && mSuffixDayTextWidth > 0) {
            if (mSuffixDayLeftMargin < 0) {
                mSuffixDayLeftMargin = if (!isSuffixLRMarginNull) {
                    mSuffixLRMargin
                } else {
                    defSuffixLRMargin.toFloat()
                }
            }
            if (mSuffixDayRightMargin < 0) {
                mSuffixDayRightMargin = if (!isSuffixLRMarginNull) {
                    mSuffixLRMargin
                } else {
                    defSuffixLRMargin.toFloat()
                }
            }
        } else {
            mSuffixDayLeftMargin = 0f
            mSuffixDayRightMargin = 0f
        }
        if (isShowHour && mSuffixHourTextWidth > 0) {
            if (mSuffixHourLeftMargin < 0) {
                mSuffixHourLeftMargin = if (!isSuffixLRMarginNull) {
                    mSuffixLRMargin
                } else {
                    defSuffixLRMargin.toFloat()
                }
            }
            if (mSuffixHourRightMargin < 0) {
                mSuffixHourRightMargin = if (!isSuffixLRMarginNull) {
                    mSuffixLRMargin
                } else {
                    defSuffixLRMargin.toFloat()
                }
            }
        } else {
            mSuffixHourLeftMargin = 0f
            mSuffixHourRightMargin = 0f
        }
        if (isShowMinute && mSuffixMinuteTextWidth > 0) {
            if (mSuffixMinuteLeftMargin < 0) {
                mSuffixMinuteLeftMargin = if (!isSuffixLRMarginNull) {
                    mSuffixLRMargin
                } else {
                    defSuffixLRMargin.toFloat()
                }
            }
            if (isShowSecond) {
                if (mSuffixMinuteRightMargin < 0) {
                    mSuffixMinuteRightMargin = if (!isSuffixLRMarginNull) {
                        mSuffixLRMargin
                    } else {
                        defSuffixLRMargin.toFloat()
                    }
                }
            } else {
                mSuffixMinuteRightMargin = 0f
            }
        } else {
            mSuffixMinuteLeftMargin = 0f
            mSuffixMinuteRightMargin = 0f
        }
        if (isShowSecond) {
            if (mSuffixSecondTextWidth > 0) {
                if (mSuffixSecondLeftMargin < 0) {
                    mSuffixSecondLeftMargin = if (!isSuffixLRMarginNull) {
                        mSuffixLRMargin
                    } else {
                        defSuffixLRMargin.toFloat()
                    }
                }
                if (isShowMillisecond) {
                    if (mSuffixSecondRightMargin < 0) {
                        mSuffixSecondRightMargin = if (!isSuffixLRMarginNull) {
                            mSuffixLRMargin
                        } else {
                            defSuffixLRMargin.toFloat()
                        }
                    }
                } else {
                    mSuffixSecondRightMargin = 0f
                }
            } else {
                mSuffixSecondLeftMargin = 0f
                mSuffixSecondRightMargin = 0f
            }
            if (isShowMillisecond && mSuffixMillisecondTextWidth > 0) {
                if (mSuffixMillisecondLeftMargin < 0) {
                    mSuffixMillisecondLeftMargin = if (!isSuffixLRMarginNull) {
                        mSuffixLRMargin
                    } else {
                        defSuffixLRMargin.toFloat()
                    }
                }
            } else {
                mSuffixMillisecondLeftMargin = 0f
            }
        } else {
            mSuffixSecondLeftMargin = 0f
            mSuffixSecondRightMargin = 0f
            mSuffixMillisecondLeftMargin = 0f
        }
    }

    protected open fun initTimeTextBaseInfo() {
        // initialize time text width and height
        val rect = Rect()
        mTimeTextPaint!!.getTextBounds("00", 0, 2, rect)
        mTimeTextWidth = rect.width().toFloat()
        mTimeTextHeight = rect.height().toFloat()
        mTimeTextBottom = rect.bottom.toFloat()
    }

    private fun initTimeTextBaseline(viewHeight: Int, viewPaddingTop: Int, viewPaddingBottom: Int) {
        mTimeTextBaseline = if (viewPaddingTop == viewPaddingBottom) {
            // center
            viewHeight / 2 + mTimeTextHeight / 2 - mTimeTextBottom
        } else {
            // padding top
            viewHeight - (viewHeight - viewPaddingTop) + mTimeTextHeight - mTimeTextBottom
        }
        if (isShowDay && mSuffixDayTextWidth > 0) {
            mSuffixDayTextBaseline = getSuffixTextBaseLine(mSuffixDay)
        }
        if (isShowHour && mSuffixHourTextWidth > 0) {
            mSuffixHourTextBaseline = getSuffixTextBaseLine(mSuffixHour)
        }
        if (isShowMinute && mSuffixMinuteTextWidth > 0) {
            mSuffixMinuteTextBaseline = getSuffixTextBaseLine(mSuffixMinute)
        }
        if (mSuffixSecondTextWidth > 0) {
            mSuffixSecondTextBaseline = getSuffixTextBaseLine(mSuffixSecond)
        }
        if (isShowMillisecond && mSuffixMillisecondTextWidth > 0) {
            mSuffixMillisecondTextBaseline = getSuffixTextBaseLine(mSuffixMillisecond)
        }
    }

    private fun getSuffixTextBaseLine(suffixText: String?): Float {
        val tempRect = Rect()
        mSuffixTextPaint!!.getTextBounds(suffixText, 0, suffixText!!.length, tempRect)
        val ret: Float = when (mSuffixGravity) {
            0 -> mTimeTextBaseline - mTimeTextHeight - tempRect.top  // top

            1 -> mTimeTextBaseline - mTimeTextHeight / 2 + tempRect.height() / 2 // center

            2 -> mTimeTextBaseline - tempRect.bottom // bottom

            else -> mTimeTextBaseline - mTimeTextHeight / 2 + tempRect.height() / 2
        }
        return ret
    }

    protected fun getAllContentWidthBase(timeWidth: Float): Float {
        var width =
            mSuffixDayTextWidth + mSuffixHourTextWidth + mSuffixMinuteTextWidth + mSuffixSecondTextWidth + mSuffixMillisecondTextWidth
        width += (mSuffixDayLeftMargin + mSuffixDayRightMargin + mSuffixHourLeftMargin + mSuffixHourRightMargin
                + mSuffixMinuteLeftMargin + mSuffixMinuteRightMargin + mSuffixSecondLeftMargin + mSuffixSecondRightMargin + mSuffixMillisecondLeftMargin)
        if (isConvertDaysToHours) {
            width += dayAndHourContentWidth
        } else if (isShowHour) {
            width += timeWidth
        }
        if (isShowMinute) {
            width += timeWidth
        }
        if (isShowSecond) {
            width += timeWidth
        }
        if (isShowMillisecond) {
            width += timeWidth
        }
        return width
    }

    private val dayAndHourContentWidth: Float
        get() {
            var width = 0f
            val tempRect = Rect()
            if (isShowDay) {
                val tempDay = Utils.formatNum(mDay)
                mTimeTextPaint!!.getTextBounds(tempDay, 0, tempDay.length, tempRect)
                mDayTimeTextWidth = tempRect.width().toFloat()
                width += mDayTimeTextWidth
            }
            if (isShowHour) {
                val tempHour = Utils.formatNum(mHour)
                mMeasureHourWidthPaint!!.getTextBounds(tempHour, 0, tempHour.length, tempRect)
                mHourTimeTextWidth = tempRect.width().toFloat()
                width += mHourTimeTextWidth
            }
            return width
        }

    /**
     * get all view width
     *
     * @return all view width
     */
    open val allContentWidth: Int
        get() {
            var width = getAllContentWidthBase(mTimeTextWidth)
            if (!isConvertDaysToHours && isShowDay) {
                if (isDayLargeNinetyNine) {
                    val rect = Rect()
                    val tempDay = mDay.toString()
                    mTimeTextPaint!!.getTextBounds(tempDay, 0, tempDay.length, rect)
                    mDayTimeTextWidth = rect.width().toFloat()
                    width += mDayTimeTextWidth
                } else {
                    mDayTimeTextWidth = mTimeTextWidth
                    width += mTimeTextWidth
                }
            }
            return ceil(width.toDouble()).toInt()
        }
    open val allContentHeight: Int
        get() = mTimeTextHeight.toInt()

    open fun onMeasure(v: View, viewWidth: Int, viewHeight: Int, allContentWidth: Int, allContentHeight: Int) {
        initTimeTextBaseline(viewHeight, v.paddingTop, v.paddingBottom)
        mLeftPaddingSize =
            (if (v.paddingLeft == v.paddingRight) (viewWidth - allContentWidth) / 2 else v.paddingLeft).toFloat()
    }

    open fun onDraw(canvas: Canvas) {
        val mHourLeft: Float = if (isShowDay) {
            // draw day text
            canvas.drawText(
                Utils.formatNum(mDay),
                mLeftPaddingSize + mDayTimeTextWidth / 2,
                mTimeTextBaseline,
                mTimeTextPaint!!
            )
            if (mSuffixDayTextWidth > 0) {
                // draw day suffix
                canvas.drawText(
                    mSuffixDay!!,
                    mLeftPaddingSize + mDayTimeTextWidth + mSuffixDayLeftMargin,
                    mSuffixDayTextBaseline,
                    mSuffixTextPaint!!
                )
            }

            // hour left point
            mLeftPaddingSize + mDayTimeTextWidth + mSuffixDayTextWidth + mSuffixDayLeftMargin + mSuffixDayRightMargin
        } else {
            // hour left point
            mLeftPaddingSize
        }

        val mMinuteLeft: Float = if (isShowHour) {
            // draw hour text
            val curTimeTextWidth = if (isConvertDaysToHours) mHourTimeTextWidth else mTimeTextWidth
            canvas.drawText(
                Utils.formatNum(mHour),
                mHourLeft + curTimeTextWidth / 2,
                mTimeTextBaseline,
                mTimeTextPaint!!
            )
            if (mSuffixHourTextWidth > 0) {
                // draw hour suffix
                canvas.drawText(
                    mSuffixHour!!,
                    mHourLeft + curTimeTextWidth + mSuffixHourLeftMargin,
                    mSuffixHourTextBaseline,
                    mSuffixTextPaint!!
                )
            }

            // minute left point
            mHourLeft + curTimeTextWidth + mSuffixHourTextWidth + mSuffixHourLeftMargin + mSuffixHourRightMargin
        } else {
            // minute left point
            mHourLeft
        }
        val mSecondLeft: Float = if (isShowMinute) {
            // draw minute text
            canvas.drawText(
                Utils.formatNum(mMinute),
                mMinuteLeft + mTimeTextWidth / 2,
                mTimeTextBaseline,
                mTimeTextPaint!!
            )
            if (mSuffixMinuteTextWidth > 0) {
                // draw minute suffix
                canvas.drawText(
                    mSuffixMinute!!,
                    mMinuteLeft + mTimeTextWidth + mSuffixMinuteLeftMargin,
                    mSuffixMinuteTextBaseline,
                    mSuffixTextPaint!!
                )
            }

            // second left point
            mMinuteLeft + mTimeTextWidth + mSuffixMinuteTextWidth + mSuffixMinuteLeftMargin + mSuffixMinuteRightMargin
        } else {
            // second left point
            mMinuteLeft
        }
        if (isShowSecond) {
            // draw second text
            canvas.drawText(
                Utils.formatNum(mSecond),
                mSecondLeft + mTimeTextWidth / 2,
                mTimeTextBaseline,
                mTimeTextPaint!!
            )
            if (mSuffixSecondTextWidth > 0) {
                // draw second suffix
                canvas.drawText(
                    mSuffixSecond!!,
                    mSecondLeft + mTimeTextWidth + mSuffixSecondLeftMargin,
                    mSuffixSecondTextBaseline,
                    mSuffixTextPaint!!
                )
            }
            if (isShowMillisecond) {
                // millisecond left point
                val mMillisecondLeft =
                    mSecondLeft + mTimeTextWidth + mSuffixSecondTextWidth + mSuffixSecondLeftMargin + mSuffixSecondRightMargin
                // draw millisecond text
                canvas.drawText(
                    Utils.formatMillisecond(mMillisecond),
                    mMillisecondLeft + mTimeTextWidth / 2,
                    mTimeTextBaseline,
                    mTimeTextPaint!!
                )
                if (mSuffixMillisecondTextWidth > 0) {
                    // draw millisecond suffix
                    canvas.drawText(
                        mSuffixMillisecond!!,
                        mMillisecondLeft + mTimeTextWidth + mSuffixMillisecondLeftMargin,
                        mSuffixMillisecondTextBaseline,
                        mSuffixTextPaint!!
                    )
                }
            }
        }
    }

    fun refTimeShow(
        isShowDay: Boolean,
        isShowHour: Boolean,
        isShowMinute: Boolean,
        isShowSecond: Boolean,
        isShowMillisecond: Boolean
    ): Boolean {
        var mIsShowMillisecond = isShowMillisecond
        if (!isShowSecond) mIsShowMillisecond = false
        var isModCountdownInterval = false
        if (this.isShowDay != isShowDay) {
            this.isShowDay = isShowDay
            // reset day margins
            if (isShowDay) {
                mSuffixDayLeftMargin = mTempSuffixDayLeftMargin
                mSuffixDayRightMargin = mTempSuffixDayRightMargin
            }
        }
        if (this.isShowHour != isShowHour) {
            this.isShowHour = isShowHour
            // reset hour margins
            if (isShowHour) {
                mSuffixHourLeftMargin = mTempSuffixHourLeftMargin
                mSuffixHourRightMargin = mTempSuffixHourRightMargin
            }
        }
        if (this.isShowMinute != isShowMinute) {
            this.isShowMinute = isShowMinute
            // reset minute margins
            if (isShowMinute) {
                mSuffixMinuteLeftMargin = mTempSuffixMinuteLeftMargin
                mSuffixMinuteRightMargin = mTempSuffixMinuteRightMargin
                mSuffixMinute = mTempSuffixMinute
            }
        }
        if (this.isShowSecond != isShowSecond) {
            this.isShowSecond = isShowSecond
            isModCountdownInterval = true

            // reset second margins
            if (isShowSecond) {
                mSuffixSecondLeftMargin = mTempSuffixSecondLeftMargin
                mSuffixSecondRightMargin = mTempSuffixSecondRightMargin
                mSuffixSecond = mTempSuffixSecond
            } else {
                mSuffixMinute = mTempSuffixMinute
            }
            mSuffixMinuteLeftMargin = mTempSuffixMinuteLeftMargin
            mSuffixMinuteRightMargin = mTempSuffixMinuteRightMargin
        }
        if (this.isShowMillisecond != mIsShowMillisecond) {
            this.isShowMillisecond = mIsShowMillisecond
            isModCountdownInterval = true

            // reset millisecond margins
            if (mIsShowMillisecond) {
                mSuffixMillisecondLeftMargin = mTempSuffixMillisecondLeftMargin
            } else {
                mSuffixSecond = mTempSuffixSecond
            }
            mSuffixSecondLeftMargin = mTempSuffixSecondLeftMargin
            mSuffixSecondRightMargin = mTempSuffixSecondRightMargin
        }
        return isModCountdownInterval
    }

    fun handlerAutoShowTime(): Boolean {
        var isReLayout = false
        if (!mHasSetIsShowDay) {
            if (!isShowDay && mDay > 0) {
                // auto show day
                // judgement auto show hour
                if (!mHasSetIsShowHour) {
                    refTimeShow(
                        isShowDay = true,
                        isShowHour = true,
                        isShowMinute = isShowMinute,
                        isShowSecond = isShowSecond,
                        isShowMillisecond = isShowMillisecond
                    )
                } else {
                    refTimeShow(true, isShowHour, isShowMinute, isShowSecond, isShowMillisecond)
                }
                isReLayout = true
            } else if (isShowDay && mDay == 0) {
                // auto hide day
                refTimeShow(false, isShowHour, isShowMinute, isShowSecond, isShowMillisecond)
                isReLayout = true
            } else {
                if (!mHasSetIsShowHour) {
                    if (!isShowHour && (mDay > 0 || mHour > 0)) {
                        // auto show hour
                        refTimeShow(isShowDay, true, isShowMinute, isShowSecond, isShowMillisecond)
                        isReLayout = true
                    } else if (isShowHour && mDay == 0 && mHour == 0) {
                        // auto hide hour
                        refTimeShow(
                            isShowDay = false,
                            isShowHour = false,
                            isShowMinute = isShowMinute,
                            isShowSecond = isShowSecond,
                            isShowMillisecond = isShowMillisecond
                        )
                        isReLayout = true
                    }
                }
            }
        } else {
            if (!mHasSetIsShowHour) {
                if (!isShowHour && (mDay > 0 || mHour > 0)) {
                    // auto show hour
                    refTimeShow(isShowDay, true, isShowMinute, isShowSecond, isShowMillisecond)
                    isReLayout = true
                } else if (isShowHour && mDay == 0 && mHour == 0) {
                    // auto hide hour
                    refTimeShow(isShowDay, false, isShowMinute, isShowSecond, isShowMillisecond)
                    isReLayout = true
                }
            }
        }
        return isReLayout
    }

    fun handlerDayLargeNinetyNine(): Boolean {
        var isReLayout = false
        if (isShowDay) {
            // handler large ninety nine
            if (!isDayLargeNinetyNine && mDay > 99) {
                isDayLargeNinetyNine = true
                isReLayout = true
            } else if (isDayLargeNinetyNine && mDay <= 99) {
                isDayLargeNinetyNine = false
                isReLayout = true
            }
        }
        return isReLayout
    }

    fun setTimes(day: Int, hour: Int, minute: Int, second: Int, millisecond: Int) {
        mDay = day
        mHour = hour
        mMinute = minute
        mSecond = second
        mMillisecond = millisecond
    }

    fun reLayout() {
        initSuffix()
        initTimeTextBaseInfo()
    }

    fun setTimeTextSize(textSize: Float) {
        if (textSize > 0) {
            mTimeTextSize = Utils.sp2px(mContext, textSize)
            mTimeTextPaint!!.textSize = mTimeTextSize
        }
    }

    fun setTimeTextColor(textColor: Int) {
        mTimeTextColor = textColor
        mTimeTextPaint!!.color = mTimeTextColor
    }

    fun setTimeTextBold(isBold: Boolean) {
        isTimeTextBold = isBold
        mTimeTextPaint!!.isFakeBoldText = isTimeTextBold
    }

    fun setSuffixTextSize(textSize: Float) {
        if (textSize > 0) {
            mSuffixTextSize = Utils.sp2px(mContext, textSize)
            mSuffixTextPaint!!.textSize = mSuffixTextSize
        }
    }

    fun setSuffixTextColor(textColor: Int) {
        mSuffixTextColor = textColor
        mSuffixTextPaint!!.color = mSuffixTextColor
    }

    fun setSuffixTextBold(isBold: Boolean) {
        isSuffixTextBold = isBold
        mSuffixTextPaint!!.isFakeBoldText = isSuffixTextBold
    }

    fun setSuffix(suffix: String?) {
        mSuffix = suffix
        setSuffix(mSuffix, mSuffix, mSuffix, mSuffix, mSuffix)
    }

    fun setConvertDaysToHours(isConvertDaysToHours: Boolean): Boolean {
        if (this.isConvertDaysToHours == isConvertDaysToHours) return false
        this.isConvertDaysToHours = isConvertDaysToHours
        return true
    }

    fun setSuffix(
        suffixDay: String?,
        suffixHour: String?,
        suffixMinute: String?,
        suffixSecond: String?,
        suffixMillisecond: String?
    ): Boolean {
        var isRef = false
        if (null != suffixDay) {
            mSuffixDay = suffixDay
            isRef = true
        }
        if (null != suffixHour) {
            mSuffixHour = suffixHour
            isRef = true
        }
        if (null != suffixMinute) {
            mSuffixMinute = suffixMinute
            isRef = true
        }
        if (null != suffixSecond) {
            mSuffixSecond = suffixSecond
            isRef = true
        }
        if (null != suffixMillisecond) {
            mSuffixMillisecond = suffixMillisecond
            isRef = true
        }
        if (isRef) initSuffixBase()
        return isRef
    }

    fun setSuffixLRMargin(suffixLRMargin: Float) {
        mSuffixLRMargin = Utils.dp2px(mContext, suffixLRMargin).toFloat()
        setSuffixMargin(
            mSuffixDayLeftMargin, mSuffixDayLeftMargin,
            mSuffixDayLeftMargin, mSuffixDayLeftMargin,
            mSuffixDayLeftMargin, mSuffixDayLeftMargin,
            mSuffixDayLeftMargin, mSuffixDayLeftMargin,
            mSuffixDayLeftMargin
        )
    }

    fun setSuffixMargin(
        suffixDayMarginL: Float?, suffixDayMarginR: Float?,
        suffixHourMarginL: Float?, suffixHourMarginR: Float?,
        suffixMinuteMarginL: Float?, suffixMinuteMarginR: Float?,
        suffixSecondMarginL: Float?, suffixSecondMarginR: Float?,
        suffixMillisecondMarginL: Float?
    ): Boolean {
        var isRef = false
        if (null != suffixDayMarginL) {
            mSuffixDayLeftMargin = Utils.dp2px(mContext, suffixDayMarginL).toFloat()
            isRef = true
        }
        if (null != suffixDayMarginR) {
            mSuffixDayRightMargin = Utils.dp2px(mContext, suffixDayMarginR).toFloat()
            isRef = true
        }
        if (null != suffixHourMarginL) {
            mSuffixHourLeftMargin = Utils.dp2px(mContext, suffixHourMarginL).toFloat()
            isRef = true
        }
        if (null != suffixHourMarginR) {
            mSuffixHourRightMargin = Utils.dp2px(mContext, suffixHourMarginR).toFloat()
            isRef = true
        }
        if (null != suffixMinuteMarginL) {
            mSuffixMinuteLeftMargin = Utils.dp2px(mContext, suffixMinuteMarginL).toFloat()
            isRef = true
        }
        if (null != suffixMinuteMarginR) {
            mSuffixMinuteRightMargin = Utils.dp2px(mContext, suffixMinuteMarginR).toFloat()
            isRef = true
        }
        if (null != suffixSecondMarginL) {
            mSuffixSecondLeftMargin = Utils.dp2px(mContext, suffixSecondMarginL).toFloat()
            isRef = true
        }
        if (null != suffixSecondMarginR) {
            mSuffixSecondRightMargin = Utils.dp2px(mContext, suffixSecondMarginR).toFloat()
            isRef = true
        }
        if (null != suffixMillisecondMarginL) {
            mSuffixMillisecondLeftMargin = Utils.dp2px(mContext, suffixMillisecondMarginL).toFloat()
            isRef = true
        }
        if (isRef) initTempSuffixMargin()
        return isRef
    }

    fun setSuffixGravity(suffixGravity: Int) {
        mSuffixGravity = suffixGravity
    }

    companion object {
        private const val DEFAULT_SUFFIX = ":"
        private const val DEFAULT_SUFFIX_LR_MARGIN = 3f // dp
    }
}