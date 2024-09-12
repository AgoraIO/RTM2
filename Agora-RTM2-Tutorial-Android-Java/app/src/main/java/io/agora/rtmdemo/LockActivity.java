package io.agora.rtmdemo;

import static io.agora.utils.RtmUtils.LogLevel.API;
import static io.agora.utils.RtmUtils.LogLevel.CALLBACK;
import static io.agora.utils.RtmUtils.LogLevel.ERROR;
import static io.agora.utils.RtmUtils.LogLevel.INFO;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.JoinChannelOptions;
import io.agora.rtm.LockDetail;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmConstants.RtmChannelType;
import io.agora.rtm.RtmLock;
import io.agora.rtm.StreamChannel;
import io.agora.rtm.SubscribeOptions;
import io.agora.utils.LogUtil;
import io.agora.utils.RtmUtils;

public class LockActivity extends Activity {
    private final String TAG = LockActivity.class.getSimpleName();

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
    @BindView(R.id.lock_name)
    EditText mLockName;
    @BindView(R.id.lock_ttl)
    EditText mLockTTL;
    @BindView(R.id.owner_user_id)
    EditText mLockOwner;

    private LogUtil logUtil;

    private RtmClient mRtmClient = RtmManager.the().getRtmClient();
    private RtmLock mLock = RtmManager.the().getRtmClient().getLock();
    private StreamChannel mStreamChannel;
    private RtmChannelType mChannelType = RtmChannelType.STREAM;
    private boolean retryAcquireLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        this.mChannelName.setText("channelName");
        this.mLockName.setText("lockName");
        this.mLockTTL.setText("30");

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
            mRtmClient.subscribe(channelName, new SubscribeOptions(false, false, false, true, false), new ResultCallback<Void>() {
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
                e.printStackTrace();
                showToast("create stream channel failed");
            }

            mStreamChannel.join(new JoinChannelOptions(RtmManager.the().getToken(), false, false, true, false), new ResultCallback<Void>() {
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

    @OnClick(R.id.set_lock_btn)
    void onSetLockBtnClicked() {
        String channelName = mChannelName.getText().toString();
        String lockName = mLockName.getText().toString();
        String lockTTL = mLockTTL.getText().toString();

        mLock.setLock(channelName, mChannelType, lockName, Long.parseLong(lockTTL), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "set lock(" + lockName + ") success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnCheckedChanged(R.id.retry_acquire_lock)
    void onRetryAcquireCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        retryAcquireLock = isChecked;
    }

    @OnClick(R.id.acquire_lock_btn)
    void onAcquireLockBtnClicked() {
        String channelName = mChannelName.getText().toString();
        String lockName = mLockName.getText().toString();

        mLock.acquireLock(channelName, mChannelType, lockName, retryAcquireLock, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "acquire lock(" + lockName + ") success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.remove_lock_btn)
    void onRemoveLockBtnClicked() {
        String channelName = mChannelName.getText().toString();
        String lockName = mLockName.getText().toString();

        mLock.removeLock(channelName, mChannelType, lockName, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "remove lock(" + lockName + ") success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.release_lock_btn)
    void onReleaseLockBtnClicked() {
        String channelName = mChannelName.getText().toString();
        String lockName = mLockName.getText().toString();

        mLock.releaseLock(channelName, mChannelType, lockName, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "release lock(" + lockName + ") success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.get_locks_btn)
    void onGetLockBtnClicked() {
        String channelName = mChannelName.getText().toString();

        mLock.getLocks(channelName, mChannelType, new ResultCallback<ArrayList<LockDetail>>() {
            @Override
            public void onSuccess(ArrayList<LockDetail> details) {
                log(CALLBACK, "get channel(" + channelName + ") locks success");

                for (LockDetail detail : details) {
                    log(INFO, detail.toString());
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log(ERROR, errorInfo.toString());
            }
        });
    }

    @OnClick(R.id.revoke_lock_btn)
    void onRevokeLockBtnClicked() {
        String channelName = mChannelName.getText().toString();
        String lockName = mLockName.getText().toString();
        String owner = mLockOwner.getText().toString();

        mLock.revokeLock(channelName, mChannelType, lockName, owner, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log(CALLBACK, "revoke lock(" + lockName + ") success");
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

