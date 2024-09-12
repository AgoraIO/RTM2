package io.agora.rtmdemo

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import io.agora.rtm.*
import io.agora.rtmdemo.databinding.ActivityPrivateBinding
import io.agora.utils.LogUtil
import io.agora.utils.RtmUtils.LogLevel

class PrivateActivity : Activity() {
    private lateinit var binding: ActivityPrivateBinding

    private val rtmClient: RtmClient? = RtmManager.the().rtmClient
    private var logUtil: LogUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        logUtil = LogUtil(binding.privateLog.logScrollView, binding.privateLog.logTextView)
        RtmManager.the().setLogUtil(logUtil)
        binding.privateMessage.setText("hello rtm2")
        binding.getPeerMessagesBtn.setOnClickListener { onClickGetMessagesBtn() }
        binding.sendPrivateMessageBtn.setOnClickListener { onClickSendPrivateMessageBtn() }
        binding.privateLog.clearLogBtn.setOnClickListener { onClickClearLogBtn() }
    }

    private fun onClickGetMessagesBtn() {
//        log(LogLevel.API, "get history messages")
//        val start =
//            if (binding.historyStart.text.isEmpty()) 0 else binding.historyStart.text.toString()
//                .toLong()
//        val end = if (binding.historyEnd.text.isEmpty()) 0 else binding.historyEnd.text.toString()
//            .toLong()
//        val count =
//            if (binding.historyCount.text.isEmpty()) 0 else binding.historyCount.text.toString()
//                .toInt()
//        val options = GetHistoryMessagesOptions(count, start, end);
//        val peer = binding.peerName.text.toString()
//        rtmClient?.history!!.getMessages(
//            peer,
//            RtmConstants.RtmChannelType.USER,
//            options,
//            object : ResultCallback<GetMessagesResult> {
//                override fun onSuccess(result: GetMessagesResult) {
//                    log(
//                        LogLevel.CALLBACK,
//                        "get history messages success! new start: ${result.newStart}"
//                    )
//                    for (message in result.messageList) {
//                        log(LogLevel.INFO, message.toString())
//                    }
//                }
//
//                override fun onFailure(errorInfo: ErrorInfo) {
//                    log(LogLevel.ERROR, errorInfo.toString())
//                }
//            })
    }

    private fun onClickSendPrivateMessageBtn() {
        log(LogLevel.API, "send private message")
        val storeInHistory = binding.storeInHistory.isChecked()
        val options = PublishOptions(
            RtmConstants.RtmChannelType.USER, ""
        )
        val peer = binding.peerName.text.toString()
        val message = binding.privateMessage.text.toString()
        rtmClient?.publish(peer, message, options, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "send message to peer: $peer success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickClearLogBtn() {
        logUtil!!.clearLog()
    }

    private fun log(level: LogLevel, message: String) {
        logUtil!!.log(level, message)
    }
}