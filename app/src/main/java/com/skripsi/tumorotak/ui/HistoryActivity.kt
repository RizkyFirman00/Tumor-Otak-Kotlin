package com.skripsi.tumorotak.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skripsi.tumorotak.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbarHistory.btnBack.setOnClickListener {
            finish()
        }
    }
}