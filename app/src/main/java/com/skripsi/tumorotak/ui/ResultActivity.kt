package com.skripsi.tumorotak.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.skripsi.tumorotak.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }
    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.include2.btnBack.setOnClickListener {
            Intent(this, DetectionActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        val imageFilePath = intent.getStringExtra("imageFile")
        Glide.with(this)
            .load(imageFilePath)
            .into(binding.ivImage)

        val valueResult = intent.getFloatExtra("valueResult", 0F) * 100
        val formattedValueResult = String.format("%.2f", valueResult)

        val resultText = when (intent.getStringExtra("result")) {
            "Tumor" -> "Ada tanda-tanda tumor di otak."
            "Normal" -> "Tidak ada tanda-tanda tumor di otak."
            else -> "Hasil tidak diketahui"
        }

        binding.tvResult.text = resultText
        binding.tvValue.text = "$formattedValueResult%"
    }
}