package com.skripsi.tumorotak.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.skripsi.tumorotak.api.ApiService
import com.skripsi.tumorotak.api.PredictionResponse
import com.skripsi.tumorotak.R
import com.skripsi.tumorotak.databinding.ActivityDetectionBinding
import com.skripsi.tumorotak.utility.createCustomTempFile
import com.skripsi.tumorotak.utility.uriToFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class DetectionActivity : AppCompatActivity() {
    private lateinit var photoPath: String
    private var file: File? = null
    private val apiService = ApiService.create()
    private val binding by lazy { ActivityDetectionBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener {
            if (allPermissionsGranted()) {
                val intent = Intent(this, CameraActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.include.btnBack.isVisible = false

        binding.ivImage.setOnClickListener {
            startGallery()
        }

        binding.btnSubmit.setOnClickListener {
            if (file != null && file!!.exists()) {
                submitImage()
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Permission Function
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 123
        private const val REQUEST_CODE_CAMERA = 456
        private const val REQUEST_CODE_GALLERY = 789
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            val imagePath = data?.getStringExtra("imagePath")
            if (imagePath != null) {
                file = File(imagePath)
                Glide.with(this)
                    .load(file)
                    .into(binding.ivImage)
            }
        } else if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            val selectedImg: Uri = data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            file = myFile
            Glide.with(this)
                .load(selectedImg)
                .into(binding.ivImage)
        }
    }

    //Gallery Function
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        startActivityForResult(chooser, REQUEST_CODE_GALLERY)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            file = myFile
            Glide.with(this)
                .load(selectedImg)
                .into(binding.ivImage)
        }
    }

    private fun submitImage() {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file!!)
        val imagePart = MultipartBody.Part.createFormData("image", file!!.name, requestBody)
        binding.progressBar2.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.predict(imagePart)
                Log.d("detection", "API: ${response.result}, ${response.probability}")
                runOnUiThread {
                    handleResponse(response)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@DetectionActivity,
                        "Kesalahan API, Silahkan coba lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar2.visibility = View.GONE
                }
            }
        }
    }

    private fun handleResponse(response: PredictionResponse) {
        runOnUiThread {
            Intent(this@DetectionActivity, ResultActivity::class.java).apply {
                putExtra("result", response.result)
                putExtra("valueResult", response.probability)
                putExtra("imageFile", file!!.absolutePath)
                startActivity(this)
            }
            finish()
            with(binding.progressBar2) {
                visibility = View.GONE
            }
        }
    }
}