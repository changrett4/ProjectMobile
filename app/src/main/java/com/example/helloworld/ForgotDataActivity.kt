package com.example.helloworld

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotDataActivity : AppCompatActivity() {
    private val TAG= "ForgotDataActivity"
    private var etEmail: EditText?=null
    private var btnSubmit: Button?=null
    private var email: String?=null
 //   private var message: String?=null

    private var mAuth: FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_data)

        initialise()
    }
    private fun initialise(){
        etEmail= findViewById(R.id.et_email) as EditText
        btnSubmit= findViewById(R.id.btn_submit_forgot) as Button
        mAuth=FirebaseAuth.getInstance()
        btnSubmit!!.setOnClickListener { sendPasswordResetEmail() }

    }
    private fun sendPasswordResetEmail(){
        email = etEmail?.text.toString()
            if(!TextUtils.isEmpty(email)){
                mAuth!!
                    .sendPasswordResetEmail(email!!)
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            val message= "Email sent."
                            Log.d(TAG,message)
                            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
                            updateUserUI()
                        }
                        else{
                            Log.w(TAG, task.exception!!.message!!)
                            Toast.makeText(this,"User not found",Toast.LENGTH_SHORT).show()

                        }
                    }
            }
            else{
                Toast.makeText(this,"Insert an Email",Toast.LENGTH_SHORT).show()
            }

    }

    private fun updateUserUI(){
        val intent = Intent(this@ForgotDataActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }


    }

