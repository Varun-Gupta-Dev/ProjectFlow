package com.example.projectflow.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.projectflow.R
import com.example.projectflow.databinding.ActivityBaseBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBaseBinding

    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog: Dialog
//    private lateinit var tvProgressText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        tvProgressText = findViewById(R.id.tv_progress_text)
    }
    fun showProgressDialog(text: String){

        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
      The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.setCancelable(false)

//        tvProgressText.text = text
        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        if(mProgressDialog.isShowing){
            mProgressDialog.dismiss()
        }

    }

    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler().postDelayed({doubleBackToExitPressedOnce = false}, 2000)
    }

    fun showErrorSnackBar(message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        snackBar.show()
    }
}