package io.agora.rtmdemo

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtmdemo.databinding.ActivitySelectionBinding

class SelectionActivity : Activity() {
    private lateinit var binding: ActivitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        bindBtnClickListener()
    }

    private fun bindBtnClickListener() {
        binding.privateBtn.setOnClickListener { onClickPrivateBtn() }
        binding.channelBtn.setOnClickListener { onClickChannelBtn() }
        binding.storageBtn.setOnClickListener { onClickStorageBtn() }
        binding.lockBtn.setOnClickListener { onClickLockBtn() }
        binding.presenceBtn.setOnClickListener { onClickPresenceBtn() }
    }

    private fun onClickPrivateBtn() {
        runOnUiThread {
            val intent = Intent(this@SelectionActivity, PrivateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onClickChannelBtn() {
        runOnUiThread {
            val intent = Intent(this@SelectionActivity, ChannelActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onClickStorageBtn() {
        runOnUiThread {
            val intent = Intent(this@SelectionActivity, StorageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onClickLockBtn() {
        runOnUiThread {
            val intent = Intent(this@SelectionActivity, LockActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onClickPresenceBtn() {
        runOnUiThread {
            val intent = Intent(this@SelectionActivity, PresenceActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}