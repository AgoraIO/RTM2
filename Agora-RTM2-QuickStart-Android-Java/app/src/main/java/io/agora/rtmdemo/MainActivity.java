package io.agora.rtmdemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.LinkStateEvent;
import io.agora.rtm.MessageEvent;
import io.agora.rtm.PresenceEvent;
import io.agora.rtm.PublishOptions;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmConfig;
import io.agora.rtm.RtmEventListener;
import io.agora.rtm.SubscribeOptions;

public class MainActivity extends Activity {
    @BindView(R.id.appid)
    EditText mAppId;
    @BindView(R.id.token)
    EditText mToken;
    @BindView(R.id.uid)
    EditText mUserId;
    @BindView(R.id.channel_name)
    EditText mChannelName;
    @BindView(R.id.message)
    EditText mMessage;
    @BindView(R.id.log_scroll_view)
    ScrollView mLogScrollView;
    @BindView(R.id.log_text_view)
    TextView mLogTextView;

    // RTM 客户端实例
    private RtmClient mRtmClient;
    private Handler uiHandler;

    private RtmEventListener eventListener = new RtmEventListener() {
        @Override
        public void onMessageEvent(MessageEvent event) {
            printLog(event.toString());
        }

        @Override
        public void onPresenceEvent(PresenceEvent event) {
            printLog(event.toString());
        }

        @Override
        public void onLinkStateEvent(LinkStateEvent event) {
            printLog(event.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
        this.uiHandler = new Handler(Looper.getMainLooper());

        Random random = new Random();
        this.mUserId.setText("user_" + String.format("%04d", random.nextInt(10000)));
        this.mChannelName.setText("channelName");
        this.mMessage.setText("hello rtm2");
        String appid = getApplicationContext().getString(R.string.agora_app_id);
        if (!appid.contains("#")) {
            this.mAppId.setText(appid);
            this.mToken.setText(appid);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // 登录按钮
    @OnClick(R.id.login_button)
    void onLoginBtnClicked()
    {
        try {
            String userId = mUserId.getText().toString();
            if (userId == null || userId.isEmpty()) {
                showToast("invalid userId");
                return;
            }
            RtmConfig config = new RtmConfig.Builder(mAppId.getText().toString(), userId)
                    .eventListener(eventListener)
                    .build();
            mRtmClient = RtmClient.create(config);
        } catch (Exception e) {
            showToast("create rtm client is null");
        }

        if (mRtmClient == null) {
            showToast("rtm client is null");
            return;
        }

        // 登录 RTM 系统
        mRtmClient.login(mToken.getText().toString(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                printLog("login success!");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                CharSequence text = "login failed! " + errorInfo.toString();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        });
    }

    // 加入频道按钮
    @OnClick(R.id.join_button)
    void onJoinBtnClicked()
    {
        if (mRtmClient == null) {
            showToast("rtm client is null");
            return;
        }

        String channelName = mChannelName.getText().toString();
        SubscribeOptions options = new SubscribeOptions();
        mRtmClient.subscribe(channelName, options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                printLog("join channel success!");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                printLog("join channel failed! " + errorInfo.toString());
            }
        });
    }

    // 登出按钮
    @OnClick(R.id.logout_button)
    void onLogoutBtnClicked()
    {
        if (mRtmClient == null) {
            showToast("rtm client is null");
        }
        // 登出 RTM 系统
        mRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                printLog("logout success!");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                printLog("logout failed! " + errorInfo.toString());
            }
        });
    }

    // 离开频道按钮
    @OnClick(R.id.leave_button)
    void onLeaveBtnClicked()
    {
        if (mRtmClient == null) {
            showToast("rtm client is null");
            return;
        }
        String channelName = mChannelName.getText().toString();
        // 离开 RTM 频道
        mRtmClient.unsubscribe(channelName, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                printLog("leave channel success!");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                printLog("leave channel failed! " + errorInfo.toString());
            }
        });
    }

    // 发送频道消息按钮
    @OnClick(R.id.send_channel_msg_button)
    void onSendChannelMsgBtnClicked()
    {
        // 发送频道消息
        String message = mMessage.getText().toString();
        String channelName = mChannelName.getText().toString();
        PublishOptions options = new PublishOptions();
        options.setCustomType("");
        mRtmClient.publish(channelName, message, options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                printLog("send message success!");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                printLog("send message failed! " + errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.clear_log_btn)
    void onClearLogBtnClicked() {
        mLogTextView.setText("");
    }

    // 将消息记录写入 TextView
    public void printLog(String record) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder("[INFO] " + record + "\n");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        uiHandler.post(() -> {
            mLogTextView.append(spannableString + "\n");
            mLogScrollView.fullScroll(View.FOCUS_DOWN);
        });
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}