package io.agora.rtmdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;


public class SelectionActivity extends Activity {
    private final String TAG = SelectionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.private_btn)
    void onPrivateBtnClicked() {
        runOnUiThread(() -> {
            Intent intent = new Intent(SelectionActivity.this, PrivateActivity.class);
            startActivity(intent);
        });
    }

    @OnClick(R.id.channel_btn)
    void onChannelBtnClicked() {
        runOnUiThread(() -> {
            Intent intent = new Intent(SelectionActivity.this, ChannelActivity.class);
            startActivity(intent);
        });
    }

    @OnClick(R.id.storage_btn)
    void onStorageBtnClicked() {
        runOnUiThread(() -> {
            Intent intent = new Intent(SelectionActivity.this, StorageActivity.class);
            startActivity(intent);
        });
    }

    @OnClick(R.id.lock_btn)
    void onLockBtnClicked() {
        runOnUiThread(() -> {
            Intent intent = new Intent(SelectionActivity.this, LockActivity.class);
            startActivity(intent);
        });
    }

    @OnClick(R.id.presence_btn)
    void onPresenceBtnClicked() {
        runOnUiThread(() -> {
            Intent intent = new Intent(SelectionActivity.this, PresenceActivity.class);
            startActivity(intent);
        });
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
