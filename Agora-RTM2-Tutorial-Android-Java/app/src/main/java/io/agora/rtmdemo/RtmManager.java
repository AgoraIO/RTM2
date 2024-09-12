package io.agora.rtmdemo;

import io.agora.rtm.RtmClient;
import io.agora.utils.LogUtil;

public class RtmManager {
    private static final RtmManager sInstance = new RtmManager();
    private RtmClient mRtmClient;
    private final EventListener mRtmEventListener = new EventListener();
    private String mAppId;
    private String mToken;

    public static RtmManager the() {
        return sInstance;
    }

    public RtmClient getRtmClient() {
        return mRtmClient;
    }

    public EventListener getRtmEventListener() { return mRtmEventListener; }

    public String getAppId() {
        return mAppId;
    }

    public void setAppId(String appId) {
        mAppId = appId;
    }

    public String getToken() { return mToken; }

    public void setToken(String token) { mToken = token; }

    public void setRtmClient(RtmClient client) {
        mRtmClient = client;
    }

    public void setLogUtil(LogUtil logUtil) {
        mRtmEventListener.setLogUtil(logUtil);
    }
}
