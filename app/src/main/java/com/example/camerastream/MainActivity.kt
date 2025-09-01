package com.example.camerastream

import android.Manifest
import android.app.Application
import android.os.Bundle
import android.widget.VideoView
import android.widget.Toast
import android.widget.Button
import android.content.pm.PackageManager
import android.net.Network
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat
import java.security.Permission
import java.net.Socket


class MainActivity : AppCompatActivity() {


    private val permissions  = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    );

    private val requestPerms = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {granted -> 
            
        val allGranted = granted.values.all {it}
        if (allGranted) startCamera() else Toast.makeText(this,"Camera Permission is required ",Toast.LENGTH_SHORT).show()
        
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val  vv : VideoView = findViewById(R.id.videoView);
        val act: Button = findViewById(R.id.actn);



        
        havePermission()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    private fun havePermission(){
       val missing = permissions.filter{
                    ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED;
    }
    if missing.isEmpty() startCamera() else requestPerms.launch(permissions)

    }


    public fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get(),
            bindUseCases(rebind)
        }, ContextCompat.getMainExecutor(this))
    }


    private fun bindUseCases(rebind: Boolean) {
val provider = cameraProvider ?: return
if (rebind) provider.unbindAll()


// Preview
preview = Preview.Builder()
.setTargetAspectRatio(AspectRatio.RATIO_16_9)
.build()
.also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }


// ImageAnalysis for per-frame access (YUV420)
imageAnalysis = ImageAnalysis.Builder()
.setTargetResolution(Size(1280, 720))
.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
.build()
.also { analysis ->
analysis.setAnalyzer(cameraExecutor) { image ->
try {
onFrame(image)
} catch (t: Throwable) {
Log.e("CameraX", "Analyzer error", t)
} finally {
image.close()
}
}
}



 fun push(){
Socket("10.0.0.1:8080",)
 Network.bindSocket(socket)
}
    
}