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
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions


class DetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetectionBinding

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    var canvas: Canvas? = null
    var mPaint = Paint()

    var boundaryPaint = Paint()


    var leftPaint = Paint()

    var rightPaint = Paint()


    // Create an instance of PoseDetector

    // 1. Create options

    // Base pose detector with streaming frames, when depending on the pose-detection sdk
    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()

//    // Accurate pose detector on static images, when depending on the pose-detection-accurate sdk
//    val options = AccuratePoseDetectorOptions.Builder()
//        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
//        .build()

    // 2.  Initialize the pose detector
    val poseDetector = PoseDetection.getClient(options)
    @RequiresApi(Build.VERSION_CODES.R)
    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cardId = intent.getIntExtra(CARD_ID_EXTRA, -1)


        mPaint.color = Color.GREEN;
        mPaint.style = Paint.Style.FILL_AND_STROKE;
        mPaint.strokeWidth = 10F;

        leftPaint.strokeWidth = 10f
        leftPaint.color = Color.GREEN

        rightPaint.strokeWidth = 10f
        rightPaint.color = Color.YELLOW

        boundaryPaint.color = Color.WHITE
        boundaryPaint.strokeWidth = 10f
        boundaryPaint.style = Paint.Style.STROKE

        // Request a CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // Check for CameraProvider availability
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))


    }



    // Select a camera and bind the lifecycle
    @RequiresApi(Build.VERSION_CODES.R)
    @ExperimentalGetImage
    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        // Create a preview
        var preview : Preview = Preview.Builder()
            .build()

        // Select  camera
        var cameraSelector : CameraSelector = CameraSelector.Builder()
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

                        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
                        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
                        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
                        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
                        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
                        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
                        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
                        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
                        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
                        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
                        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
                        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

                        val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
                        val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
                        val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
                        val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
                        val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
                        val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
                        val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
                        val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
                        val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
                        val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)

                        // Task completed successfully
                        for (poseLandmark in pose.allPoseLandmarks) {
                            canvas!!.drawCircle(
                                poseLandmark.position.x,
                                poseLandmark.position.y,
                                5f,
                                mPaint
                            )


                            canvas?.drawLine(leftShoulder!!.position.x,leftShoulder.position.y,rightShoulder!!.position.x,rightShoulder.position.y,boundaryPaint)
                            canvas?.drawLine(leftHip!!.position.x,leftHip.position.y,rightHip!!.position.x,rightHip.position.y,boundaryPaint)

                            //Left body

                            canvas?.drawLine(leftShoulder!!.position.x,leftShoulder.position.y,leftElbow!!.position.x,leftElbow.position.y,leftPaint)
                            canvas?.drawLine(leftElbow!!.position.x,leftElbow.position.y,leftWrist!!.position.x,leftWrist.position.y,leftPaint)
                            canvas?.drawLine(leftShoulder!!.position.x,leftShoulder.position.y,leftHip!!.position.x,leftHip.position.y,leftPaint)
                            canvas?.drawLine(leftHip!!.position.x,leftHip.position.y,leftKnee!!.position.x,leftKnee.position.y,leftPaint)
                            canvas?.drawLine(leftKnee!!.position.x,leftKnee.position.y,leftAnkle!!.position.x,leftAnkle.position.y,leftPaint)
                            canvas?.drawLine(leftWrist!!.position.x,leftWrist.position.y,leftThumb!!.position.x,leftThumb.position.y,leftPaint)
                            canvas?.drawLine(leftWrist!!.position.x,leftWrist.position.y,leftPinky!!.position.x,leftPinky.position.y,leftPaint)
                            canvas?.drawLine(leftWrist!!.position.x,leftWrist.position.y,leftIndex!!.position.x,leftIndex.position.y,leftPaint)
                            canvas?.drawLine(leftIndex!!.position.x,leftIndex.position.y,leftPinky!!.position.x,leftPinky.position.y,leftPaint)
                            canvas?.drawLine(leftAnkle!!.position.x,leftAnkle.position.y,leftHeel!!.position.x,leftHeel.position.y,leftPaint)
                            canvas?.drawLine(leftHeel!!.position.x,leftHeel.position.y,leftFootIndex!!.position.x,leftFootIndex.position.y,leftPaint)

                            //Right body
                            canvas?.drawLine(rightShoulder!!.position.x,rightShoulder.position.y,rightElbow!!.position.x,rightElbow.position.y,rightPaint)
                            canvas?.drawLine(rightElbow!!.position.x,rightElbow.position.y,rightWrist!!.position.x,rightWrist.position.y,rightPaint)
                            canvas?.drawLine(rightShoulder!!.position.x,rightShoulder.position.y,rightHip!!.position.x,rightHip.position.y,rightPaint)
                            canvas?.drawLine(rightHip!!.position.x,rightHip.position.y,rightKnee!!.position.x,rightKnee.position.y,rightPaint)
                            canvas?.drawLine(rightKnee!!.position.x,rightKnee.position.y,rightAnkle!!.position.x,rightAnkle.position.y,rightPaint)
                            canvas?.drawLine(rightWrist!!.position.x,rightWrist.position.y,rightThumb!!.position.x,rightThumb.position.y,rightPaint)
                            canvas?.drawLine(rightWrist!!.position.x,rightWrist.position.y,rightPinky!!.position.x,rightPinky.position.y,rightPaint)
                            canvas?.drawLine(rightWrist!!.position.x,rightWrist.position.y,rightIndex!!.position.x,rightIndex.position.y,rightPaint)
                            canvas?.drawLine(rightIndex!!.position.x,rightIndex.position.y,rightPinky!!.position.x,rightPinky.position.y,rightPaint)
                            canvas?.drawLine(rightAnkle!!.position.x,rightAnkle.position.y,rightHeel!!.position.x,rightHeel.position.y,rightPaint)
                            canvas?.drawLine(rightHeel!!.position.x,rightHeel.position.y,rightFootIndex!!.position.x,rightFootIndex.position.y,rightPaint)






                        }
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

//    @RequiresApi(Build.VERSION_CODES.R)
//    fun translateX(x: Float): Float {
//
//        // you will need this for the inverted image in case of using front camera
//         return this.display?.width?.minus(x)!!
//
//        //return x;
//    }
}