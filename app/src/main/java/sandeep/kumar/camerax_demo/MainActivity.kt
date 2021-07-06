package sandeep.kumar.camerax_demo

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    var camera: Camera? = null
    var preview: Preview? = null
    var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check if user give permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PERMISSION_GRANTED
        ) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                0
            )
        }

        camera_capture_button.setOnClickListener {
            takePhoto()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PERMISSION_GRANTED
        ) {
            startCamera()

        } else {
            Toast.makeText(this, "Please accept camera permissions ", Toast.LENGTH_LONG).show()
        }
    }

    private fun takePhoto() {

        val photoFile = File(
            externalMediaDirs.firstOrNull(),
            "CameraApp-${System.currentTimeMillis()}.jpg"
        )
        val output = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture?.takePicture(
            output,
            ContextCompat.getMainExecutor(this),
        object :ImageCapture.OnImageSavedCallback{

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(applicationContext, "Image Saved", Toast.LENGTH_SHORT).show()            }

            override fun onError(exception: ImageCaptureException) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build()
            preview?.setSurfaceProvider(camera_view.createSurfaceProvider(camera?.cameraInfo))

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.Builder().requireLensFacing(
                CameraSelector.LENS_FACING_BACK
            ).build()
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))

    }
}