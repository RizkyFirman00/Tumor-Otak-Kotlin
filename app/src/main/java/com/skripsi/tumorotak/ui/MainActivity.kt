package com.skripsi.tumorotak.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skripsi.tumorotak.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnScan.setOnClickListener {
            Intent(this, DetectionActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.btnHistory.setOnClickListener {
            Intent(this, HistoryActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}