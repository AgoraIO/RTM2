package io.agora.rtmdemo;

import static io.agora.utils.RtmUtils.LogLevel.INFO;

import io.agora.rtm.LinkStateEvent;
import io.agora.rtm.LockEvent;
import io.agora.rtm.MessageEvent;
import io.agora.rtm.PresenceEvent;
import io.agora.rtm.RtmConstants.RtmConnectionState;
import io.agora.rtm.RtmConstants.RtmConnectionChangeReason;
import io.agora.rtm.RtmEventListener;
import io.agora.rtm.StorageEvent;
import io.agora.rtm.TopicEvent;
import io.agora.utils.LogUtil;
import io.agora.utils.RtmUtils.LogLevel;

public class EventListener implements RtmEventListener {
    private LogUtil mLogUtil;

    public EventListener() {
    }

    public void setLogUtil(LogUtil logUtils) {
        mLogUtil = logUtils;
    }

    private void log(LogLevel level, String message) {
        if (mLogUtil != null) {
            mLogUtil.log(level, message);
        }
    }

    @Override
    public void onLinkStateEvent(LinkStateEvent event) { log(INFO, event.toString()); }
    @Override
    public void onMessageEvent(MessageEvent event) {
        log(INFO, event.toString());
    }

    @Override
    public void onPresenceEvent(PresenceEvent event) {
        log(INFO, event.toString());
    }

    @Override
    public void onTopicEvent(TopicEvent event) {
        log(INFO, event.toString());
    }

    @Override
    public void onLockEvent(LockEvent event) {
        log(INFO, event.toString());
    }

    @Override
    public void onStorageEvent(StorageEvent event) {
        log(INFO, event.toString());
    }

    @Override
    public void onConnectionStateChanged(String channelName, RtmConnectionState state, RtmConnectionChangeReason reason) {
        log(INFO, "onConnectionStateChanged, channel name: " + channelName + ", state " + state + ", reason " + reason);
    }

    @Override
    public void onTokenPrivilegeWillExpire(String channelName) {
        log(INFO, "onTokenPrivilegeWillExpire, channel name: " + channelName);
    }
}
