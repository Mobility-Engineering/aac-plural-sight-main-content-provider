package com.dexcom.sdk.aac_fullcontentapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dexcom.sdk.aac_fullcontentapp.databinding.ActivityCameraBinding
import com.squareup.picasso.Picasso


class CameraActivity : AppCompatActivity() {
    lateinit var binding: ActivityCameraBinding
    private var bitmap: Bitmap? = null
    private lateinit var launcher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        registerCameraActivity()
        launchCamera()
    }

    private fun launchCamera() {
        val photoFile = "testImage"
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(intent)
    }

    private fun registerCameraActivity() {
            launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                    result ->
                if (result.resultCode == RESULT_OK ) {
                    val intent: Intent? = result.data
                    val photo = intent?.extras?.get("data") as Bitmap?
                    bitmap = photo
                    showPhoto()
                }
            }
        }

    private fun showPhoto() {

        binding.photo.setImageBitmap(bitmap)
    }
}
