package com.example.helloworld

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.helper.EncryptionHelper
import com.example.helloworld.helper.QRCodeHelper
import com.example.helloworld.model.EatUserObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.android.synthetic.main.activity_generated_qr.*

class GeneratedQr : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private val TAG = "GeneratedQr"
    var mDatabase: FirebaseFirestore? = FirebaseFirestore.getInstance()
    private var eatUser: EatUserObject?=null
    private val currentUserRef: DocumentReference
        get() = mDatabase!!.document(
            "user/${
                FirebaseAuth.getInstance().currentUser!!.uid ?: throw NullPointerException(
                    "UID is null."
                )
            }"
        )
    private var b: Boolean?=null
    private var eatStr: String?=null

    companion object {

        fun getGenerateQrCodeActivity(callingClassContext: Context) =
            Intent(callingClassContext, GeneratedQr::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_qr)
        init()

    }

    private fun loadUser() {
        eatStr=intent.getStringExtra("eat_id")

        currentUserRef.get().addOnSuccessListener {
            if (!it.exists()) {
                Log.d(TAG, "document not exists")
                Toast.makeText(this@GeneratedQr, "doc not", Toast.LENGTH_SHORT).show()
            } else {

                eatUser= EatUserObject(uid=FirebaseAuth.getInstance().currentUser!!.uid,eatId = eatStr,scanned = false,repeat = 0)
                val serializeString = Gson().toJson(eatUser)
                val encryptedString =
                    EncryptionHelper.getInstance().encryptionString(serializeString).encryptMsg()
                setImageBitmap(encryptedString)
                    listener()

            }

        }.addOnFailureListener {
            Log.e(TAG, "an error have ocurrend")
            Toast.makeText(this@GeneratedQr, "other error ocurrend", Toast.LENGTH_SHORT).show()
        }

    }
    private fun listener(){
        currentUserRef.collection("eats")
            .document(eatUser!!.eatId!!).addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "No listener here ", error)
                    return@addSnapshotListener
                }
                val obj= value?.toObject(EatUserObject::class.java)
                if (value != null && value.exists()) {
                    if (obj?.scanned!!){
                        if (obj?.repeat!! > 2) {
                            currentUserRef.collection("eats").document(eatUser!!.eatId!!).update("scanned",false)
                            Toast.makeText(this, "LIMITE ATINGIDO ", Toast.LENGTH_SHORT).show()

                        } else if(obj?.repeat!! < 2){
                            currentUserRef.collection("eats").document(eatUser!!.eatId!!).update("scanned",false)
                            Toast.makeText(this, "REGISTRADO COM SUCESSO", Toast.LENGTH_SHORT).show()

                        }

                }
                }
            }
    }
    private fun init() {
        if (checkEditText()) {
            hideKeyboard()
            loadUser()


        }
    }
    private fun updateIntent(){
        val intent= Intent(this@GeneratedQr,MainActivity::class.java)
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setImageBitmap(encryptedString: String?) {
        val bitmap = QRCodeHelper.newInstance(this).setContent(encryptedString)
            .setErrorCorrectionLevel(ErrorCorrectionLevel.Q).setMargin(2).qrcOde
        qrCodeImageView.setImageBitmap(bitmap)
    }

    private fun checkEditText(): Boolean {
        if (TextUtils.isEmpty("k")) {
            Toast.makeText(this, "fullName field cannot be empty!", Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty("kl")) {
            Toast.makeText(this, "age field cannot be empty!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


}

