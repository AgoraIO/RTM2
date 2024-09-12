package io.agora.rtmdemo

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.widget.*
import io.agora.rtm.*
import io.agora.rtm.RtmConstants.RtmChannelType
import io.agora.rtmdemo.databinding.ActivityStorageBinding
import io.agora.utils.LogUtil
import io.agora.utils.RtmUtils.LogLevel
import io.agora.utils.RtmUtils.StorageOperation

class StorageActivity : Activity() {
    private lateinit var binding: ActivityStorageBinding
    private var logUtil: LogUtil? = null
    private val rtmClient: RtmClient? = RtmManager.the().rtmClient
    private val storage: RtmStorage? = RtmManager.the().rtmClient?.storage
    private val metadata: Metadata = Metadata()
    private var streamChannel: StreamChannel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.itemRevision.setText("-1")
        binding.majorRevision.setText("-1")
        bindBtnClickListener()

        logUtil = LogUtil(binding.storageLog.logScrollView, binding.storageLog.logTextView)
        RtmManager.the().setLogUtil(logUtil)
    }

    override fun onDestroy() {
        super.onDestroy()
        RtmManager.the().setLogUtil(null)
    }

    private fun bindBtnClickListener() {
        binding.storageChannel.subscribeChannelBtn.setOnClickListener() { onClickSubscribeChannelMetadataBtn() }
        binding.subUserMetadataBtn.setOnClickListener() { onClickSubUserMetadataBtn() }
        binding.unsubUserMetadataBtn.setOnClickListener() { onClickUnsubUserMetadataBtn() }
        binding.addItemBtn.setOnClickListener() { onClickAddItemBtn() }
        binding.clearItemsBtn.setOnClickListener() { onClickClearItemsBtn() }
        binding.getItemsBtn.setOnClickListener() { onClickGetItemsBtn() }
        binding.setMetadataBtn.setOnClickListener() { onClickSetMetadataBtn() }
        binding.updateMetadataBtn.setOnClickListener() { onClickUpdateMetadataBtn() }
        binding.removeMetadataBtn.setOnClickListener() { onClickRemoveMetadataBtn() }
        binding.getMetadataBtn.setOnClickListener() { onClickGetMetadataBtn() }
        binding.storageLog.clearLogBtn.setOnClickListener() { onClickClearLogBtn() }
    }

    private fun onClickSubscribeChannelMetadataBtn() {
        val channelName = binding.storageChannel.channelName.text.toString()
        val channelType =
            if (binding.storageChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM

        if (channelType == RtmChannelType.MESSAGE) {
            val options = SubscribeOptions(false, false, true, false, false)
            rtmClient?.subscribe(channelName, options, object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    log(LogLevel.CALLBACK, "subscribe channel metadata success")
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
            streamChannel!!.join(
                JoinChannelOptions(
                    RtmManager.the().token,
                    false,
                    true,
                    false,
                    false
                ), object : ResultCallback<Void?> {
                    override fun onSuccess(responseInfo: Void?) {
                        log(LogLevel.CALLBACK, "join stream channel $channelName success")
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        log(LogLevel.ERROR, errorInfo.toString())
                    }
                })
        }
    }

    private fun onClickSubUserMetadataBtn() {
        val userName = binding.subStorageUserId.text.toString()

        storage?.subscribeUserMetadata(userName, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "subscribe user($userName) storage event success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickUnsubUserMetadataBtn() {
        val userName = binding.subStorageUserId.text.toString()

        storage?.unsubscribeUserMetadata(userName, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "unsubscribe user($userName) storage event success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickAddItemBtn() {
        val key = binding.itemKey.text.toString()
        val value = binding.itemValue.text.toString()
        val revision = binding.itemRevision.text.toString()
        metadata.items.add(MetadataItem(key, value, revision.toLong()))
    }

    private fun onClickClearItemsBtn() {
        metadata.items?.clear()
    }

    private fun onClickGetItemsBtn() {
        log(LogLevel.INFO, "major revision: " + metadata.majorRevision)
        for (item in metadata.items) {
            log(LogLevel.INFO, item.toString())
        }
    }

    private fun onClickSetMetadataBtn() {
        val options = MetadataOptions(binding.recordTs.isChecked, binding.recordUserId.isChecked)
        val isChannelMetadata = binding.channelRadioButton.isChecked
        if (isChannelMetadata) {
            val channelName = binding.storageChannel.channelName.text.toString()
            val channelType =
                if (binding.storageChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM
            storage?.setChannelMetadata(
                channelName,
                channelType,
                metadata,
                options,
                "",
                object : ResultCallback<Void?> {
                    override fun onSuccess(responseInfo: Void?) {
                        log(LogLevel.CALLBACK, "set channel metadata success")
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        log(LogLevel.ERROR, errorInfo.toString())
                    }
                })
        } else {
            val userName = binding.subStorageUserId.text.toString()

            storage?.setUserMetadata(userName, metadata, options, object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    log(LogLevel.CALLBACK, "set user metadata success")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
        }
    }

    private fun onClickUpdateMetadataBtn() {
        val options = MetadataOptions(binding.recordTs.isChecked, binding.recordUserId.isChecked)
        val isChannelMetadata = binding.channelRadioButton.isChecked
        if (isChannelMetadata) {
            val channelName = binding.storageChannel.channelName.text.toString()
            val channelType =
                if (binding.storageChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM

            storage?.updateChannelMetadata(
                channelName,
                channelType,
                metadata,
                options,
                "",
                object : ResultCallback<Void?> {
                    override fun onSuccess(responseInfo: Void?) {
                        log(LogLevel.CALLBACK, "update channel metadata success")
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        log(LogLevel.ERROR, errorInfo.toString())
                    }
                })
        } else {
            val userName = binding.subStorageUserId.text.toString()

            storage?.updateUserMetadata(
                userName,
                metadata,
                options,
                object : ResultCallback<Void?> {
                    override fun onSuccess(responseInfo: Void?) {
                        log(LogLevel.CALLBACK, "update user metadata success")
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        log(LogLevel.ERROR, errorInfo.toString())
                    }
                })
        }
    }

    private fun onClickRemoveMetadataBtn() {
        val options = MetadataOptions(binding.recordTs.isChecked, binding.recordUserId.isChecked)
        val isChannelMetadata = binding.channelRadioButton.isChecked
        if (isChannelMetadata) {
            val channelName = binding.storageChannel.channelName.text.toString()
            val channelType =
                if (binding.storageChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM
            storage?.removeChannelMetadata(
                channelName,
                channelType,
                metadata,
                options,
                "",
                object : ResultCallback<Void?> {
                    override fun onSuccess(responseInfo: Void?) {
                        log(LogLevel.CALLBACK, "remove channel metadata success")
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        log(LogLevel.ERROR, errorInfo.toString())
                    }
                })
        } else {
            val userName = binding.subStorageUserId.text.toString()

            storage?.removeUserMetadata(
                userName,
                metadata,
                options,
                object : ResultCallback<Void?> {
                    override fun onSuccess(responseInfo: Void?) {
                        log(LogLevel.CALLBACK, "remove user metadata success")
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        log(LogLevel.ERROR, errorInfo.toString())
                    }
                })
        }
    }

    private fun onClickGetMetadataBtn() {
        val isChannelMetadata = binding.channelRadioButton.isChecked
        if (isChannelMetadata) {
            val channelName = binding.storageChannel.channelName.text.toString()
            val channelType =
                if (binding.storageChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM

            storage?.getChannelMetadata(
                channelName,
                channelType,
                object : ResultCallback<Metadata> {
                    override fun onSuccess(data: Metadata) {
                        log(LogLevel.CALLBACK, "get channel metadata success")
                        log(LogLevel.INFO, data.toString())
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        log(LogLevel.ERROR, errorInfo.toString())
                    }
                })
        } else {
            val userName = binding.subStorageUserId.text.toString()

            storage?.getUserMetadata(userName, object : ResultCallback<Metadata> {
                override fun onSuccess(data: Metadata) {
                    log(LogLevel.CALLBACK, "get user metadata success")
                    log(LogLevel.INFO, data.toString())
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
        }
    }

    private fun onClickClearLogBtn() {
        logUtil!!.clearLog()
    }

    private fun log(level: LogLevel, message: String) {
        logUtil!!.log(level, message)
    }
}