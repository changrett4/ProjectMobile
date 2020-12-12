package com.example.helloworld

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.helloworld.model.UserObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_account.*
import java.util.*

class CreateAdmActivity : AppCompatActivity() {
    private var etUsername: EditText?=null
    private var etId: EditText?=null
    private var etEmail: EditText?=null
    private var etPassword: EditText?=null
    private var etConfirm: EditText?=null
    private var btnCreate: Button?=null
    private var mProgress: ProgressDialog?=null
    private var valid: Boolean?=null
    private var mAuth: FirebaseAuth?=null
    private var db: FirebaseFirestore?=null

    private val TAG= "CreateAccountActivity"
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=100
    private var username: String?=null
    private var id: String?=null
    private var email: String?=null
    private var pass: String?=null
    private var confirm: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_adm)

        initialise()
    }

    private fun initialise(){
        etUsername= findViewById(R.id.et_username) as EditText
        etEmail= findViewById(R.id.et_email) as EditText
        etId= findViewById(R.id.et_id) as EditText
        etPassword =findViewById(R.id.et_password) as EditText
        etConfirm= findViewById(R.id.et_confirmPassword) as EditText
        btnCreate= findViewById<Button>(R.id.btn_create)
        mProgress= ProgressDialog(this)

        db= FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db!!.firestoreSettings = settings
        mAuth= FirebaseAuth.getInstance()

        btnCreate!!.setOnClickListener {

            val builder = AlertDialog.Builder(this@CreateAdmActivity,R.style.AlertDialogStyle)

            builder.setMessage("Deseja Cadastrar esse funcionário?")
                .setCancelable(false)
                .setPositiveButton("Sim") { dialog, id ->

                    uploadImageToFirebaseStorage()
                }
                .setNegativeButton("Não") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()}

        selectphoto_button_registeraccount.setOnClickListener{
            Log.d(EatRegister.TAG, "Try to show photo selector")
            if (!permissionIfNeeded()) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(EatRegister.TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_user_register.setImageBitmap(bitmap)

            selectphoto_button_registeraccount.alpha = 0f

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun permissionIfNeeded(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                return true
            }
        }
        return false
    }


    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        mProgress!!.setMessage("Cadastrando Conta...")
        mProgress!!.show()
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/userImage/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(EatRegister.TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(EatRegister.TAG, "File Location: $it")

                    createNewAccount(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(EatRegister.TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun createNewAccount(link: String) {
        username = etUsername?.text.toString()
        email = etEmail?.text.toString()
        id = etId?.text.toString()
        pass = etPassword?.text.toString()
        confirm = etConfirm?.text.toString()
        if (TextUtils.isEmpty(username) ) {
            etUsername?.setError("informe o nome do funcionário")
            valid=false
        }
        else if(TextUtils.isEmpty(email)){
            etEmail?.setError("informe o email do funcionário")
            valid=false
        }
        else if(TextUtils.isEmpty(id)){
            etId?.setError("informe o código do funcionário")
            valid=false
        }
        else if(TextUtils.isEmpty(pass)){
            etPassword?.setError("digite a senha")
            valid=false
        }
        else if (TextUtils.isEmpty(confirm) ){
            etConfirm?.setError("confirme sua senha")
            valid=false
        }
        else if(!TextUtils.equals(pass,confirm)){
            Toast.makeText(this, "senhas diferentes!", Toast.LENGTH_SHORT).show()
            etPassword?.setText("")
            etConfirm?.setText("")
            valid=false
        }
        else if(!TextUtils.isEmpty(pass) && pass?.length!! <7){
            Toast.makeText(this, "password too short!", Toast.LENGTH_SHORT).show()
            valid= false
        }
        else{

            valid=true
        }

        if(valid==true){
            mAuth!!
                .createUserWithEmailAndPassword(email!!,pass!!).addOnCompleteListener(this){
                        task ->
                    mProgress!!.hide()
                    if(task.isSuccessful){
                        Log.d(TAG,"CreateUserWithEmail:Sucess")
                        val userId= mAuth!!.currentUser!!.uid
                        // verifyEmail()
                        val testUser= UserObject(username=username,email = email,id = id,authId = userId,role = "admin",image=link)
                        // Add a new document with a generated ID
                        db!!.collection("user").document(userId)
                            .set(testUser)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "DocumentSnapshot added ")
                                Toast.makeText(this,"Dados cadastrados com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                                Toast.makeText(this,"Falha do cadastro, tente novamente mais tarde!",
                                    Toast.LENGTH_SHORT).show()
                            }
                        //updateUserUI()
                    } else{
                        Log.w(TAG, "Create user fail",task.exception)
                        Toast.makeText(this@CreateAdmActivity,"Auth failed", Toast.LENGTH_SHORT).show()
                    }

                }}

    }
    private fun updateUserUI(){
        //val intent = Intent(this@CreateAccountActivity,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    private fun verifyEmail(){
        val mUser= mAuth!!.currentUser
        mUser!!.sendEmailVerification().addOnCompleteListener(this){
                task ->
            if(task.isSuccessful){
              //  Toast.makeText(this@CreateAccountActivity,"Verification Email Send to"+mUser.getEmail(),
                //    Toast.LENGTH_SHORT).show()
            }
            else{
                Log.e(TAG,"Send Email Verification ",task.exception)
              //  Toast.makeText(this@CreateAccountActivity,"Failed to send email verification",
                //    Toast.LENGTH_SHORT).show()
            }
        }
    }


}