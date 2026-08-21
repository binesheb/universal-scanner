package com.binesheb.universalscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private val executor = Executors.newSingleThreadExecutor()
    private var granted by mutableStateOf(false)
    private var last by mutableStateOf("Waiting for a code…")
    private var scans by mutableStateOf(listOf<String>())
    private var lastAccepted = ""

    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        granted = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!granted) permission.launch(Manifest.permission.CAMERA)
        setContent { ScannerUi() }
    }

    private fun bind(view: PreviewView) {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().also { it.surfaceProvider = view.surfaceProvider }
            val detector = BarcodeScanning.getClient()
            val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
            analysis.setAnalyzer(executor) { frame ->
                val image = frame.image
                if (image == null) { frame.close(); return@setAnalyzer }
                detector.process(InputImage.fromMediaImage(image, frame.imageInfo.rotationDegrees)).addOnSuccessListener { codes ->
                    codes.firstOrNull()?.rawValue?.takeIf { it.isNotBlank() && it != lastAccepted }?.let { value ->
                        lastAccepted = value
                        runOnUiThread { last = value; scans = (listOf(value) + scans).take(20) }
                    }
                }.addOnCompleteListener { frame.close() }
            }
            provider.unbindAll()
            provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, ContextCompat.getMainExecutor(this))
    }

    @Composable private fun ScannerUi() {
        Column(Modifier.fillMaxSize().background(Color(0xFF101114)).padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("UNIVERSAL SCANNER", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Text("● LIVE", color = Color(0xFF55D66B))
            }
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().weight(1f)) {
                if (granted) AndroidView({ PreviewView(it).also(::bind) }, Modifier.fillMaxSize()) else Text("Camera permission required", color = Color.White)
            }
            Spacer(Modifier.height(10.dp))
            Text("LAST SCAN", color = Color.Gray)
            Text(last, color = Color.White, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text("SCAN LOG (${scans.size})", color = Color.Gray)
            scans.take(6).forEach { Text(it, color = Color.LightGray, modifier = Modifier.padding(vertical = 2.dp)) }
        }
    }

    override fun onDestroy() { executor.shutdown(); super.onDestroy() }
}
