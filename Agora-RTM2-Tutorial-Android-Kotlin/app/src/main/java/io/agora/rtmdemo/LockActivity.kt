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
import io.agora.rtmdemo.databinding.ActivityLockBinding
import io.agora.utils.LogUtil
import io.agora.utils.RtmUtils.LockOperation
import io.agora.utils.RtmUtils.LogLevel

class LockActivity : Activity() {
    private lateinit var binding: ActivityLockBinding
    private var logUtil: LogUtil? = null
    private val rtmClient: RtmClient? = RtmManager.the().rtmClient
    private val lock: RtmLock? = RtmManager.the().rtmClient?.lock
    private var streamChannel: StreamChannel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.lockChannel.channelName.setText("channel1")
        binding.lockName.setText("test_lock")
        binding.lockTtl.setText("30")
        bindBtnClickListener()

        logUtil = LogUtil(binding.lockLog.logScrollView, binding.lockLog.logTextView)
        RtmManager.the().setLogUtil(logUtil)
    }

    override fun onDestroy() {
        super.onDestroy()
        RtmManager.the().setLogUtil(null)
    }

    private fun bindBtnClickListener() {
        binding.lockChannel.subscribeChannelBtn.setOnClickListener() { onClickSubscribeChannelBtn() }
        binding.setLockBtn.setOnClickListener() { onClickSetLockBtn() }
        binding.acquireLockBtn.setOnClickListener() { onClickAcquireLockBtn() }
        binding.removeLockBtn.setOnClickListener() { onClickRemoveLockBtn() }
        binding.releaseLockBtn.setOnClickListener() { onClickReleaseLockBtn() }
        binding.getLocksBtn.setOnClickListener() { onClickGetLockBtn() }
        binding.revokeLockBtn.setOnClickListener() { onClickRevokeLockBtn() }
        binding.lockLog.clearLogBtn.setOnClickListener() { onClickClearLogBtn() }
    }

    private fun onClickSubscribeChannelBtn() {
        val channelName = binding.lockChannel.channelName.text.toString()
        val channelType =
            if (binding.lockChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM

        if (channelType == RtmChannelType.MESSAGE) {
            rtmClient?.subscribe(
                channelName,
                SubscribeOptions(false, false, false, true, false),
                object : ResultCallback<Void?> {
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
                e.printStackTrace()
                showToast("create stream channel failed")
            }

            val options = JoinChannelOptions(
                RtmManager.the().token,
                false,
                false,
                true,
                false
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
    }

    private fun onClickSetLockBtn() {
        val channelName = binding.lockChannel.channelName.text.toString()
        val channelType =
            if (binding.lockChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM
        val lockName = binding.lockName.text.toString()
        val lockTTL = binding.lockTtl.text.toString()

        lock?.setLock(
            channelName,
            channelType,
            lockName,
            lockTTL.toLong(),
            object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    log(LogLevel.CALLBACK, "set lock($lockName) success")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
    }

    private fun onClickAcquireLockBtn() {
        val channelName = binding.lockChannel.channelName.text.toString()
        val channelType =
            if (binding.lockChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM
        val lockName = binding.lockName.text.toString()
        val retryAcquireLock = binding.retryAcquireLock.isChecked

        lock?.acquireLock(
            channelName,
            channelType,
            lockName,
            retryAcquireLock,
            object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    log(LogLevel.CALLBACK, "acquire lock($lockName) success")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
    }

    private fun onClickRemoveLockBtn() {
        val channelName = binding.lockChannel.channelName.text.toString()
        val channelType =
            if (binding.lockChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM
        val lockName = binding.lockName.text.toString()

        lock?.removeLock(channelName, channelType, lockName, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "remove lock($lockName) success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickReleaseLockBtn() {
        val channelName = binding.lockChannel.channelName.text.toString()
        val channelType =
            if (binding.lockChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM
        val lockName = binding.lockName.text.toString()

        lock?.releaseLock(channelName, channelType, lockName, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                log(LogLevel.CALLBACK, "release lock($lockName) success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickGetLockBtn() {
        val channelName = binding.lockChannel.channelName.text.toString()
        val channelType =
            if (binding.lockChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM

        lock?.getLocks(channelName, channelType, object : ResultCallback<ArrayList<LockDetail>> {
            override fun onSuccess(details: ArrayList<LockDetail>) {
                log(LogLevel.CALLBACK, "get channel($channelName) locks success")
                for (detail in details) {
                    log(LogLevel.INFO, detail.toString())
                }
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                log(LogLevel.ERROR, errorInfo.toString())
            }
        })
    }

    private fun onClickRevokeLockBtn() {
        val channelName = binding.lockChannel.channelName.text.toString()
        val channelType =
            if (binding.lockChannel.messageChannelRadioButton.isChecked) RtmChannelType.MESSAGE else RtmChannelType.STREAM
        val lockName = binding.lockName.text.toString()
        val owner = binding.ownerUserId.text.toString()

        lock?.revokeLock(
            channelName,
            channelType,
            lockName,
            owner,
            object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    log(LogLevel.CALLBACK, "revoke lock($lockName) success")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log(LogLevel.ERROR, errorInfo.toString())
                }
            })
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun onClickClearLogBtn() {
        logUtil!!.clearLog()
    }

    private fun log(level: LogLevel, message: String) {
        logUtil!!.log(level, message)
    }
}