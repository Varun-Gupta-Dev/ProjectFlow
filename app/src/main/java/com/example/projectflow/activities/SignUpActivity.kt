package com.example.projectflow.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projectflow.R
import com.example.projectflow.databinding.ActivitySignUpBinding
import com.example.projectflow.firebase.FirestoreClass
import com.example.projectflow.models.User
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

     fun userRegisteredSuccess(){

        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()
        hideProgressDialog()
//         FirebaseAuth.getInstance().signOut()
         finish()
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
        val email:String = binding.etEmailSignUp.text.toString().trim{ it <= ' '}
        val password: String = binding.etPassword.text.toString().trim{ it <= ' '}

        if (validateForm(name, email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    // If the registration is successfully done
                    if (task.isSuccessful) {

                        // Firebase registered user
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        // Registered Email
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)
                        FirestoreClass().registerUser(this, user)

                        /**
                         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
                         * and send him to Intro Screen for Sign-In
                         */




                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                           task.exception.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        hideProgressDialog()
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