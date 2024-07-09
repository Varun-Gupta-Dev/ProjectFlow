package com.example.projectflow.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectflow.R
import com.example.projectflow.databinding.ActivityCreateBoardBinding
import com.example.projectflow.firebase.FirestoreClass
import com.example.projectflow.models.Board
import com.example.projectflow.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var mSelectedImageFileUri: Uri? = null
    private var ivProfileUserImage: CircleImageView? = null
    private lateinit var binding: ActivityCreateBoardBinding
    private var mBoardImageURL: String = ""
    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupActionBar()

        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }
        ivProfileUserImage = findViewById(R.id.iv_profile_user_image)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }


        binding.ivBoardImage.setOnClickListener {

            //1. Checks the permissiom to read external storage
            //2. Provides functionality to select an image
            if(ContextCompat.checkSelfPermission(
                    this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ){
                // Show image chooser
                Constants.showImageChooser(this)

            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        //Create button onClickListener
        binding.btnCreate.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog("Please Wait")
                createBoard()
            }

        }

    }

    private fun createBoard(){
        // Step1: Get the assigned user
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        // Step2: Create the board object
        var board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        //Step3: Passing the above created board object to the createBoard function of FirestoreClass
        FirestoreClass().createBoard(this, board)
    }

    // Function to upload board image to the storage
    //The uploadBoardImage function handles the process of uploading an image to Firebase Storage
    // and then using the uploaded image's URL to create a board.
     private fun uploadBoardImage(){
         //1. Show Progress Dialog:
         //A progress dialog is displayed to inform the user that an image upload is in progress
         showProgressDialog("Please Wait")

//        2. Create Storage Reference:
        //A reference to Firebase Storage is obtained using FirebaseStorage.getInstance().
        //A child node is created with a unique name constructed using "BOARD_IMAGE", the current timestamp,
        // and the file extension of the selected image (mSelectedImageFileUri). This ensures each uploaded image has a distinct name.
        val sRef : StorageReference =
            FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE"+ System.currentTimeMillis()
                    + "." + Constants.getFileExtension(this,mSelectedImageFileUri))

        //3. Upload Image:
        //The selected image file (mSelectedImageFileUri) is uploaded to the created storage reference using putFile().
        //Success and failure listeners are attached to handle the outcome of the upload operation

        //4. Handle Successful Upload:
        //Upon successful upload, the download URL of the uploaded image is retrieved from the taskSnapshot.
        //The download URL is logged for informational purposes.
        //The download URL is stored in the mBoardImageURL variable.
        //The progress dialog is hidden.
        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot->
            Log.i(
                "Board Image Url",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                // Above line creates url of where the image is stored.
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                Log.i("Downlodable Image URL", uri.toString())
                mBoardImageURL = uri.toString()

                hideProgressDialog()
                createBoard()
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


    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    // Function to set up the action bar
    // Sets up the action bar
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressed() }
    }

    // Function to decide what to do when the permissions are granted or denied
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Todo show image chooser
                Constants.showImageChooser(this)
            }
        }else{
            Toast.makeText(this,
                "You just denied permission for storage. You can enable it from settings.",
                Toast.LENGTH_LONG).show()
        }
    }

    // Function that uses result obtained from the previous activity, and populates the data in UI by
    // checking the result code and request code
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK &&
            requestCode == Constants.PICK_IMAGE_REQUEST_CODE &&
            data!!.data != null){
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .fitCenter()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(binding.ivBoardImage);
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}