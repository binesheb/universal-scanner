package com.binesheb.universalscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var status: TextView
    private var lastValue: String? = null
    private var lastAcceptedAt = 0L
    private var count = 0
    private val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) startCamera() else status.text = "Camera permission is required" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        val root = FrameLayout(this)
        val previewView = PreviewView(this)
        root.addView(previewView, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        status = TextView(this).apply { text = "Starting scanner…"; textSize = 20f; setPadding(32, 32, 32, 32); setBackgroundColor(0xCC101010.toInt()); setTextColor(0xFFFFFFFF.toInt()) }
        root.addView(status, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { gravity = android.view.Gravity.BOTTOM })
        setContentView(root)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) startCamera(previewView) else requestCamera.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera(previewView: PreviewView? = null) {
        val view = previewView ?: findPreviewView() ?: return
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()
            val preview = Preview.Builder().build().also { it.surfaceProvider = view.surfaceProvider }
            val scanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build())
            val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
            analysis.setAnalyzer(cameraExecutor) { proxy ->
                val mediaImage = proxy.image
                if (mediaImage == null) { proxy.close(); return@setAnalyzer }
                val image = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
                scanner.process(image).addOnSuccessListener { codes ->
                    val value = codes.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue?.trim()
                    if (!value.isNullOrBlank()) accept(value)
                }.addOnCompleteListener { proxy.close() }
            }
            provider.unbindAll()
            provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
            status.text = "READY TO SCAN"
        }, ContextCompat.getMainExecutor(this))
    }

    private fun findPreviewView(): PreviewView? = (findViewById<android.view.ViewGroup>(android.R.id.content).getChildAt(0) as? FrameLayout)?.getChildAt(0) as? PreviewView

    private fun accept(value: String) {
        val now = System.currentTimeMillis()
        if (value == lastValue && now - lastAcceptedAt < 1500) return
        lastValue = value; lastAcceptedAt = now; count++
        runOnUiThread { status.text = "SCAN $count\n$value" }
    }

    override fun onDestroy() { cameraExecutor.shutdown(); super.onDestroy() }
}
