package com.pandacorp.timeui.presentation.utils.countdownview

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.pandacorp.timeui.R
import kotlin.math.max

class CountdownView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mCountdown: BaseCountdown
    private var mCustomCountDownTimer: CustomCountDownTimer? = null
    private var mOnCountdownEndListener: OnCountdownEndListener? = null
    private var mOnCountdownIntervalListener: OnCountdownIntervalListener? = null
    private val isHideTimeBackground: Boolean
    private var mPreviousIntervalCallbackTime: Long = 0
    private var mInterval: Long = 0

    var remainTime: Long = 0
        private set

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CountdownView)
        isHideTimeBackground = ta.getBoolean(R.styleable.CountdownView_isHideTimeBackground, true)
        mCountdown = if (isHideTimeBackground) BaseCountdown() else BackgroundCountdown()
        mCountdown.initStyleAttr(context, ta)
        ta.recycle()
        mCountdown.initialize()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val contentAllWidth = mCountdown.allContentWidth
        val contentAllHeight = mCountdown.allContentHeight
        val viewWidth = measureSize(1, contentAllWidth, widthMeasureSpec)
        val viewHeight = measureSize(2, contentAllHeight, heightMeasureSpec)
        setMeasuredDimension(viewWidth, viewHeight)
        mCountdown.onMeasure(this, viewWidth, viewHeight, contentAllWidth, contentAllHeight)
    }

    /**
     * measure view Size
     *
     * @param specType    1 width 2 height
     * @param contentSize all content view size
     * @param measureSpec spec
     * @return measureSize
     */
    private fun measureSize(specType: Int, contentSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = max(contentSize, specSize)
        } else {
            result = contentSize
            result += if (specType == 1) {
                // width
                paddingLeft + paddingRight
            } else {
                // height
                paddingTop + paddingBottom
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mCountdown.onDraw(canvas)
    }

    private fun reLayout() {
        mCountdown.reLayout()
        requestLayout()
    }

    /**
     * start countdown
     *
     * @param millisecond millisecond
     */
    fun start(millisecond: Long) {
        if (millisecond <= 0) return
        mPreviousIntervalCallbackTime = 0
        if (null != mCustomCountDownTimer) {
            mCustomCountDownTimer!!.stop()
            mCustomCountDownTimer = null
        }
        val countDownInterval: Long
        if (mCountdown.isShowMillisecond) {
            countDownInterval = 10
            updateShow(millisecond)
        } else {
            countDownInterval = 1000
        }
        mCustomCountDownTimer = object : CustomCountDownTimer(millisecond, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                updateShow(millisUntilFinished)
            }

            override fun onFinish() {
                // countdown end
                allShowZero()
                // callback
                if (null != mOnCountdownEndListener) {
                    mOnCountdownEndListener!!.onEnd(this@CountdownView)
                }
            }
        }
        mCustomCountDownTimer!!.start()
    }

    fun stop() {
        if (null != mCustomCountDownTimer) mCustomCountDownTimer!!.stop()
    }

    fun pause() {
        if (null != mCustomCountDownTimer) mCustomCountDownTimer!!.pause()
    }

    fun restart() {
        if (null != mCustomCountDownTimer) mCustomCountDownTimer!!.restart()
    }

    fun allShowZero() {
        mCountdown.setTimes(0, 0, 0, 0, 0)
        invalidate()
    }

    /**
     * set countdown end callback listener
     *
     * @param onCountdownEndListener OnCountdownEndListener
     */
    fun setOnCountdownEndListener(onCountdownEndListener: OnCountdownEndListener?) {
        mOnCountdownEndListener = onCountdownEndListener
    }

    /**
     * set interval callback listener
     *
     * @param interval                    interval time
     * @param onCountdownIntervalListener OnCountdownIntervalListener
     */
    fun setOnCountdownIntervalListener(
        interval: Long,
        onCountdownIntervalListener: OnCountdownIntervalListener?
    ) {
        mInterval = interval
        mOnCountdownIntervalListener = onCountdownIntervalListener
    }

    /**
     * get day
     *
     * @return current day
     */
    val day: Int
        get() = mCountdown.mDay

    /**
     * get hour
     *
     * @return current hour
     */
    val hour: Int
        get() = mCountdown.mHour

    /**
     * get minute
     *
     * @return current minute
     */
    val minute: Int
        get() = mCountdown.mMinute

    /**
     * get second
     *
     * @return current second
     */
    val second: Int
        get() = mCountdown.mSecond

    fun updateShow(ms: Long) {
        remainTime = ms
        reSetTime(ms)

        // interval callback
        if (mInterval > 0 && null != mOnCountdownIntervalListener) {
            if (mPreviousIntervalCallbackTime == 0L) {
                mPreviousIntervalCallbackTime = ms
            } else if (ms + mInterval <= mPreviousIntervalCallbackTime) {
                mPreviousIntervalCallbackTime = ms
                mOnCountdownIntervalListener!!.onInterval(this, remainTime)
            }
        }
        if (mCountdown.handlerAutoShowTime() || mCountdown.handlerDayLargeNinetyNine()) {
            reLayout()
        } else {
            invalidate()
        }
    }

    private fun reSetTime(ms: Long) {
        var day = 0
        val hour: Int
        if (!mCountdown.isConvertDaysToHours) {
            day = (ms / (1000 * 60 * 60 * 24)).toInt()
            hour = (ms % (1000 * 60 * 60 * 24) / (1000 * 60 * 60)).toInt()
        } else {
            hour = (ms / (1000 * 60 * 60)).toInt()
        }
        val minute = (ms % (1000 * 60 * 60) / (1000 * 60)).toInt()
        val second = (ms % (1000 * 60) / 1000).toInt()
        val millisecond = (ms % 1000).toInt()
        mCountdown.setTimes(day, hour, minute, second, millisecond)
    }

    interface OnCountdownEndListener {
        fun onEnd(cv: CountdownView?)
    }

    interface OnCountdownIntervalListener {
        fun onInterval(cv: CountdownView?, remainTime: Long)
    }

    fun dynamicShow(dynamicConfig: DynamicConfig?) {
        if (null == dynamicConfig) return
        var isReLayout = false
        var isInvalidate = false
        val timeTextSize = dynamicConfig.timeTextSize
        if (null != timeTextSize) {
            mCountdown.setTimeTextSize(timeTextSize)
            isReLayout = true
        }
        val suffixTextSize = dynamicConfig.suffixTextSize
        if (null != suffixTextSize) {
            mCountdown.setSuffixTextSize(suffixTextSize)
            isReLayout = true
        }
        val timeTextColor = dynamicConfig.timeTextColor
        if (null != timeTextColor) {
            mCountdown.setTimeTextColor(timeTextColor)
            isInvalidate = true
        }
        val suffixTextColor = dynamicConfig.suffixTextColor
        if (null != suffixTextColor) {
            mCountdown.setSuffixTextColor(suffixTextColor)
            isInvalidate = true
        }
        val isTimeTextBold = dynamicConfig.isTimeTextBold
        if (null != isTimeTextBold) {
            mCountdown.setTimeTextBold(isTimeTextBold)
            isReLayout = true
        }
        val isSuffixTimeTextBold = dynamicConfig.isSuffixTimeTextBold
        if (null != isSuffixTimeTextBold) {
            mCountdown.setSuffixTextBold(isSuffixTimeTextBold)
            isReLayout = true
        }

        // suffix text (all)
        val suffix = dynamicConfig.suffix
        if (!TextUtils.isEmpty(suffix)) {
            mCountdown.setSuffix(suffix)
            isReLayout = true
        }

        // suffix text
        val suffixDay = dynamicConfig.suffixDay
        val suffixHour = dynamicConfig.suffixHour
        val suffixMinute = dynamicConfig.suffixMinute
        val suffixSecond = dynamicConfig.suffixSecond
        val suffixMillisecond = dynamicConfig.suffixMillisecond
        if (mCountdown.setSuffix(suffixDay, suffixHour, suffixMinute, suffixSecond, suffixMillisecond)) {
            isReLayout = true
        }

        // suffix margin (all)
        val suffixLRMargin = dynamicConfig.suffixLRMargin
        if (null != suffixLRMargin) {
            mCountdown.setSuffixLRMargin(suffixLRMargin)
            isReLayout = true
        }

        // suffix margin
        val suffixDayLeftMargin = dynamicConfig.suffixDayLeftMargin
        val suffixDayRightMargin = dynamicConfig.suffixDayRightMargin
        val suffixHourLeftMargin = dynamicConfig.suffixHourLeftMargin
        val suffixHourRightMargin = dynamicConfig.suffixHourRightMargin
        val suffixMinuteLeftMargin = dynamicConfig.suffixMinuteLeftMargin
        val suffixMinuteRightMargin = dynamicConfig.suffixMinuteRightMargin
        val suffixSecondLeftMargin = dynamicConfig.suffixSecondLeftMargin
        val suffixSecondRightMargin = dynamicConfig.suffixSecondRightMargin
        val suffixMillisecondRightMargin = dynamicConfig.suffixMillisecondLeftMargin
        if (mCountdown.setSuffixMargin(
                suffixDayLeftMargin,
                suffixDayRightMargin,
                suffixHourLeftMargin,
                suffixHourRightMargin,
                suffixMinuteLeftMargin,
                suffixMinuteRightMargin,
                suffixSecondLeftMargin,
                suffixSecondRightMargin,
                suffixMillisecondRightMargin
            )
        ) {
            isReLayout = true
        }
        val suffixGravity = dynamicConfig.suffixGravity
        if (null != suffixGravity) {
            mCountdown.setSuffixGravity(suffixGravity)
            isReLayout = true
        }

        // judgement time show
        val tempIsShowDay = dynamicConfig.isShowDay
        val tempIsShowHour = dynamicConfig.isShowHour
        val tempIsShowMinute = dynamicConfig.isShowMinute
        val tempIsShowSecond = dynamicConfig.isShowSecond
        val tempIsShowMillisecond = dynamicConfig.isShowMillisecond
        if (null != tempIsShowDay || null != tempIsShowHour || null != tempIsShowMinute || null != tempIsShowSecond || null != tempIsShowMillisecond) {
            var isShowDay = mCountdown.isShowDay
            if (null != tempIsShowDay) {
                isShowDay = tempIsShowDay
                mCountdown.mHasSetIsShowDay = true
            } else {
                mCountdown.mHasSetIsShowDay = false
            }
            var isShowHour = mCountdown.isShowHour
            if (null != tempIsShowHour) {
                isShowHour = tempIsShowHour
                mCountdown.mHasSetIsShowHour = true
            } else {
                mCountdown.mHasSetIsShowHour = false
            }
            val isShowMinute = tempIsShowMinute ?: mCountdown.isShowMinute
            val isShowSecond = tempIsShowSecond ?: mCountdown.isShowSecond
            val isShowMillisecond = tempIsShowMillisecond ?: mCountdown.isShowMillisecond
            val isModCountdownInterval =
                mCountdown.refTimeShow(isShowDay, isShowHour, isShowMinute, isShowSecond, isShowMillisecond)

            // judgement modify countdown interval
            if (isModCountdownInterval) {
                start(remainTime)
            }
            isReLayout = true
        }
        val backgroundInfo = dynamicConfig.backgroundInfo
        if (!isHideTimeBackground && null != backgroundInfo) {
            val backgroundCountdown = mCountdown as BackgroundCountdown
            val size = backgroundInfo.size
            if (null != size) {
                backgroundCountdown.setTimeBgSize(size)
                isReLayout = true
            }
            val color = backgroundInfo.color
            if (null != color) {
                backgroundCountdown.setTimeBgColor(color)
                isInvalidate = true
            }
            val radius = backgroundInfo.radius
            if (null != radius) {
                backgroundCountdown.setTimeBgRadius(radius)
                isInvalidate = true
            }
            val isShowTimeBgDivisionLine = backgroundInfo.isShowTimeBgDivisionLine
            if (null != isShowTimeBgDivisionLine) {
                backgroundCountdown.setIsShowTimeBgDivisionLine(isShowTimeBgDivisionLine)
                if (isShowTimeBgDivisionLine) {
                    val divisionLineColor = backgroundInfo.divisionLineColor
                    if (null != divisionLineColor) {
                        backgroundCountdown.setTimeBgDivisionLineColor(divisionLineColor)
                    }
                    val divisionLineSize = backgroundInfo.divisionLineSize
                    if (null != divisionLineSize) {
                        backgroundCountdown.setTimeBgDivisionLineSize(divisionLineSize)
                    }
                }
                isInvalidate = true
            }
            val isShowTimeBgBorder = backgroundInfo.isShowTimeBgBorder
            if (null != isShowTimeBgBorder) {
                backgroundCountdown.setIsShowTimeBgBorder(isShowTimeBgBorder)
                if (isShowTimeBgBorder) {
                    val borderColor = backgroundInfo.borderColor
                    if (null != borderColor) {
                        backgroundCountdown.setTimeBgBorderColor(borderColor)
                    }
                    val borderSize = backgroundInfo.borderSize
                    if (null != borderSize) {
                        backgroundCountdown.setTimeBgBorderSize(borderSize)
                    }
                    val borderRadius = backgroundInfo.borderRadius
                    if (null != borderRadius) {
                        backgroundCountdown.setTimeBgBorderRadius(borderRadius)
                    }
                }
                isReLayout = true
            }
        }
        val tempIsConvertDaysToHours = dynamicConfig.isConvertDaysToHours
        if (null != tempIsConvertDaysToHours && mCountdown.setConvertDaysToHours(tempIsConvertDaysToHours)) {
            reSetTime(remainTime)
            isReLayout = true
        }
        if (isReLayout) {
            reLayout()
        } else if (isInvalidate) {
            invalidate()
        }
    }
}