package com.example.projectflow.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projectflow.R
import com.example.projectflow.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()

        binding.btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }


    }

    private fun registerUser(){
        val name: String = binding.etName.text.toString().trim{ it <= ' '}
        val email:String = binding.etEmail.text.toString().trim{ it <= ' '}
        val password: String = binding.etPassword.text.toString().trim{ it <= ' '}

        if (validateForm(name, email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    // Hide the progress dialog
                    hideProgressDialog()

                    // If the registration is successfully done
                    if (task.isSuccessful) {

                        // Firebase registered user
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        // Registered Email
                        val registeredEmail = firebaseUser.email!!

                        Toast.makeText(
                            this@SignUpActivity,
                            "$name you have successfully registered with email id $registeredEmail.",
                            Toast.LENGTH_SHORT
                        ).show()

                        /**
                         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
                         * and send him to Intro Screen for Sign-In
                         */

                        /**
                         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
                         * and send him to Intro Screen for Sign-In
                         */
                        FirebaseAuth.getInstance().signOut()
                        // Finish the Sign-Up Screen
                        finish()
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(name: String, email: String, password: String):Boolean{

        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please Enter a name")
                false
            }
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