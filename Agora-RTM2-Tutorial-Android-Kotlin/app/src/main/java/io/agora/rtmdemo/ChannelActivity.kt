package io.agora.rtmdemo

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.HandlerThread
import android.widget.*
import io.agora.rtm.*
import io.agora.rtm.RtmConstants.RtmChannelType
import io.agora.rtm.RtmConstants.RtmMessageQos
import io.agora.rtmdemo.databinding.ActivityChannelBinding
import io.agora.utils.LogUtil
import io.agora.utils.RtmUtils.LogLevel
import org.json.JSONObject

class ChannelActivity : Activity() {
    private lateinit var binding: ActivityChannelBinding

    private val rtmClient: RtmClient? = RtmManager.the().rtmClient
    private var streamChannel: StreamChannel? = null
    private val handlerThread = HandlerThread("handlerThread")
    private var logUtil: LogUtil? = null
    private var messageSeq = 0
    private val subscribeUserList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.streamChannelName.setText("stream_channel1")
        binding.topicName.setText("test_topic")
        binding.subscribeUserId.setText("user2")
        binding.topicMessage.setText("rtm test msg")
        binding.msgChannelName.setText("message_channel1")
        binding.channelMessage.setText("rtm test msg")
        binding.customType.setText("")
        bindBtnClickListener()

        handlerThread.start()
        logUtil = LogUtil(binding.channelLog.logScrollView, binding.channelLog.logTextView)
        RtmManager.the().setLogUtil(logUtil)
    }

    override fun onDestroy() {
        super.onDestroy()
        RtmManager.the().setLogUtil(null)
    }

    private fun bindBtnClickListener() {
        binding.joinChannelBtn.setOnClickListener { onClickJoinChannelBtn() }
        binding.leaveChannelBtn.setOnClickListener { onClickLeaveChannelBtn() }
        binding.joinTopicBtn.setOnClickListener { onClickJoinTopicBtn() }
        binding.sendTopicMessageBtn.setOnClickListener { onClickSendMessageBtn() }
        binding.leaveTopicBtn.setOnClickListener { onLeaveTopicBtnClicked() }
        binding.addUserToListBtn.setOnClickListener() { onClickAddUserBtn() }
        binding.clearUserBtn.setOnClickListener() { onClickClearUserBtn() }
        binding.subscribeTopicBtn.setOnClickListener() { onClickSubscribeTopicBtn() }
        binding.unsubscribeTopicBtn.setOnClickListener() { onClickUnsubscribeTopicBtn() }
        binding.getTopicUserListBtn.setOnClickListener() { onClickGetSubscribeUserListBtn() }
        binding.subscribeBtn.setOnClickListener() { onClickSubscribeBtn() }
        binding.unsubscribeBtn.setOnClickListener() { onClickUnsubscribeBtn() }
        binding.publishBtn.setOnClickListener() { onClickPublishBtn() }
        binding.getChannelMessages.setOnClickListener { onClickGetChannelMessages() }
        binding.channelLog.clearLogBtn.setOnClickListener() { onClickClearLogBtn() }
    }

    private fun onClickJoinChannelBtn() {
        log(LogLevel.API, "join channel")
        val channelName = binding.streamChannelName.text.toString()
        if (true) { // rtm stream channel
            try {
                streamChannel = rtmClient?.createStreamChannel(channelName)
            } catch (e: Exception) {
                log(LogLevel.ERROR, "create stream channel exception: $e")
            }
        } else { // reuse rtc channel
            try {
                // streamChannel = mRtcEngine.getStreamChannel(channelName);
                log(LogLevel.INFO, "get stream channel from rtc")
            } catch (e: Exception) {
                log(LogLevel.ERROR, "create stream channel exception: $e")
            }
        }

        val options = JoinChannelOptions(
            RtmManager.the().token,
            binding.stWithPresence.isChecked,
            binding.stWithMetadata.isChecked,
            binding.stWithLock.isChecked,
            binding.stBeQuiet.isChecked
        )

        streamChannel?.join(options, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "join stream channel $channelName success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickLeaveChannelBtn() {
        log(LogLevel.API, "leave channel")
        streamChannel?.leave(object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "leave stream channel success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickJoinTopicBtn() {
        log(LogLevel.API, "join topic")
        val topicName = binding.topicName.text.toString()
        val options = JoinTopicOptions(
            RtmMessageQos.ORDERED,
            RtmConstants.RtmMessagePriority.HIGH,
            false,
            "test topic meta"
        )

        streamChannel?.joinTopic(topicName, options, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "join topic($topicName) success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickSendMessageBtn() {
        log(LogLevel.API, "send message")
        val topicName = binding.topicName.text.toString()
        val msg = binding.topicMessage.text.toString()
        //val message = msg.toString().getBytes();
        val options = TopicMessageOptions("custom type")

        streamChannel?.publishTopicMessage(topicName, msg, options, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "publish topic message success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onLeaveTopicBtnClicked() {
        log(LogLevel.API, "leave topic")
        val topicName = binding.topicName.text.toString()
        streamChannel?.leaveTopic(topicName, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "leave topic($topicName) success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickAddUserBtn() {
        val userId = binding.subscribeUserId.text.toString()
        subscribeUserList.add(userId)
    }

    private fun onClickClearUserBtn() {
        subscribeUserList.clear()
    }

    private fun onClickSubscribeTopicBtn() {
        log(LogLevel.API, "subscribe topic")
        val options = TopicOptions()
        options.users = subscribeUserList
        val topicName = binding.topicName.text.toString()
        streamChannel?.subscribeTopic(
            topicName,
            options,
            object : ResultCallback<SubscribeTopicResult?> {
                override fun onSuccess(responseInfo: SubscribeTopicResult?) {
                    log(LogLevel.CALLBACK, "subscribe topic($topicName) success")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
    }

    private fun onClickUnsubscribeTopicBtn() {
        log(LogLevel.API, "unsubscribe topic")
        val options = TopicOptions()
        options.users = subscribeUserList
        val topicName = binding.topicName.text.toString()
        streamChannel?.unsubscribeTopic(topicName, options,
            object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    log(LogLevel.CALLBACK, "unsubscribe topic success")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
    }

    private fun onClickGetSubscribeUserListBtn() {
        log(LogLevel.API, "get subscribe user list")
        val topicName = binding.topicName.text.toString()
        try {
            streamChannel?.getSubscribedUserList(
                topicName,
                object : ResultCallback<ArrayList<String>> {
                    override fun onSuccess(userList: ArrayList<String>) {
                        for (user in userList) {
                            log(LogLevel.INFO, "subscribed user: $user")
                        }
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        log(LogLevel.ERROR, errorInfo.toString())
                    }
                })
        } catch (e: Exception) {
            log(LogLevel.ERROR, "get subscribe user list failed! $e")
        }
    }

    private fun onTestMessageBtnClicked() {
        log(LogLevel.API, "send test message to topic")
        val topicName = binding.topicName.text.toString()
        var msg = String()
        try {
            val jsonObject = JSONObject()
            jsonObject.put("type", "message")
            jsonObject.put("user_id", "user1")
            jsonObject.put("action", "req")
            jsonObject.put("seq", messageSeq++)
            jsonObject.put("tick", System.currentTimeMillis())
            msg = jsonObject.toString()
        } catch (e: Exception) {
            log(LogLevel.ERROR, "send topic message failed! json exception: $e")
        }
        val message = msg.toByteArray()
        streamChannel?.publishTopicMessage(
            topicName,
            message,
            TopicMessageOptions(),
            object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    log(LogLevel.CALLBACK, "publish topic message success")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
    }

    private fun onClickSubscribeBtn() {
        log(LogLevel.API, "subscribe channel")
        val channelName = binding.msgChannelName.text.toString()
        val options = SubscribeOptions(
            binding.msgWithMessage.isChecked,
            binding.msgWithPresence.isChecked,
            binding.msgWithMetadata.isChecked,
            binding.msgWithLock.isChecked,
            binding.msgBeQuiet.isChecked
        )

        rtmClient?.subscribe(channelName, options, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "subscribe channel $channelName success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(
                    LogLevel.ERROR,
                    "subscribe channel " + channelName + " failed, error code: " + errorInfo.errorCode
                )
            }
        })
    }

    private fun onClickUnsubscribeBtn() {
        log(LogLevel.API, "unsubscribe channel")
        val channelName = binding.msgChannelName.text.toString()
        rtmClient?.unsubscribe(channelName, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "unsubscribe channel $channelName success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickPublishBtn() {
        log(LogLevel.API, "publish channel message")
        val channelName = binding.msgChannelName.text.toString()
        val customType = binding.customType.text.toString()
        val storeInHistory = binding.msgStoreInHistory.isChecked()
        val options =
            PublishOptions(RtmConstants.RtmChannelType.MESSAGE, customType)
        val message = binding.channelMessage.text.toString()

        rtmClient?.publish(channelName, message, options, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "send message to channel $channelName success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickGetChannelMessages() {
//        log(LogLevel.API, "get history message")
//
//        val start =
//            if (binding.msgHistoryStart.text.isEmpty()) 0 else binding.msgHistoryStart.text.toString()
//                .toLong()
//        val end =
//            if (binding.msgHistoryEnd.text.isEmpty()) 0 else binding.msgHistoryEnd.text.toString()
//                .toLong()
//        val count =
//            if (binding.msgHistoryCount.text.isEmpty()) 0 else binding.msgHistoryCount.text.toString()
//                .toInt()
//        val options = GetHistoryMessagesOptions(count, start, end);
//        val channelName = binding.msgChannelName.text.toString()
//
//        rtmClient?.history!!.getMessages(
//            channelName,
//            RtmChannelType.MESSAGE,
//            options,
//            object : ResultCallback<GetMessagesResult> {
//                override fun onSuccess(result: GetMessagesResult) {
//                    log(LogLevel.CALLBACK, "get history messages success! new start: ${result.newStart}")
//                    for (message in result.messageList) {
//                        log(LogLevel.INFO, message.toString())
//                    }
//                }
//
//                override fun onFailure(errorInfo: ErrorInfo?) {
//                    log(LogLevel.ERROR, errorInfo.toString())
//                }
//            })
    }

    private fun onClickClearLogBtn() {
        logUtil!!.clearLog()
    }

    private fun log(level: LogLevel, message: String) {
        logUtil!!.log(level, message)
    }
}