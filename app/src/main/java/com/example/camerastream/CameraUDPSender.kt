package com.example.camerastream

import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class CameraUDPSender(
    private val ipaddr :String = "192.168.2.2",
    private val port  :Int =5050
): ImageAnalysis.Analyzer{
    private val socket = DatagramSocket()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        val img = image.image ?:run{image.close();return}

        val jpegbyte :ByteArray = yuvToJpg(img)

        val packet = DatagramPacket(jpegbyte,jpegbyte.size, InetAddress.getByName(ipaddr),port)
        socket.send(packet)

        image.close()

    }

    private  fun yuvToJpg(image: Image):ByteArray{
        val ybuffer  = image.planes[0].buffer
        var ubuffer  = image.planes[1].buffer
        val vbuffer = image.planes[2].buffer

        val ysize = ybuffer.remaining()
        val usize = ubuffer.remaining()
        val vsize = vbuffer.remaining()

        val nv21 = ByteArray(ysize+usize+vsize);
        ybuffer.get(nv21,0,ysize)
        vbuffer.get(nv21,ysize,vsize)
        ubuffer.get(nv21,ysize+vsize,usize)

        val out = ByteArrayOutputStream()
        val yuvImage = YuvImage(nv21, ImageFormat.NV21,image.width,image.height,null)
        yuvImage.compressToJpeg(android.graphics.Rect(0,0,image.width,image.height),50,out)

        return out.toByteArray()

    }
}