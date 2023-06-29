package com.pandacorp.timeui.presentation.utils.countdownview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.pandacorp.timeui.R
import kotlin.math.max

class CountdownView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    interface OnCountdownEndListener {
        fun onEnd(cv: CountdownView?)
    }

    interface OnCountdownIntervalListener {
        fun onInterval(cv: CountdownView?, remainTime: Long)
    }

    private val mCountdown: BaseCountdown
    private var mCustomCountDownTimer: CustomCountDownTimer? = null
    private var mOnCountdownEndListener: OnCountdownEndListener? = null
    private var mOnCountdownIntervalListener: OnCountdownIntervalListener? = null
    private var mPreviousIntervalCallbackTime: Long = 0
    private var mInterval: Long = 0

    // Monitor the time while the item is detached, subtract it from the current and the countdown's time.
    private var mPreviousAttachSystemTime: Long? = null
    private var mRegistered = false
    private var isRunning = false

    private val isHideTimeBackground: Boolean

    var milliseconds: Long = 0

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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mRegistered || isRunning) {
            mCustomCountDownTimer?.cancel()
            mRegistered = false
            mPreviousAttachSystemTime = System.currentTimeMillis()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!mRegistered && isRunning) {
            mRegistered = true
            mPreviousAttachSystemTime?.let {
                val millisLeft = mCustomCountDownTimer!!.milliseconds - (System.currentTimeMillis() - it)
                updateShow(millisLeft)
            }
            start()
        }
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

    fun start(milliseconds: Long = this.milliseconds) {
        if (milliseconds <= 0) return
        mPreviousIntervalCallbackTime = 0
        mPreviousAttachSystemTime = null
        isRunning = true
        mCustomCountDownTimer?.cancel()
        mCustomCountDownTimer = null
        val countDownInterval: Long = if (mCountdown.isShowMillisecond) {
            updateShow(milliseconds)
            10
        } else {
            1000
        }
        mCustomCountDownTimer = object : CustomCountDownTimer(countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                updateShow(millisUntilFinished)
            }

            override fun onFinish() {
                allShowZero()
                if (null != mOnCountdownEndListener) mOnCountdownEndListener!!.onEnd(this@CountdownView)
            }
        }
        mCustomCountDownTimer?.start(milliseconds)
    }

    fun cancel() {
        isRunning = false
        mCustomCountDownTimer?.cancel()
    }

    fun allShowZero() {
        mCountdown.setTimes(0, 0, 0, 0, 0)
        invalidate()
    }

    fun updateShow(ms: Long) {
        milliseconds = ms
        reSetTime(ms)

        // interval callback
        if (mInterval > 0 && null != mOnCountdownIntervalListener) {
            if (mPreviousIntervalCallbackTime == 0L)
                mPreviousIntervalCallbackTime = ms
            else if (ms + mInterval <= mPreviousIntervalCallbackTime) {
                mPreviousIntervalCallbackTime = ms
                mOnCountdownIntervalListener!!.onInterval(this, milliseconds)
            }
        }
        if (mCountdown.handlerAutoShowTime() || mCountdown.handlerDayLargeNinetyNine())
            reLayout()
        else invalidate()
    }

    private fun reSetTime(ms: Long) {
        if (ms < 0) {
            if (isRunning) mCustomCountDownTimer?.onFinish()
            else allShowZero()
            return
        }
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

}