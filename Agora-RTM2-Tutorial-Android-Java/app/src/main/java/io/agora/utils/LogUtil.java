package io.agora.utils;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import io.agora.rtmdemo.R;

public class LogUtil {
    private static final String TAG = "rtm-demo";

    ScrollView logScrollView;
    TextView logsTextView;

    private Handler uiHandler;

    public void log(RtmUtils.LogLevel level, String message) {
        switch (level) {
            case INFO:
                visualLog_INFO(message);
                break;
            case ERROR:
                visualLog_Error(message);
                break;
            case CALLBACK:
                visualLog_CALLBACK(message);
                break;
            case API:
                visualLog_API(message);
                break;
            default:
                visualLog_INFO(message);
        }
    }

    public LogUtil(ScrollView logScrollView, TextView logsTextView) {
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.logScrollView = logScrollView;
        this.logsTextView = logsTextView;
    }

    public void visualLog_API(final CharSequence msg) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder("[RTM2-API] " + msg + "\n");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#2AA198")), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        visualLog(spannableString);
    }

    public void visualLog_CALLBACK(final CharSequence msg) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder("[CALLBACK] " + msg + "\n");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#6C71C4")), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        visualLog(spannableString);
    }

    public void visualLog_Error(final CharSequence msg) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder("[ERROR]" + msg + "\n");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#DB322F")), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        visualLog(spannableString);
    }

    public void visualLog_INFO(final CharSequence msg) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder("[INFO] " + msg + "\n");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        visualLog(spannableString);
    }

    public void visualLog(final CharSequence msg) {
        Log.d(TAG, "[" + TAG + "]: " + msg);
        uiHandler.post(() -> {
            logsTextView.append(msg);
            logScrollView.fullScroll(View.FOCUS_DOWN);
        });
    }

    public void clearLog() {
        uiHandler.post(() -> {
            logsTextView.setText("");
            logScrollView.fullScroll(View.FOCUS_DOWN);
        });
    }
}
