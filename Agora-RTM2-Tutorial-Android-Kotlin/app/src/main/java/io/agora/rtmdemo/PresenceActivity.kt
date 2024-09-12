package io.agora.rtmdemo

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import io.agora.rtm.*
import io.agora.rtm.RtmConstants.RtmChannelType
import io.agora.rtmdemo.databinding.ActivityPresenceBinding
import io.agora.utils.LogUtil
import io.agora.utils.RtmUtils.LogLevel
import io.agora.utils.RtmUtils.PresenceOperation
import java.util.*

class PresenceActivity : Activity() {
    private lateinit var binding: ActivityPresenceBinding
    private var logUtil: LogUtil? = null
    private val rtmClient: RtmClient? = RtmManager.the().rtmClient
    private val presence: RtmPresence? = RtmManager.the().rtmClient!!.presence
    private var streamChannel: StreamChannel? = null
    private val stateItems = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindBtnClickListener()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        logUtil = LogUtil(binding.presenceLog.logScrollView, binding.presenceLog.logTextView)
        RtmManager.the().setLogUtil(logUtil)
    }

    override fun onDestroy() {
        super.onDestroy()
        RtmManager.the().setLogUtil(null)
    }

    private fun bindBtnClickListener() {
        binding.presenceChannel.subscribeChannelBtn.setOnClickListener() { onClickSubscribeChannelBtn() }
        binding.whoNowBtn.setOnClickListener() { onClickWhoNowBtn() }
        binding.whereNowBtn.setOnClickListener() { onClickWhereNowBtn() }
        binding.addStateItemBtn.setOnClickListener() { onClickAddStateItemBtn() }
        binding.clearStateItemBtn.setOnClickListener() { onClickClearStateItemBtn() }
        binding.getStateItemBtn.setOnClickListener() { onClickGetStateItemBtn() }
        binding.setStateBtn.setOnClickListener() { onClickSetStateBtn() }
        binding.removeStateBtn.setOnClickListener() { onClickRemoveStateBtn() }
        binding.getStateBtn.setOnClickListener() { onClickGetStateBtn() }
        binding.presenceLog.clearLogBtn.setOnClickListener() { onClickClearLogBtn() }
    }

    private fun onClickSubscribeChannelBtn() {
        val channelName = binding.presenceChannel.channelName.text.toString()
        val selectMessageChannel = binding.presenceChannel.messageChannelRadioButton.isChecked
        if (selectMessageChannel) {
            val options = SubscribeOptions(false, true, false, false, false)
            rtmClient?.subscribe(channelName, options, object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    log(LogLevel.CALLBACK, "subscribe channel($channelName)success")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
        } else {
            try {
                streamChannel = rtmClient?.createStreamChannel(channelName)
            } catch (e: Exception) {
                log(LogLevel.ERROR, "create stream channel failed: $e")
            }
//            RtmManager.the().getRtmEventListener().setStreamChannel(mStreamChannel)
            val options = JoinChannelOptions(RtmManager.the().token, true, false, false, false)
            streamChannel?.join(options, object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    log(LogLevel.CALLBACK, "join stream channel $channelName success")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
        }
    }

    private fun onClickWhoNowBtn() {
        val channelName = binding.presenceChannel.channelName.text.toString()
        val options =
            GetOnlineUsersOptions(binding.includeUserId.isChecked, binding.includeState.isChecked)
        val channelType =
            if (binding.presenceChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM
        presence?.getOnlineUsers(
            channelName,
            channelType,
            options,
            object : ResultCallback<GetOnlineUsersResult> {
                override fun onSuccess(result: GetOnlineUsersResult) {
                    log(LogLevel.CALLBACK, "get online user success")
                    for (state in result.userStateList) {
                        log(LogLevel.INFO, "user id: " + state.userId)
                        state.states.forEach { (key: String, value: String) ->
                            log(
                                LogLevel.INFO,
                                "key: $key, value: $value"
                            )
                        }
                    }
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
    }

    private fun onClickWhereNowBtn() {
        val queryUserId = binding.whereNowUserId.text.toString()
        presence?.getUserChannels(queryUserId, object : ResultCallback<ArrayList<ChannelInfo>> {
            override fun onSuccess(channels: ArrayList<ChannelInfo>) {
                log(LogLevel.CALLBACK, "get $queryUserId user channels success")
                for (channel in channels) {
                    log(LogLevel.INFO, channel.toString())
                }
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickAddStateItemBtn() {
        stateItems[binding.stateKey.text.toString()] = binding.stateValue.text.toString()
    }

    private fun onClickClearStateItemBtn() {
        stateItems.clear()
    }

    private fun onClickGetStateItemBtn() {
        log(LogLevel.INFO, "get current states")
        stateItems.forEach { (key: String, value: String) ->
            log(LogLevel.INFO, "key: $key, value: $value")
        }
    }

    private fun onClickSetStateBtn() {
        val channelName = binding.presenceChannel.channelName.text.toString()
        val channelType =
            if (binding.presenceChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM
        presence?.setState(channelName, channelType, stateItems, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "set state success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickRemoveStateBtn() {
        val channelName = binding.presenceChannel.channelName.text.toString()
        val keys = ArrayList<String>()
        for ((key) in stateItems) {
            keys.add(key)
        }
        val channelType =
            if (binding.presenceChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM

        presence?.removeState(channelName, channelType, keys, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "remove state success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickGetStateBtn() {
        val channelName = binding.presenceChannel.channelName.text.toString()
        val channelType =
            if (binding.presenceChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM
        val queryUserId = binding.whereNowUserId.text.toString()

        presence?.getState(
            channelName,
            channelType,
            queryUserId,
            object : ResultCallback<UserState> {
                override fun onSuccess(state: UserState) {
                    log(
                        LogLevel.CALLBACK,
                        "get users(" + state.userId + ") state success, " + state.toString()
                    )
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