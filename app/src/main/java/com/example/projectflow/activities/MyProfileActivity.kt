package com.example.projectflow.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

import com.example.projectflow.R
import com.example.projectflow.databinding.ActivityMyProfileBinding
import com.example.projectflow.firebase.FirestoreClass
import com.example.projectflow.models.User
import com.example.projectflow.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    companion object{
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }
    private lateinit var binding: ActivityMyProfileBinding

    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageURL: String = ""
    private lateinit var mUserDetails: User
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        //setStatusBarColor()
        FirestoreClass().loadUserData(this)

        binding.ivProfileUserImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                    ){
                // Show image chooser
                showImageChooser()

            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnUpdate.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog("Please Wait")
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Todo show image chooser
                showImageChooser()
            }
        }else{
            Toast.makeText(this,
                "You just denied permission for storage. You can enable it from settings.",
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK &&
            requestCode == PICK_IMAGE_REQUEST_CODE &&
            data!!.data != null){
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .fitCenter()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivProfileUserImage);
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }
    private fun showImageChooser(){
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun setStatusBarColor() {
//        // Check if the device is running on Android Lollipop or higher
//        // Get the window object
//        val window: Window = window
//
//        // Set the status bar color
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//
//            window.statusBarColor = resources.getColor(R.color.status_bar_color, theme)
//
//
//    }

    fun setUserDataInUI(user: User){

        mUserDetails = user
        Glide
            .with(this)
            .load(user.image)
            .fitCenter()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivProfileUserImage);

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
            if(user.mobile != 0L){
                binding.etMobile.setText(user.mobile.toString())
            }
    }

    private fun updateUserProfileData(){
        val userHashmap = HashMap<String,Any>()
        var anyChangesMade = false

         if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){
             userHashmap[Constants.IMAGE] = mProfileImageURL
             anyChangesMade = true
         }
        if(binding.etName.text.toString() != mUserDetails.name){
            userHashmap[Constants.NAME] = binding.etName.text.toString()
            anyChangesMade = true
        }
        if(binding.etMobile.text.isNullOrEmpty()){
            hideProgressDialog()
            Toast.makeText(this, "Please Enter your mobile number.", Toast.LENGTH_LONG).show()
            anyChangesMade = false
        }else if(binding.etMobile.text.toString() != mUserDetails.mobile.toString()){
            userHashmap[Constants.MOBILE] = binding.etMobile.text.toString().toLong()
            anyChangesMade = true
        }
        if (anyChangesMade){
            FirestoreClass().updateUserProfileData(this,userHashmap)
        }else {
            Toast.makeText(this, "You have made no changes in your profile.", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadUserImage(){
        showProgressDialog("Please Wait")

        if(mSelectedImageFileUri != null){
            // store the uri in Firebase storage
            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference.child("USER_IMAGE"+ System.currentTimeMillis()
                + "." + getFileExtension(mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot->
                Log.i(
                    "Firebase Image Url",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    // Above line creates url of where the image is stored.
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.i("Downlodable Image URL", uri.toString())
                    mProfileImageURL = uri.toString()

                    hideProgressDialog()
                    updateUserProfileData()
                }
            }.addOnFailureListener {
                exception->
                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }
        }
    }

    // Function to get the file extension so that we know which type of file we get from user
    private fun getFileExtension(uri:Uri?): String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}
