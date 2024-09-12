package io.agora.utils

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import io.agora.utils.RtmUtils.LogLevel

class LogUtil(var logScrollView: ScrollView, var logsTextView: TextView) {
    private val tag = "rtm-demo"
    private val uiHandler: Handler = Handler(Looper.getMainLooper())

    fun log(level: LogLevel?, message: String) {
        when (level) {
            LogLevel.INFO -> visualLog("[INFO]", message, "#000000")
            LogLevel.ERROR -> visualLog("[ERROR]", message, "#DB322F")
            LogLevel.CALLBACK -> visualLog("[CALLBACK]", message, "#6C71C4")
            LogLevel.API -> visualLog("[API]", message, "#2AA198")
            else -> visualLog("[INFO]", message, "#000000")
        }
    }

    private fun visualLog(level: String, message: CharSequence, color: String) {
        val msg = SpannableStringBuilder("$level $message\n")
        msg.setSpan(
            ForegroundColorSpan(Color.parseColor(color)),
            0,
            msg.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        Log.d(tag, "[$tag]: $msg")
        uiHandler.post {
            logsTextView.append(msg)
            logScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    fun clearLog() {
        uiHandler.post {
            logsTextView.text = ""
            logScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
}