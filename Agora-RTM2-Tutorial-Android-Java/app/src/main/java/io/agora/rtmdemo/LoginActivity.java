package io.agora.rtmdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.EnumSet;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.rtm.RtmClient;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmConfig;
import io.agora.rtm.RtmConstants;
import io.agora.rtm.RtmLogConfig;
import io.agora.rtm.RtmProxyConfig;

public class LoginActivity extends Activity {
    private final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.user_id)
    EditText mUserIdEditText;
    @BindView(R.id.app_id)
    EditText mAppIdEditText;

    @BindView(R.id.token)
    EditText mTokenEditText;

    @BindView(R.id.login_rtm_btn)
    TextView mLoginBtn;

    private String mUserId;
    private RtmClient mRtmClient;
    private boolean mIsInChat = false;
    private final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_NETWORK_STATE}, REQUEST_CODE);
        }
        Random random = new Random();
        mUserIdEditText.setText("user_" + String.format("%04d", random.nextInt(10000)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RtmClient.release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runOnUiThread(() -> {
                    showToast("request permission success!");
                });
            } else {
                runOnUiThread(() -> {
                    showToast("request permission failed!");
                });
            }
        }
    }

    @OnClick(R.id.login_rtm_btn)
    public void onClickLogin(View v) {
        String appId = mAppIdEditText.getText().toString();
        if (TextUtils.isEmpty(appId)) {
            appId = getApplicationContext().getString(R.string.agora_app_id);
        }
        String token = mTokenEditText.getText().toString();
        if (TextUtils.isEmpty(token)) {
            token = appId;
        }
        RtmManager.the().setAppId(appId);
        RtmManager.the().setToken(token);
        mUserId = mUserIdEditText.getText().toString();
        if (TextUtils.isEmpty(mUserId)) {
            showToast("userId is empty");
        } else {
            mLoginBtn.setEnabled(false);
            createRtmClient(mUserId);
            doLogin();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginBtn.setEnabled(true);
        if (mIsInChat) {
            doLogout();
        }
    }

    private void createRtmClient(String userId) {

        try {
            String appId = RtmManager.the().getAppId();

            RtmProxyConfig proxyConfig = new RtmProxyConfig();
            RtmLogConfig logConfig = new RtmLogConfig();
//            logConfig.setFilePath(""); // your log path
//            logConfig.setFileSize(10*1024); // log file size
//            logConfig.setLevel(RtmLogLevel.INFO);

            RtmConfig rtmConfig = new RtmConfig.Builder(appId, userId)
                    .areaCode(EnumSet.of(RtmConstants.RtmAreaCode.AS, RtmConstants.RtmAreaCode.CN))
                    .eventListener(RtmManager.the().getRtmEventListener())
                    .proxyConfig(proxyConfig)
                    .logConfig(logConfig)
                    .build();
            mRtmClient = RtmClient.create(rtmConfig);
            mRtmClient.setParameters("{\"rtm.log_filter\": 2063}"); // 0x080f

            RtmManager.the().setRtmClient(mRtmClient);
        } catch (Exception e) {
            showToast( e.toString());
        }
    }

    private void doLogin() {
        if (mRtmClient == null) {
            showToast("rtm client is null");
            return;
        }
        mIsInChat = true;
        String token = RtmManager.the().getToken();
        mRtmClient.login(token, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                runOnUiThread(() -> {
                    showToast("login success");
                    Intent intent = new Intent(LoginActivity.this, SelectionActivity.class);
                    startActivity(intent);
                });
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                runOnUiThread(() -> {
                    mLoginBtn.setEnabled(true);
                    mIsInChat = false;
                    showToast(errorInfo.toString());
                });
            }
        });
    }

    private void doLogout() {
        if (mRtmClient == null) {
            showToast("rtm client is null");
            return;
        }
        mRtmClient.logout(null);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
