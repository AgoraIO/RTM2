package io.agora.rtmdemo

import io.agora.rtm.RtmClient
import io.agora.utils.LogUtil

class RtmManager {
    var rtmClient: RtmClient? = null
    var appId: String? = null
    var token: String? = null
    val rtmEventListener: EventListener = EventListener()

    companion object {
        private val sInstance = RtmManager()
        fun the(): RtmManager {
            return sInstance
        }
    }

    fun setLogUtil(logUtil: LogUtil?) {
        rtmEventListener.logUtil = logUtil
    }
}