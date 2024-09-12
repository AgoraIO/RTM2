package io.agora.rtmdemo

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmClient
import io.agora.rtm.RtmConfig
import io.agora.rtm.RtmConstants.RtmAreaCode
import io.agora.rtm.RtmLogConfig
import io.agora.rtm.RtmProxyConfig
import io.agora.rtmdemo.databinding.ActivityMainBinding
import java.util.EnumSet
import java.util.Random

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private var rtmClient: RtmClient? = null
    private var userId: String? = null
    private var inChat = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.loginRtmBtn.setOnClickListener { onClickLogin() }
        val random = Random()
        binding.userId.setText("user_" + String.format("%04d", random.nextInt(10000)))
    }

    override fun onResume() {
        super.onResume()
        binding.loginRtmBtn.isEnabled = true
        if (inChat) {
            inChat = false
            doLogout()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RtmClient.release()
    }

    private fun onClickLogin() {
        val appId = binding.appId.text.toString()
            .ifEmpty { applicationContext.getString(R.string.agora_app_id) }
        val token =
            binding.token.text.toString().ifEmpty { appId }
        RtmManager.the().appId = appId
        RtmManager.the().token = token

        userId = binding.userId.text.toString()
        if (TextUtils.isEmpty(userId)) {
            showToast("userId is empty")
        } else {
            binding.loginRtmBtn.isEnabled = false
            createRtmClient()
            doLogin()
        }
    }

    private fun createRtmClient() {
        val logConfig = RtmLogConfig()
        val proxyConfig = RtmProxyConfig()
        val rtmConfig = RtmConfig.Builder(RtmManager.the().appId, userId)
            .areaCode(EnumSet.of<RtmAreaCode>(RtmAreaCode.AS, RtmAreaCode.CN))
            .eventListener(RtmManager.the().rtmEventListener)
            .proxyConfig(proxyConfig)
            .logConfig(logConfig)
            .build()

        rtmClient = RtmClient.create(rtmConfig)
        RtmManager.the().rtmClient = rtmClient
    }

    private fun doLogin() {
        if (rtmClient == null) {
            showToast("rtm client is null")
            return
        }

        inChat = true
        rtmClient?.login(RtmManager.the().token, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                runOnUiThread {
                    showToast("login success")
                    val intent = Intent(this@MainActivity, SelectionActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                runOnUiThread {
                    showToast(errorInfo.toString())
                }
            }
        })
    }

    private fun doLogout() {
        if (rtmClient == null) {
            showToast("rtm client is null")
            return
        }

        rtmClient?.logout(object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                runOnUiThread {
                    showToast("logout success")
                }
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                runOnUiThread {
                    showToast(errorInfo.toString())
                }
            }
        })
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}