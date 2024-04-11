package com.example.projectflow.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.projectflow.R
import com.example.projectflow.databinding.ActivitySignInBinding
import com.example.projectflow.firebase.FirestoreClass
import com.example.projectflow.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
        setupActionBar()
    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarSignInActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser(){
        val email:String = binding.etEmailSignIn.text.toString().trim{ it <= ' '}
        val password: String = binding.etPasswordSignIn.text.toString().trim{ it <= ' '}

        if(validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    hideProgressDialog()

                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                       FirestoreClass().signInUser(this@SignInActivity)
                        val user = auth.currentUser

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign in", "signInWithEmail:failure",task.exception)
                        Toast.makeText(
                            baseContext,
                            "Sorry, you don't have an account!",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }

        }
    }

    private fun validateForm(email: String, password: String):Boolean{

        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please Enter your email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please Enter password")
                false
            }
            else->{
                true
            }
        }
    }
}