package com.example.registrationwithserver

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.registrationwithserver.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var imageType = ""
    var selectedCity = ""
    var photoPathString = ""
    var registeredDataArrayList = ArrayList<RegisteredData>()
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        clickListener()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun clickListener() {
        binding.photo.setOnClickListener {
            if (!checkPermissionGrantedForReadExternalStorage()) {
                requestPermissions(
                    arrayOf(permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            } else {
                selectImage()
            }
        }
        binding.citySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedCity = if (position > 0) {
                    parent.getItemAtPosition(position).toString()
                } else {
                    ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.submit.setOnClickListener {
            if (checkNameNotExist() || checkEmailNotExist() || checkValidPhoneNumberNotExist() || checkCityNotExist()) {
                Toast.makeText(
                    this@MainActivity,
                    "Please Fill All valid Details",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                submitRecord()
                clearForm()
            }
        }
        binding.showData.setOnClickListener { openAllRecords() }
    }

    private fun submitRecord() {
        registeredDataArrayList.add(
            RegisteredData(
                imageType,
                photoPathString,
                binding.name.text.toString(),
                binding.email.text.toString(),
                binding.phoneNumber.text.toString(),
                selectedCity
            )
        )
        Toast.makeText(this@MainActivity, "Registration Successful !!!", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1000) {
            if (checkPermissionGrantedForReadExternalStorage()) {
                selectImage()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkPermissionGrantedForReadExternalStorage(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
    }

    private fun openAllRecords() {
        val intent = Intent(this@MainActivity, RecordsActivity::class.java)
        intent.putParcelableArrayListExtra("data", registeredDataArrayList)
        startActivity(intent)
    }

    private fun clearForm() {
        binding.name.text = null
        binding.email.text = null
        binding.phoneNumber.text = null
        selectedCity = ""
        binding.citySpinner.setSelection(0)
        photoPathString = ""
        imageType = ""
        setDefaultPhoto()
    }

    private fun setDefaultPhoto() {
        val imageDefault =
            resources.getIdentifier("@drawable/download", null, packageName)
        val res = resources.getDrawable(imageDefault)
        photo!!.setImageDrawable(res)
    }

    private fun checkCityNotExist(): Boolean {
        return selectedCity.equals("", ignoreCase = true)
    }

    private fun checkEmailNotExist(): Boolean {
        return binding.email.text.toString().equals("", ignoreCase = true)
    }

    private fun checkNameNotExist(): Boolean {
        return binding.name.text.toString().equals("", ignoreCase = true)
    }

    private fun checkValidPhoneNumberNotExist(): Boolean {
        return binding.phoneNumber.text.toString().length != 10
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun selectImage() {
        try {
            val options =
                arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Option")
            builder.setItems(options) { dialog, item ->
                if (options[item] == "Take Photo") {
                    takePictureFromCamera(dialog)
                } else if (options[item] == "Choose From Gallery") {
                    takePictureFromGallery(dialog)
                } else {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            cameraAccessPermissionError(e)
        }
    }

    private fun cameraAccessPermissionError(e: Exception) {
        Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun takePictureFromGallery(dialog: DialogInterface) {
        dialog.dismiss()
        openGallery()
    }

    private fun openGallery() {
        val pickPhoto =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, RESULT_LOAD_IMAGE_FROM_GALLERY)
    }

    private fun takePictureFromCamera(dialog: DialogInterface) {
        dialog.dismiss()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, RESULT_LOAD_IMAGE_FROM_CAMERA)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && null != data) {
            try {
                setPictureFromGallery(data)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == RESULT_LOAD_IMAGE_FROM_CAMERA && resultCode == Activity.RESULT_OK && null != data) {
            setPictureByCamera(data)
        }
    }

    private fun setPictureByCamera(data: Intent) {
        try {
            val bitmap = data.extras!!["data"] as Bitmap?
            val bytes = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
            binding.photo.setImageBitmap(bitmap)
            photoPathString = saveToInternalStorage(bitmap)
            imageType = "camera"
            Log.d("MainActivity", saveToInternalStorage(bitmap))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap?): String {
        val cw = ContextWrapper(applicationContext)

        // path to /data/data/yourapp/app_data/imageDir
        @SuppressLint("SimpleDateFormat") val directory = cw.getDir(
            "imageDir" +
                    SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date()),
            Context.MODE_PRIVATE
        )

        // Create imageDir
        val mypath = File(directory, "profile.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage!!.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }

    @Throws(IOException::class)
    private fun setPictureFromGallery(data: Intent) {
        val selectedImage = data.data
        binding.photo.setImageURI(selectedImage)
        photoPathString = selectedImage.toString()
        imageType = "gallery"
    }

    companion object {
        private const val RESULT_LOAD_IMAGE_FROM_CAMERA = 1
        private const val RESULT_LOAD_IMAGE_FROM_GALLERY = 2
    }
}