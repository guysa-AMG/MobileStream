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
import androidx.camera.core.CameraExecutor
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat
import java.security.Permission
import java.net.Socket

import androidx.camera.view.PreviewView
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private  lateinit var preview : PreviewView;
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        preview = PreviewView(this);
        setContentView(preview);

        startCamera();



    }
    public fun startCamera(){
        val camerapf = ProcessCameraProvider.getInstance(this)
        camerapf.addListener({
            val cameraProvider = camerapf.get();
            val prev = androidx.camera.core.Preview.Builder().build().also{
                it.setSurfaceProvider(preview.surfaceProvider);
            }
            val analyze = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also{
                    it.setAnalyzer(cameraExecutor,CameraUDPSender())
                }
            val camerSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this,camerSelector,prev,analyze)
        }, ContextCompat.getMainExecutor(this)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}