package io.agora.rtmdemo;

import static io.agora.utils.RtmUtils.LogLevel.API;
import static io.agora.utils.RtmUtils.LogLevel.CALLBACK;
import static io.agora.utils.RtmUtils.LogLevel.ERROR;
import static io.agora.utils.RtmUtils.LogLevel.INFO;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.agora.rtm.ChannelInfo;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.GetOnlineUsersOptions;
import io.agora.rtm.GetOnlineUsersResult;
import io.agora.rtm.JoinChannelOptions;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmConstants.RtmChannelType;
import io.agora.rtm.RtmPresence;
import io.agora.rtm.StreamChannel;
import io.agora.rtm.SubscribeOptions;
import io.agora.rtm.UserState;
import io.agora.utils.LogUtil;
import io.agora.utils.RtmUtils;

public class PresenceActivity extends Activity {
    private final String TAG = PresenceActivity.class.getSimpleName();

    @BindView(R.id.logScrollView)
    ScrollView logScrollView;
    @BindView(R.id.logTextView)
    TextView logsTextView;
    @BindView(R.id.channel_type_radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.stream_channel_radio_button)
    RadioButton streamChannelRadioButton;
    @BindView(R.id.message_channel_radio_button)
    RadioButton messageChannelRadioButton;
    @BindView(R.id.channel_name)
    EditText mChannelName;
    @BindView(R.id.where_now_user_id)
    EditText mQueryUserId;
    @BindView(R.id.state_key)
    EditText stateKey;
    @BindView(R.id.state_value)
    EditText stateValue;
    private HandlerThread mHandlerThread = new HandlerThread("handlerThread");
    private Handler mPresenceWorkHandler;
    private LogUtil logUtil;

    private RtmClient mRtmClient = RtmManager.the().getRtmClient();
    private RtmPresence mPresence = RtmManager.the().getRtmClient().getPresence();
    private StreamChannel mStreamChannel;
    private RtmChannelType mChannelType = RtmChannelType.STREAM;
    private boolean includeUserId = false;
    private boolean includeState = false;
    private HashMap<String, String> mStateItems = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        mHandlerThread.start();
        logUtil = new LogUtil(logScrollView, logsTextView);
        RtmManager.the().setLogUtil(logUtil);

        setRadioGroupListener();
    }

    protected void setRadioGroupListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.stream_channel_radio_button:
                        mChannelType = RtmChannelType.STREAM;
                        break;
                    case R.id.message_channel_radio_button:
                        mChannelType = RtmChannelType.MESSAGE;
                        break;
                    default:
                        mChannelType = RtmChannelType.STREAM;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RtmManager.the().setLogUtil(null);
    }

    @OnClick(R.id.subscribe_channel_btn)
    void onSubscribeChannelBtnClicked() {
        String channelName = mChannelName.getText().toString();
        if (channelName == null || channelName.isEmpty()) {
            showToast("invalid channel name!");
            return;
        }
        if (mChannelType == RtmChannelType.MESSAGE) {
            SubscribeOptions options = new SubscribeOptions(false, true, false, false, false);
            mRtmClient.subscribe(channelName, options, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log(CALLBACK, "subscribe channel(" + channelName + ")success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log(ERROR, errorInfo.toString());
                }
            });
        } else {
            try {
                mStreamChannel = mRtmClient.createStreamChannel(channelName.toString());
            } catch (Exception e) {
                log(ERROR, "create stream channel failed: " + e.toString());
            }

            JoinChannelOptions options = new JoinChannelOptions(RtmManager.the().getToken(), true, false, false, false);
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

    }

    @OnCheckedChanged(R.id.include_user_id)
    void onIncludeUserIdCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        includeUserId = isChecked;
    }

    @OnCheckedChanged(R.id.include_state)
    void onIncludeStateCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        includeState = isChecked;
    }

    @OnClick(R.id.who_now_btn)
    void onWhoNowBtnClicked() {
        String channelName = mChannelName.getText().toString();
        GetOnlineUsersOptions options = new GetOnlineUsersOptions(includeUserId, includeState);

        mPresence.getOnlineUsers(channelName, mChannelType, options, new ResultCallback<GetOnlineUsersResult>() {
            @Override
            public void onSuccess(GetOnlineUsersResult result) {
                log(CALLBACK, "get online user success");
                for (UserState state : result.getUserStateList()) {
                    log(INFO, "user id: " + state.getUserId());
                    state.getStates().forEach((key, value) -> {
                        log(INFO, "key: " + key + ", value: " + value);
                    });
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.where_now_btn)
    void onWhereNowBtnClicked() {
        String queryUserId = mQueryUserId.getText().toString();

        mPresence.getUserChannels(queryUserId, new ResultCallback<ArrayList<ChannelInfo>>() {
            @Override
            public void onSuccess(ArrayList<ChannelInfo> channels) {
                log(CALLBACK, "get " + queryUserId + " user channels success");
                for (ChannelInfo channel : channels) {
                    log(INFO, channel.toString());
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.add_state_item_btn)
    void onAddStateItemBtnClicked() {
        mStateItems.put(stateKey.getText().toString(), stateValue.getText().toString());
    }

    @OnClick(R.id.clear_state_item_btn)
    void onClearStateItemBtnClicked() {
        mStateItems.clear();
    }

    @OnClick(R.id.get_state_item_btn)
    void onGetStateItemBtnClicked() {
        log(INFO, "get current states");
        mStateItems.forEach((key, value) -> {
            log(INFO, "key: " + key + ", value: " + value);
        });
    }

    @OnClick(R.id.set_state_btn)
    void onSetStateBtnClicked() {
        String channelName = mChannelName.getText().toString();

        mPresence.setState(channelName, mChannelType, mStateItems, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "set state success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.remove_state_btn)
    void onRemoveStateBtnClicked() {
        String channelName = mChannelName.getText().toString();
        ArrayList<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> entry: mStateItems.entrySet()) {
            keys.add(entry.getKey());
        }

        mPresence.removeState(channelName, mChannelType, keys, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "remove state success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.get_state_btn)
    void onGetStateBtnClicked() {
        String channelName = mChannelName.getText().toString();
        String queryUserId = mQueryUserId.getText().toString();

        mPresence.getState(channelName, mChannelType, queryUserId, new ResultCallback<UserState>() {
            @Override
            public void onSuccess(UserState state) {
                log(CALLBACK, "get users(" + state.getUserId() + ") state success, " + state.toString());
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.clear_log_btn)
    void onClearLogBtnClicked() {
        logUtil.clearLog();
    }

    private void log(RtmUtils.LogLevel level, String message) {
        logUtil.log(level, message);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}

