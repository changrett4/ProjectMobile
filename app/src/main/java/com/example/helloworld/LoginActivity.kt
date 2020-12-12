package com.example.helloworld

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.helloworld.model.UserObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    private val TAG= "LoginActivity"

    private var email: String?=null
    private var password: String?=null

    private var tvForgotPassword: TextView?=null
    private var etEmail: TextView?=null
    private var etPassword: TextView?=null
    private var btnLogin: Button?=null
    private var btnCreate: Button?=null
    private var mProgress: ProgressDialog?=null
    private var user: UserObject?=null

    private var mAuth: FirebaseAuth?=null
    private val mData: FirebaseFirestore= FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            window.setStatusBarColorTo(R.color.colorPrimary)
        }
        initialise()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun Window.setStatusBarColorTo(color: Int){
        this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.statusBarColor = ContextCompat.getColor(baseContext,color)

    }
    private fun initialise(){
        tvForgotPassword= findViewById(R.id.tv_forgot_password ) as TextView
        etEmail= findViewById(R.id.et_email) as EditText
        etPassword= findViewById(R.id.et_password) as EditText
        btnCreate= findViewById(R.id.btn_register_account) as Button
        btnLogin= findViewById(R.id.btn_login) as Button
        mProgress= ProgressDialog(this)

        mAuth= FirebaseAuth.getInstance()

        tvForgotPassword!!.setOnClickListener{startActivity(Intent(this@LoginActivity,ForgotDataActivity::class.java))}

        btnCreate!!.setOnClickListener{startActivity(Intent(this@LoginActivity,CreateAccountActivity::class.java))}

        btnLogin!!.setOnClickListener{loginUser()}
    }
    private fun loginUser(){
        email=etEmail?.text.toString()
        password=etPassword?.text.toString()
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mProgress!!.setMessage("working...")
            mProgress!!.show()

            Log.d(TAG,"login user")
            mAuth!!.signInWithEmailAndPassword(email!!,password!!).addOnCompleteListener(this){
                task ->


                if(task.isSuccessful){
                    Log.d(TAG,"logado com sucesso")

                    mData.collection("user").document(mAuth!!.currentUser!!.uid).get().addOnSuccessListener {
                        user= it.toObject(UserObject::class.java)
                        if(user?.role=="admin"){
                            adminUI()
                            mProgress!!.hide()
                        }
                        else
                        {
                           updateUserUI()
                            mProgress!!.hide()
                        }

                    }.addOnFailureListener {
                        Toast.makeText(this,"toObject failed",Toast.LENGTH_SHORT).show()
                    }

                }
                else{
                    Log.d(TAG,"Auth fail",task.exception)
                    Toast.makeText(this,"autentication failed",Toast.LENGTH_SHORT).show()

                }
            }
        }
        else{
            Toast.makeText(this,"de mais detalhes",Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateUserUI(){
        val intent = Intent(this@LoginActivity,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun adminUI(){
        val intent= Intent(this@LoginActivity,AdminMainActivity::class.java )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}