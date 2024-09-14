package io.agora.rtmdemo

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import io.agora.rtm.ErrorInfo
import io.agora.rtm.LinkStateEvent
import io.agora.rtm.MessageEvent
import io.agora.rtm.PresenceEvent
import io.agora.rtm.PublishOptions
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmClient
import io.agora.rtm.RtmConfig
import io.agora.rtm.RtmConstants.RtmAreaCode
import io.agora.rtm.RtmEventListener
import io.agora.rtm.RtmLogConfig
import io.agora.rtm.SubscribeOptions
import io.agora.rtmdemo.databinding.ActivityMainBinding
import java.util.EnumSet
import java.util.Random

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private var rtmClient: RtmClient? = null
    private var uiHandler: Handler? = null

    private val eventListener: RtmEventListener = object : RtmEventListener {
        override fun onMessageEvent(event: MessageEvent) {
            printLog(event.toString())
        }

        override fun onPresenceEvent(event: PresenceEvent) {
            printLog(event.toString())
        }

        override fun onLinkStateEvent(event: LinkStateEvent) {
            printLog(event.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.uiHandler = Handler(Looper.getMainLooper())

        binding.loginButton.setOnClickListener { onClickLogin() }
        binding.logoutButton.setOnClickListener { onClickLogout() }
        binding.joinButton.setOnClickListener { onClickJoin() }
        binding.leaveButton.setOnClickListener { onClickLeave() }
        binding.sendChannelMsgButton.setOnClickListener { onClickSendMessage() }
        binding.demoLog.clearLogBtn.setOnClickListener { onClickClearLog() }

        val random = Random()
        binding.uid.setText("user_" + String.format("%04d", random.nextInt(10000)))
        binding.channelName.setText("channel_" + String.format("%02d", random.nextInt(100)))
        binding.message.setText("hello rtm2")
        val appid = applicationContext.getString(R.string.agora_app_id)
        if (!appid.contains("#")) {
            binding.appid.setText(appid)
            binding.token.setText(appid)
        }
    }

    private fun onClickLogin() {
        val appId = binding.token.text.toString()
        val token = binding.token.text.toString()
        val userId = binding.uid.text.toString()
        val logConfig = RtmLogConfig()
        val rtmConfig = RtmConfig.Builder(appId, userId)
            .areaCode(EnumSet.of<RtmAreaCode>(RtmAreaCode.AS, RtmAreaCode.CN))
            .eventListener(eventListener)
            .logConfig(logConfig)
            .build()

        rtmClient = RtmClient.create(rtmConfig)
        rtmClient?.login(token, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                printLog("login success!")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                printLog(errorInfo.toString())
            }
        })
    }

    private fun onClickLogout() {
        rtmClient?.logout(object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                printLog("logout success!")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                printLog(errorInfo.toString())
            }
        })
    }

    private fun onClickJoin() {
        val channelName = binding.channelName.text.toString()
        val options = SubscribeOptions()

        rtmClient?.subscribe(channelName, options, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                printLog("join channel success!")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                printLog(errorInfo.toString())
            }
        })
    }

    private fun onClickLeave() {
        val channelName = binding.channelName.text.toString()

        rtmClient?.unsubscribe(channelName, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                printLog("leave channel success!")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                printLog(errorInfo.toString())
            }
        })
    }

    private fun onClickSendMessage() {
        val channelName = binding.channelName.text.toString()
        val message = binding.message.text.toString()
        val options = PublishOptions()
        options.customType = ""

        rtmClient?.publish(channelName, message, options, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                printLog("send message success!")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                printLog(errorInfo.toString())
            }
        })
    }

    private fun onClickClearLog() {
        binding.demoLog.logTextView.setText("")
    }

    fun printLog(record: String) {
        val spannableString = SpannableStringBuilder("[INFO] $record\n")
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#000000")),
            0,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        uiHandler?.post(Runnable {
            binding.demoLog.logTextView.append(spannableString.toString() + "\n")
            binding.demoLog.logScrollView.fullScroll(View.FOCUS_DOWN)
        })
    }
}