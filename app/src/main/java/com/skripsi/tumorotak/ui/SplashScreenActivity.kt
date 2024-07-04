package com.skripsi.tumorotak.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.skripsi.tumorotak.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 1200
    private val binding by lazy {ActivitySplashScreenBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Handler().postDelayed({
            val intent = Intent(this@SplashScreenActivity, DetectionActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}