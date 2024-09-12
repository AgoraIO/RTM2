package io.agora.rtmdemo

import io.agora.rtm.*
import io.agora.utils.LogUtil
import io.agora.utils.RtmUtils.LogLevel

class EventListener : RtmEventListener {
    var logUtil: LogUtil? = null

    override fun onLinkStateEvent(event: LinkStateEvent) {
        log(LogLevel.INFO, event.toString())
    }

    override fun onMessageEvent(event: MessageEvent) {
        log(LogLevel.INFO, event.toString())
    }

    override fun onPresenceEvent(event: PresenceEvent) {
        log(LogLevel.INFO, event.toString())
    }

    override fun onTopicEvent(event: TopicEvent) {
        log(LogLevel.INFO, event.toString())
    }

    override fun onLockEvent(event: LockEvent) {
        log(LogLevel.INFO, event.toString())
    }

    override fun onStorageEvent(event: StorageEvent) {
        log(LogLevel.INFO, event.toString())
    }

    override fun onConnectionStateChanged(
        channelName: String,
        state: RtmConstants.RtmConnectionState,
        reason: RtmConstants.RtmConnectionChangeReason
    ) {
        log(LogLevel.INFO,"onConnectionStateChanged, channel name: $channelName, state $state, reason $reason")
    }

    override fun onTokenPrivilegeWillExpire(channelName: String) {
        log(LogLevel.INFO,"onTokenPrivilegeWillExpire, channel name: $channelName")
    }

    private fun log(level: LogLevel, message: String) {
        logUtil?.log(level, message)
    }
}