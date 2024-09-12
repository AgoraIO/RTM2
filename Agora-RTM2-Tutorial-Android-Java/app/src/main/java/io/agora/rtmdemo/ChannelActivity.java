package io.agora.rtmdemo;

import static io.agora.utils.RtmUtils.LogLevel.API;
import static io.agora.utils.RtmUtils.LogLevel.CALLBACK;
import static io.agora.utils.RtmUtils.LogLevel.ERROR;
import static io.agora.utils.RtmUtils.LogLevel.INFO;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import io.agora.rtm.ErrorInfo;
//import io.agora.rtm.GetHistoryMessagesOptions;
//import io.agora.rtm.GetMessagesResult;
//import io.agora.rtm.HistoryMessage;
import io.agora.rtm.PublishOptions;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmConstants;
import io.agora.rtm.RtmConstants.RtmMessageQos;
import io.agora.rtm.StreamChannel;
import io.agora.rtm.SubscribeOptions;
import io.agora.rtm.SubscribeTopicResult;
import io.agora.rtm.TopicMessageOptions;
import io.agora.rtm.TopicOptions;
import io.agora.rtm.JoinChannelOptions;
import io.agora.rtm.JoinTopicOptions;

import io.agora.utils.LogUtil;
import io.agora.utils.RtmUtils;

public class ChannelActivity extends Activity {
    private static final String TAG = ChannelActivity.class.getSimpleName();
    @BindView(R.id.stream_channel_name)
    EditText mStreamChannelName;
    @BindView(R.id.topic_name)
    EditText mTopicName;
    @BindView(R.id.subscribe_user_id)
    EditText mSubscribeUser;
    @BindView(R.id.topic_message)
    EditText mTopicMessage;
    @BindView(R.id.msg_channel_name)
    EditText mMessageChannelName;
    @BindView(R.id.channel_message)
    EditText mChannelMessage;
    @BindView(R.id.custom_type)
    EditText mCustomType;
    @BindView(R.id.msg_history_start)
    EditText mHistoryStart;
    @BindView(R.id.msg_history_end)
    EditText mHistoryEnd;
    @BindView(R.id.msg_history_count)
    EditText mHistoryCount;
    @BindView(R.id.logScrollView)
    ScrollView logScrollView;
    @BindView(R.id.logTextView)
    TextView logsTextView;

    private final RtmClient mRtmClient = RtmManager.the().getRtmClient();
    private StreamChannel mStreamChannel;
    private boolean stWithMetadata = false;
    private boolean stWithLock = false;
    private boolean stWithPresence = false;
    private boolean stBeQuiet = false;
    private boolean msgWithMessage = false;
    private boolean msgWithMetadata = false;
    private boolean msgWithLock = false;
    private boolean msgWithPresence = false;
    private boolean msgBeQuiet = false;
    private boolean msgStoreInHistory = false;

    private LogUtil logUtil;

    private ArrayList<String> mSubscribeUserList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        this.mStreamChannelName.setText("stream_channel");
        this.mTopicName.setText("topic_name");
        this.mSubscribeUser.setText("user2");
        this.mTopicMessage.setText("hello rtm2");
        this.mMessageChannelName.setText("message_channel");
        this.mChannelMessage.setText("hello rtm2");
        this.mCustomType.setText("");

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

    @OnClick(R.id.join_channel_btn)
    void onJoinChannelBtnClicked() {
        log(API, "join channel");
        if (mRtmClient == null) {
            log(ERROR, "rtm client not initialized");
            return;
        }

        Editable channelName = this.mStreamChannelName.getText();

        try {
            mStreamChannel = mRtmClient.createStreamChannel(channelName.toString());
        } catch (Exception e) {
            log(ERROR, "create stream channel exception: " + e.toString());
        }

        if (mStreamChannel == null) {
            log(ERROR, "create stream channel failed");
            return;
        }

        JoinChannelOptions options = new JoinChannelOptions(RtmManager.the().getToken(),
                stWithPresence, stWithMetadata, stWithLock, stBeQuiet);
        mStreamChannel.join(options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "join stream channel " + channelName.toString() + " success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.leave_channel_btn)
    void onLeaveChannelBtnClicked() {
        log(API, "leave channel");

        if (mStreamChannel == null) {
            log(ERROR, "stream channel is null");
            return;
        }

        mStreamChannel.leave(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "leave stream channel success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.join_topic_btn)
    void onJoinTopicBtnClicked() {
        log(API, "join topic");
        if (mStreamChannel == null) {
            log(ERROR, "stream channel is null");
            return;
        }

        Editable topicName = this.mTopicName.getText();
        JoinTopicOptions options = new JoinTopicOptions(RtmMessageQos.ORDERED, RtmConstants.RtmMessagePriority.HIGH, false, "test topic meta");

        mStreamChannel.joinTopic(topicName.toString(), options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "join topic(" + topicName.toString() + ") success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.send_topic_message_btn)
    void onSendMessageBtnClicked() {
        log(API, "send message");
        if (mStreamChannel == null) {
            log(ERROR, "stream channel is null");
            return;
        }

        Editable topicName = this.mTopicName.getText();
        Editable msg = this.mTopicMessage.getText();
        TopicMessageOptions options = new TopicMessageOptions();
        options.setCustomType("");
        mStreamChannel.publishTopicMessage(topicName.toString(), msg.toString(), options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "publish topic message success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.leave_topic_btn)
    void onLeaveTopicBtnClicked() {
        log(API, "leave topic");
        if (mStreamChannel == null) {
            log(ERROR, "stream channel is null");
            return;
        }
        Editable topicName = this.mTopicName.getText();

        mStreamChannel.leaveTopic(topicName.toString(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "leave topic(" + topicName.toString() + ") success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.add_user_to_list_btn)
    void onAddUserBtnClicked() {
        Editable userId = this.mSubscribeUser.getText();
        if (TextUtils.isEmpty(userId)) {
            showToast("Please enter user id!");
            return;
        }
        mSubscribeUserList.add(userId.toString());
    }

    @OnClick(R.id.clear_user_btn)
    void onClearUserBtnClicked() {
        mSubscribeUserList.clear();
    }

    @OnClick(R.id.subscribe_topic_btn)
    void onSubscribeTopicBtnClicked() {
        log(API, "subscribe topic");
        if (mStreamChannel == null) {
            log(ERROR, "stream channel is null");
            return;
        }

        TopicOptions options = new TopicOptions();
        options.setUsers(mSubscribeUserList);
        Editable topicName = this.mTopicName.getText();

        mStreamChannel.subscribeTopic(topicName.toString(), options, new ResultCallback<SubscribeTopicResult>() {
            @Override
            public void onSuccess(SubscribeTopicResult responseInfo) {
                log(CALLBACK, "subscribe topic(" + topicName.toString() + ") success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.unsubscribe_topic_btn)
    void onUnsubscribeTopicBtnClicked() {
        log(API, "unsubscribe topic");
        if (mStreamChannel == null) {
            log(ERROR, "stream channel is null");
            return;
        }
        TopicOptions options = new TopicOptions();
        options.setUsers(mSubscribeUserList);

        Editable topicName = this.mTopicName.getText();
        if (TextUtils.isEmpty(topicName)) {
            showToast("Please enter topicName!");
            return;
        }
        mStreamChannel.unsubscribeTopic(topicName.toString(), options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "unsubscribe topic success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.get_topic_user_list_btn)
    void onGetSubscribeUserListBtnClicked() {
        log(API, "get subscribe user list");
        if (mStreamChannel == null) {
            log(ERROR, "stream channel is null");
            return;
        }

        Editable topicName = this.mTopicName.getText();
        if (TextUtils.isEmpty(topicName)) {
            showToast("Please enter topicName!");
            return;
        }

        try {
            mStreamChannel.getSubscribedUserList(topicName.toString(), new ResultCallback<ArrayList<String>>() {
                @Override
                public void onSuccess(ArrayList<String> userList) {
                    for (String user : userList) {
                        log(INFO, "subscribed user: " + user);
                    }
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log(ERROR, errorInfo.toString());
                }
            });
        } catch (Exception e) {
            log(ERROR, "get subscribe user list failed! " + e.toString());
        }
    }

    @OnCheckedChanged(R.id.st_with_metadata)
    void onStreamMetadataCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        stWithMetadata = isChecked;
    }

    @OnCheckedChanged(R.id.st_with_lock)
    void onStreamLockCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        stWithLock = isChecked;
    }

    @OnCheckedChanged(R.id.st_with_presence)
    void onStreamPresenceCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        stWithPresence = isChecked;
    }

    @OnCheckedChanged(R.id.st_be_quiet)
    void onStreamBeQuietCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        stBeQuiet = isChecked;
    }

    // message channel
    @OnCheckedChanged(R.id.msg_with_message)
    void onMessageCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        msgWithMessage = isChecked;
    }

    @OnCheckedChanged(R.id.msg_with_metadata)
    void onMsgMetadataCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        msgWithMetadata = isChecked;
    }

    @OnCheckedChanged(R.id.msg_with_lock)
    void onMsgLockCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        msgWithLock = isChecked;
    }

    @OnCheckedChanged(R.id.msg_with_presence)
    void onMsgPresenceCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        msgWithPresence = isChecked;
    }

    @OnCheckedChanged(R.id.msg_be_quiet)
    void onMsgQuietCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        msgBeQuiet = isChecked;
    }

    @OnCheckedChanged(R.id.msg_store_in_history)
    void onMsgStoreInHistoryCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        msgStoreInHistory = isChecked;
    }

    @OnClick(R.id.subscribe_btn)
    void onSubscribeBtnClicked() {
        log(API, "subscribe channel");
        if (mRtmClient == null) {
            log(ERROR, "rtm client not initialized");
            return;
        }
        String channelName = mMessageChannelName.getText().toString();

        SubscribeOptions options = new SubscribeOptions(msgWithMessage, msgWithPresence, msgWithMetadata, msgWithLock, msgBeQuiet);
        mRtmClient.subscribe(channelName, options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "subscribe channel " + channelName + " success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, "subscribe channel " + channelName + " failed, error code: " + errorInfo.getErrorCode());
            }
        });
    }

    @OnClick(R.id.unsubscribe_btn)
    void onUnsubscribeBtnClicked() {
        log(API, "unsubscribe channel");
        if (mRtmClient == null) {
            log(ERROR, "rtm client not initialized");
            return;
        }
        String channelName = mMessageChannelName.getText().toString();

        mRtmClient.unsubscribe(channelName, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "unsubscribe channel " + channelName + " success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.publish_btn)
    void onPublishBtnClicked() {
        log(API, "publish channel message");
        if (mRtmClient == null) {
            log(ERROR, "rtm client not initialized");
            return;
        }
        String channelName = mMessageChannelName.getText().toString();
        String customType = mCustomType.getText().toString();
        PublishOptions options = new PublishOptions();
        options.setCustomType(customType);
//        options.setStoreInHistory(msgStoreInHistory);
        Editable msg = this.mChannelMessage.getText();
        mRtmClient.publish(channelName, msg.toString(), options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "send message to channel " + channelName + " success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.get_channel_messages_btn)
    void onGetChannelHistoryMessages() {
//        log(API, "get channel history messages");
//        if (mRtmClient == null) {
//            log(ERROR, "rtm client not initialized");
//            return;
//        }

//        long start = mHistoryStart.getText().toString().isEmpty() ? 0 : Long.parseLong(mHistoryStart.getText().toString());
//        long end = mHistoryEnd.getText().toString().isEmpty() ? 0 : Long.parseLong(mHistoryEnd.getText().toString());
//        int count = mHistoryCount.getText().toString().isEmpty()? 0 : Integer.parseInt(mHistoryCount.getText().toString());
//        GetHistoryMessagesOptions options = new GetHistoryMessagesOptions(count, start, end);
//        String channelName = mMessageChannelName.getText().toString();
//
//        mRtmClient.getHistory().getMessages(channelName, RtmConstants.RtmChannelType.MESSAGE, options, new ResultCallback<GetMessagesResult>() {
//            @Override
//            public void onSuccess(GetMessagesResult result) {
//                log(CALLBACK, "get channel history messages success! new start: " + result.getNewStart());
//                for (HistoryMessage message : result.getMessageList()) {
//                    log(INFO, message.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(ErrorInfo errorInfo) { log(ERROR, errorInfo.toString()); }
//        });
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