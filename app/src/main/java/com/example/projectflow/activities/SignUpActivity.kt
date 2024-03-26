package com.example.projectflow.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projectflow.R
import com.example.projectflow.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
        auth = Firebase.auth

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

        if(validateForm(name, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    hideProgressDialog()
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val registeredEmail = firebaseUser.email!!
                            Toast.makeText(
                                this,
                                "$name you successfully registered the email address $registeredEmail",
                                Toast.LENGTH_LONG
                            ).show()
                            FirebaseAuth.getInstance().signOut()
//                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                task.exception!!.message, Toast.LENGTH_LONG
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