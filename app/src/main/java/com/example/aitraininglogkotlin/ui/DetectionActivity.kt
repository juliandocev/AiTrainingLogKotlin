package com.example.aitraininglogkotlin.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.aitraininglogkotlin.CARD_ID_EXTRA
import com.example.aitraininglogkotlin.databinding.ActivityDetectionBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions


class DetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetectionBinding

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    var canvas: Canvas? = null

    // Create an instance of PoseDetector

    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()

//    // Accurate pose detector on static images, when depending on the pose-detection-accurate sdk
//    val options = AccuratePoseDetectorOptions.Builder()
//        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
//        .build()

    // Initialize the pose detector
    val poseDetector = PoseDetection.getClient(options)
    @RequiresApi(Build.VERSION_CODES.R)
    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val cardId = intent.getIntExtra(CARD_ID_EXTRA, -1)


        // Request CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // Check for CameraProvider availability
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))


    }



    // Select camera and bind the lifecycle
    @RequiresApi(Build.VERSION_CODES.R)
    @ExperimentalGetImage
    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        // Create a preview
        val preview : Preview = Preview.Builder()
            .build()

        // Select  camera
        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT) // Front camera usage
            .build()

        // Attach the preview to the corresponding view
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        // Create  ImageAnalysis
        val imageAnalysis = ImageAnalysis.Builder()
            // enable the following line if RGBA output is needed.
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            //.setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        //Configure your analyzer for ImageAnalysis
        imageAnalysis.setAnalyzer(ActivityCompat.getMainExecutor(this), ImageAnalysis.Analyzer { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            // insert your code here.
            // after done, release the ImageProxy object
            val byteBuffer = imageProxy.image!!.planes[0].buffer
            byteBuffer.rewind()
            val bitmap =
                Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(byteBuffer)

            // Rotate the bitmap
            val matrix = Matrix()
            matrix.postRotate(270F)
            matrix.postScale(-1f, 1f)
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                imageProxy.width,
                imageProxy.height,
                matrix,
                false
            )

            //val mediaImage = imageProxy.image
            if (rotatedBitmap != null) {
                val image = InputImage.fromBitmap(rotatedBitmap, 0)



                // Pass image to an ML Kit Vision API
                poseDetector.process(image)
                    .addOnSuccessListener { pose ->
                        canvas = Canvas(rotatedBitmap);

                        binding.drawOverlay.graphOverlay(canvas!!, pose)
                        // Handle the Pose results here
                        binding.drawOverlay.getBitmap(rotatedBitmap)
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        // Handle the failure here
                    }
                    .addOnCompleteListener {
                        // This block is optional, but you can use it to perform any cleanup or UI updates after the task is complete.
                        // For example, you could close the imageProxy here.
                        imageProxy.close()
                    }
            } else {
                // Handle the case where mediaImage is null
                imageProxy.close()
            }
        })

        // Bind all that to the lifecycle. Now we can visualise the camera
        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector,imageAnalysis, preview)
    }




}