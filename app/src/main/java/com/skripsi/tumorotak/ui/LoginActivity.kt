package com.skripsi.tumorotak.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skripsi.tumorotak.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}