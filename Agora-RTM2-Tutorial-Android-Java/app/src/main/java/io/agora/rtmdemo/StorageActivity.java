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
import android.os.Message;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.agora.rtm.JoinChannelOptions;
import io.agora.rtm.RtmConstants;
import io.agora.rtm.RtmConstants.RtmChannelType;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.Metadata;
import io.agora.rtm.MetadataItem;
import io.agora.rtm.MetadataOptions;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmStorage;
import io.agora.rtm.StreamChannel;
import io.agora.rtm.SubscribeOptions;
import io.agora.utils.LogUtil;
import io.agora.utils.RtmUtils;

public class StorageActivity extends Activity {
    private final String TAG = StorageActivity.class.getSimpleName();

    @BindView(R.id.logScrollView)
    ScrollView logScrollView;
    @BindView(R.id.logTextView)
    TextView logsTextView;
    @BindView(R.id.channel_type_radio_group)
    RadioGroup channelTypeRadioGroup;
    @BindView(R.id.stream_channel_radio_button)
    RadioButton streamChannelRadioButton;
    @BindView(R.id.message_channel_radio_button)
    RadioButton messageChannelRadioButton;
    @BindView(R.id.storage_type_radio_group)
    RadioGroup storageTypeRadioGroup;
    @BindView(R.id.channel_radio_button)
    RadioButton channelRadioButton;
    @BindView(R.id.user_radio_button)
    RadioButton userRadioButton;
    @BindView(R.id.channel_name)
    EditText mChannelName;
    @BindView(R.id.sub_storage_user_id)
    EditText mSubscribeUserId;
    @BindView(R.id.major_revision)
    EditText majorRevision;
    @BindView(R.id.item_key)
    EditText itemKey;
    @BindView(R.id.item_value)
    EditText itemValue;
    @BindView(R.id.item_revision)
    EditText itemRevision;

    private boolean mIsChannel = true;
    private RtmChannelType mChannelType = RtmChannelType.STREAM;
    private boolean mRecordTs = false;
    private boolean mRecordUserId = false;

    private LogUtil logUtil;
    private RtmClient mRtmClient = RtmManager.the().getRtmClient();
    private StreamChannel mStreamChannel;
    private RtmStorage mStorage = RtmManager.the().getRtmClient().getStorage();
    private Metadata mMetadata = new Metadata();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        logUtil = new LogUtil(logScrollView, logsTextView);
        RtmManager.the().setLogUtil(logUtil);

        setRadioGroupListener();
        itemRevision.setText("-1");
        majorRevision.setText("-1");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        RtmManager.the().setLogUtil(null);
    }

    protected void setRadioGroupListener() {
        channelTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

        storageTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.channel_radio_button:
                        mIsChannel = true;
                        break;
                    case R.id.user_radio_button:
                        mIsChannel = false;
                        break;
                    default:
                        mIsChannel = true;
                }
            }
        });
    }

    @OnClick(R.id.subscribe_channel_btn)
    void onSubscribeChannelMetadataBtnClicked() {
        String channelName = mChannelName.getText().toString();
        if (channelName == null || channelName.isEmpty()) {
            showToast("invalid channel name!");
            return;
        }
        if (mChannelType == RtmChannelType.MESSAGE) {
            SubscribeOptions options = new SubscribeOptions(false, false, true, false, false);
            mRtmClient.subscribe(channelName, options, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log(CALLBACK, "subscribe channel metadata success");
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

            mStreamChannel.join(new JoinChannelOptions(RtmManager.the().getToken(), false, true, false, false), new ResultCallback<Void>() {
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

    @OnClick(R.id.sub_user_metadata_btn)
    void onSubUserMetadataBtnClicked() {
        String userName = mSubscribeUserId.getText().toString();
        if (userName == null || userName.isEmpty()) {
            showToast("invalid subscribe user id!");
            return;
        }
        mStorage.subscribeUserMetadata(userName, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "subscribe user(" + userName + ") storage event success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.unsub_user_metadata_btn)
    void onUnsubUserMetadataBtnClicked() {
        String userName = mSubscribeUserId.getText().toString();
        if (userName == null || userName.isEmpty()) {
            showToast("invalid unsubscribe user id!");
            return;
        }
        mStorage.unsubscribeUserMetadata(userName, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "unsubscribe user(" + userName + ") storage event success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnCheckedChanged(R.id.record_ts)
    void onRecordTsCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        mRecordTs = isChecked;
    }

    @OnCheckedChanged(R.id.record_userId)
    void onRecordUserIdCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        mRecordUserId = isChecked;
    }

    @OnClick(R.id.add_item_btn)
    void onAddItemBtnClicked() {
        String key = itemKey.getText().toString();
        String value = itemValue.getText().toString();
        String revision = itemRevision.getText().toString();
        if (key.isEmpty()) {
            showToast("add invalid item");
            return;
        }
        mMetadata.getItems().add(new MetadataItem(key, value, Long.parseLong(revision)));
    }

    @OnClick(R.id.clear_items_btn)
    void onClearItemsBtnClicked() {
        if (mMetadata != null) {
            mMetadata.getItems().clear();
        }
    }

    @OnClick(R.id.get_items_btn)
    void onGetItemsBtnClicked() {
        if (mMetadata != null) {
            log(INFO, "major revision: " + mMetadata.getMajorRevision());
            for (MetadataItem item : mMetadata.getItems()) {
                log(INFO, item.toString());
            }
        }
    }

    @OnClick(R.id.set_metadata_btn)
    void onSetMetadataBtnClicked() {
        MetadataOptions options = new MetadataOptions(mRecordTs, mRecordUserId);
        options.setRecordTs(true);
        options.setRecordUserId(true);

        if (mIsChannel) {
            String channelName = mChannelName.getText().toString();
            if (channelName == null || channelName.isEmpty()) {
                showToast("invalid channel name!");
                return;
            }
            mStorage.setChannelMetadata(channelName, mChannelType, mMetadata, options, "", new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log(CALLBACK, "set channel metadata success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log(ERROR, errorInfo.toString());
                }
            });
        } else {
            String userName = mSubscribeUserId.getText().toString();
            if (userName == null || userName.isEmpty()) {
                showToast("invalid user id!");
                return;
            }
            mStorage.setUserMetadata(userName, mMetadata, options, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log(CALLBACK, "set user metadata success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log(ERROR, errorInfo.toString());
                }
            });
        }
    }

    @OnClick(R.id.update_metadata_btn)
    void onUpdateMetadataBtnClicked() {
        MetadataOptions options = new MetadataOptions(mRecordTs, mRecordUserId);

        if (mIsChannel) {
            String channelName = mChannelName.getText().toString();
            if (channelName == null || channelName.isEmpty()) {
                showToast("invalid channel name!");
                return;
            }
            mStorage.updateChannelMetadata(channelName, mChannelType, mMetadata, options, "", new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log(CALLBACK, "update channel metadata success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log(ERROR, errorInfo.toString());
                }
            });
        } else {
            String userName = mSubscribeUserId.getText().toString();
            if (userName == null || userName.isEmpty()) {
                showToast("invalid user id!");
                return;
            }
            mStorage.updateUserMetadata(userName, mMetadata, options, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log(CALLBACK, "update user metadata success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log(ERROR, errorInfo.toString());
                }
            });
        }
    }

    @OnClick(R.id.remove_metadata_btn)
    void onRemoveMetadataBtnClicked() {
        MetadataOptions options = new MetadataOptions(mRecordTs, mRecordUserId);

        if (mIsChannel) {
            String channelName = mChannelName.getText().toString();
            if (channelName == null || channelName.isEmpty()) {
                showToast("invalid channel name!");
                return;
            }
            mStorage.removeChannelMetadata(channelName, mChannelType, mMetadata, options, "", new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log(CALLBACK, "remove channel metadata success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log(ERROR, errorInfo.toString());
                }
            });
        } else {
            String userName = mSubscribeUserId.getText().toString();
            if (userName == null || userName.isEmpty()) {
                showToast("invalid user id!");
                return;
            }
            mStorage.removeUserMetadata(userName, mMetadata, options, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log(CALLBACK, "remove user metadata success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log(ERROR, errorInfo.toString());
                }
            });
        }
    }

    @OnClick(R.id.get_metadata_btn)
    void onGetMetadataBtnClicked() {
        if (mIsChannel) {
            String channelName = mChannelName.getText().toString();
            if (channelName == null || channelName.isEmpty()) {
                showToast("invalid channel name!");
                return;
            }
            mStorage.getChannelMetadata(channelName, mChannelType, new ResultCallback<Metadata>() {
                @Override
                public void onSuccess(Metadata data) {
                    log(CALLBACK, "get channel metadata success");
                    log(INFO, data.toString());
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log(ERROR, errorInfo.toString());
                }
            });
        } else {
            String userName = mSubscribeUserId.getText().toString();
            if (userName == null || userName.isEmpty()) {
                showToast("invalid user id!");
                return;
            }
            mStorage.getUserMetadata(userName, new ResultCallback<Metadata>() {
                @Override
                public void onSuccess(Metadata data) {
                    log(CALLBACK, "get user metadata success");
                    log(INFO, data.toString());
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log(ERROR, errorInfo.toString());
                }
            });
        }
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
