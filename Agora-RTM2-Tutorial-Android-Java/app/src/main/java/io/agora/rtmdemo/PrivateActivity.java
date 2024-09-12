package io.agora.rtmdemo;

import static io.agora.utils.RtmUtils.LogLevel.API;
import static io.agora.utils.RtmUtils.LogLevel.CALLBACK;
import static io.agora.utils.RtmUtils.LogLevel.ERROR;
import static io.agora.utils.RtmUtils.LogLevel.INFO;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import io.agora.rtm.ErrorInfo;
//import io.agora.rtm.GetHistoryMessagesOptions;
//import io.agora.rtm.GetMessagesResult;
//import io.agora.rtm.HistoryMessage;
import io.agora.rtm.PublishOptions;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;

import io.agora.rtm.RtmConstants;
import io.agora.rtm.RtmConstants.RtmChannelType;
import io.agora.utils.LogUtil;
import io.agora.utils.RtmUtils;

public class PrivateActivity extends Activity {
    private static final String TAG = PrivateActivity.class.getSimpleName();

    @BindView(R.id.logScrollView)
    ScrollView logScrollView;
    @BindView(R.id.logTextView)
    TextView logsTextView;

    @BindView(R.id.history_start)
    EditText mHistoryStart;
    @BindView(R.id.history_end)
    EditText mHistoryEnd;
    @BindView(R.id.history_count)
    EditText mHistoryCount;

    @BindView(R.id.peer_name)
    EditText mPeerName;
    @BindView(R.id.private_message)
    EditText mPrivateMessage;

    private RtmClient mRtmClient = RtmManager.the().getRtmClient();

    private LogUtil logUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
        mPrivateMessage.setText("hello rtm2");

        logUtil = new LogUtil(logScrollView, logsTextView);
        RtmManager.the().setLogUtil(logUtil);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        RtmManager.the().setLogUtil(null);
    }

    @OnClick(R.id.get_peer_messages_btn)
    void onGetMessagesBtnClicked() {
//        log(API, "get history messages");
//        long start = mHistoryStart.getText().toString().isEmpty() ? 0 : Long.parseLong(mHistoryStart.getText().toString());
//        long end = mHistoryEnd.getText().toString().isEmpty() ? 0 : Long.parseLong(mHistoryEnd.getText().toString());
//        int count = mHistoryCount.getText().toString().isEmpty() ? 0 : Integer.parseInt(mHistoryCount.getText().toString());
//        GetHistoryMessagesOptions options = new GetHistoryMessagesOptions(count, start, end);
//
//        String peer = mPeerName.getText().toString();
//        mRtmClient.getHistory().getMessages(peer, RtmChannelType.USER, options, new ResultCallback<GetMessagesResult>() {
//            @Override
//            public void onSuccess(GetMessagesResult result) {
//                log(CALLBACK, "get history messages! new start: " + result.getNewStart());
//                for (HistoryMessage message : result.getMessageList()) {
//                    log(INFO, message.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(ErrorInfo errorInfo) {
//                log(ERROR, errorInfo.toString());
//            }
//        });
    }

    @OnClick(R.id.send_private_message_btn)
    void onSendPrivateMessageBtnClicked() {
        log(API, "send private message");
        if (mRtmClient == null) {
            log(ERROR, "rtm client is null");
            return;
        }

        PublishOptions options = new PublishOptions();
        options.setCustomType("");
        options.setChannelType(RtmConstants.RtmChannelType.USER);
        Editable msg = this.mPrivateMessage.getText();
        String peer = mPeerName.getText().toString();
        if (peer.isEmpty()) {
            showToast("invalid peer name");
            return;
        }
        mRtmClient.publish(peer, msg.toString(), options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "send message to peer: " + peer + " success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.clear_log_btn)
    void onClearLogBtnClicked() {
        logUtil.clearLog();
    }

    private void log(RtmUtils.LogLevel level, String message) {
        logUtil.log(level, message);
    }
}