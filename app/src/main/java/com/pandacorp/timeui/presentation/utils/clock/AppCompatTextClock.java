package com.pandacorp.timeui.presentation.utils.clock;

import static android.view.ViewDebug.ExportedProperty;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

import com.pandacorp.timeui.R;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * This is a simplified implementation of {@link android.widget.TextClock} that contains less redundant commentaries
 * And fixes the bug in RecyclerView, when getHandler could return null, therefore use postDelayed instead for the same purpose
 */
public class AppCompatTextClock extends AppCompatTextView {
    private CharSequence mFormat12;
    private CharSequence mFormat24;
    @ExportedProperty
    private CharSequence mFormat;
    @ExportedProperty
    private boolean mHasSeconds;
    private boolean mRegistered;
    private boolean mShouldRunTicker;
    private Calendar mTime;
    private String mTimeZone;
    private ContentObserver mFormatChangeObserver;

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            chooseFormat();
            onTimeChanged();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            chooseFormat();
            onTimeChanged();
        }
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mTimeZone == null && Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                final String timeZone = intent.getStringExtra("time-zone");
                createTime(timeZone);
            } else if (!mShouldRunTicker && (Intent.ACTION_TIME_TICK.equals(intent.getAction())
                    || Intent.ACTION_TIME_CHANGED.equals(intent.getAction()))) {
                return;
            }
            onTimeChanged();
        }
    };

    private final Runnable mTicker = new Runnable() {
        public void run() {
            removeCallbacks(this);
            onTimeChanged();
            long now = System.currentTimeMillis();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(now);

            int seconds = calendar.get(Calendar.SECOND);
            if (mHasSeconds) {
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, seconds + 1);
            } else {
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.add(Calendar.MINUTE, 1);
            }

            long millisUntilNextTick = calendar.getTimeInMillis() - now;
            if (millisUntilNextTick <= 0) {
                // This should never happen, but if it does, then tick again in a second.
                millisUntilNextTick = 1000;
            }
            postDelayed(this, millisUntilNextTick);
        }
    };

    public AppCompatTextClock(Context context) {
        super(context);
        init();
    }

    public AppCompatTextClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppCompatTextClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AppCompatTextClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.AppCompatTextClock, defStyleAttr, defStyleRes);
        ViewCompat.saveAttributeDataForStyleable(this, context, R.styleable.AppCompatTextClock,
                attrs, a, defStyleAttr, defStyleRes);
        try {
            mFormat12 = a.getText(R.styleable.AppCompatTextClock_format12Hour);
            mFormat24 = a.getText(R.styleable.AppCompatTextClock_format24Hour);
            mTimeZone = a.getString(R.styleable.AppCompatTextClock_timeZone);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        if (mFormat12 == null) {
            mFormat12 = getBestDateTimePattern("hm");
        }
        if (mFormat24 == null) {
            mFormat24 = getBestDateTimePattern("Hm");
        }
        createTime(mTimeZone);
        chooseFormat();
    }

    private void createTime(String timeZone) {
        if (timeZone != null) {
            mTime = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        } else {
            mTime = Calendar.getInstance();
        }
    }

    public boolean is24HourModeEnabled() {
        return DateFormat.is24HourFormat(getContext());
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
        createTime(timeZone);
        onTimeChanged();
    }

    private void chooseFormat() {
        final boolean format24Requested = is24HourModeEnabled();
        if (format24Requested) {
            mFormat = abc(mFormat24, mFormat12, getBestDateTimePattern("Hm"));
        } else {
            mFormat = abc(mFormat12, mFormat24, getBestDateTimePattern("hm"));
        }
        boolean hadSeconds = mHasSeconds;
        mHasSeconds = hasSeconds(mFormat);
        if (mShouldRunTicker && hadSeconds != mHasSeconds) {
            mTicker.run();
        }
    }

    private String getBestDateTimePattern(String skeleton) {
        return DateFormat.getBestDateTimePattern(getContext().getResources().getConfiguration().locale, skeleton);
    }

    /**
     * Returns a if not null, else return b if not null, else return c.
     */
    private static CharSequence abc(CharSequence a, CharSequence b, CharSequence c) {
        return a == null ? (b == null ? c : b) : a;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mRegistered) {
            mRegistered = true;
            registerReceiver();
            registerObserver();
            createTime(mTimeZone);
        }
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (!mShouldRunTicker && isVisible) {
            mShouldRunTicker = true;
            mTicker.run();
        } else if (mShouldRunTicker && !isVisible) {
            mShouldRunTicker = false;
            removeCallbacks(mTicker);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRegistered) {
            unregisterReceiver();
            unregisterObserver();
            mRegistered = false;
        }
    }

    private void registerReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
    }

    private void registerObserver() {
        if (mRegistered) {
            if (mFormatChangeObserver == null) {
                mFormatChangeObserver = new FormatChangeObserver(getHandler());
            }
            final ContentResolver resolver = getContext().getContentResolver();
            Uri uri = Settings.System.getUriFor(Settings.System.TIME_12_24);
            resolver.registerContentObserver(uri, true, mFormatChangeObserver);
        }
    }

    private void unregisterReceiver() {
        getContext().unregisterReceiver(mIntentReceiver);
    }

    private void unregisterObserver() {
        if (mFormatChangeObserver != null) {
            final ContentResolver resolver = getContext().getContentResolver();
            resolver.unregisterContentObserver(mFormatChangeObserver);
        }
    }

    private void onTimeChanged() {
        mTime.setTimeInMillis(System.currentTimeMillis());
        setText(DateFormat.format(mFormat, mTime));
        setContentDescription(DateFormat.format(mFormat, mTime));
    }

    private boolean hasSeconds(CharSequence inFormat) {
        if (inFormat == null) return false;
        final int length = inFormat.length();
        boolean insideQuote = false;
        for (int i = 0; i < length; i++) {
            final char c = inFormat.charAt(i);
            if (c == '\'') {
                insideQuote = !insideQuote;
            } else if (!insideQuote) {
                if (c == 's') {
                    return true;
                }
            }
        }
        return false;
    }
}