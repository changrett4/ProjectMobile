package com.example.helloworld

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.helloworld.helper.EncryptionHelper
import com.example.helloworld.model.EatUserObject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class ScannedQr : AppCompatActivity() {
    companion object {
        private const val SCANNED_STRING: String = "scanned_string"
        fun getScannedActivity(callingClassContext: Context, encryptedString: String): Intent {
            return Intent(callingClassContext, ScannedQr::class.java)
                .putExtra(SCANNED_STRING, encryptedString)
        }
    }
    private val mData: FirebaseFirestore= FirebaseFirestore.getInstance()
    private var imgR: ImageView?=null
    private var textR: TextView?=null
    private var btnR: Button?=null
//    private var mProgress:ProgressDialog= ProgressDialog(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_qr)
        imgR= findViewById(R.id.image_result) as ImageView
        textR= findViewById(R.id.text_result) as TextView
        btnR= findViewById(R.id.btn_result) as Button
        btnR?.setOnClickListener { onClick() }
        init()


    }
    private fun init(){
        if (intent.getSerializableExtra(SCANNED_STRING) == null)
            throw RuntimeException("No encrypted String found in intent")
        val decryptedString = EncryptionHelper.getInstance().getDecryptionString(intent.getStringExtra(SCANNED_STRING))
        val userEatObject = Gson().fromJson(decryptedString, EatUserObject::class.java)
     //   mProgress!!.setMessage("Registering user...")
      //  mProgress!!.show()
        mData.collection("user").document(userEatObject.uid.toString()).collection("eats")
            .document(userEatObject.eatId.toString()).get().addOnSuccessListener {
                if(it.exists()){
                    Glide.with(this).load(R.mipmap.ic_failure).into(imgR!!)
                    textR!!.text= "LIMITE DE REFEIÇÃO EXCEDIDO"

                   var itin= it.toObject(EatUserObject::class.java)
                    mData.collection("user").document(userEatObject.uid.toString()).collection("eats")
                        .document(userEatObject.eatId.toString()).update("repeat", itin!!.repeat!!+1)
                    mData.collection("user").document(userEatObject.uid.toString())
                        .collection("eats").document(userEatObject.eatId.toString()).update("scanned",true)

                    return@addOnSuccessListener
                }
                else{
                    userEatObject.repeat= userEatObject.repeat!!+1
                    mData.collection("user").document(userEatObject.uid.toString()).collection("eats")
                        .document(userEatObject.eatId.toString()).set(userEatObject)
                    Glide.with(this).load(R.mipmap.ic_sucess).into(imgR!!)
                    textR!!.text= "REGISTRADO COM SUCESSO"
                    mData.collection("user").document(userEatObject.uid.toString())
                        .collection("eats").document(userEatObject.eatId.toString()).update("scanned",true)
                }

             //   mProgress!!.hide()

            }.addOnFailureListener {
                Log.d("ScannedQr","proceed is error")
            }
    }
    private fun onClick(){
        val intent= Intent(this@ScannedQr, AdminMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}