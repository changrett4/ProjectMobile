package com.example.helloworld

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.helloworld.model.EatObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_eat_register.*
import java.util.*

class EatRegister : AppCompatActivity() {
    companion object {
        val TAG = "EatRegister"
    }

    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var mData: FirebaseFirestore? = FirebaseFirestore.getInstance()
    private val mStorage = FirebaseStorage.getInstance()
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=100
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)


    var eat: EatObject? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat_register)

        date_picker_eat.setOnClickListener {
            val dpd = DatePickerDialog(this,
                R.style.DialogTheme,
                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->

                    txt_date.setText("" + mDay + "" + mMonth + "" + mYear)
                },
                year,
                month,
                day)

            dpd.show()

        }
        register_eat.setOnClickListener {

            performRegister()
            uploadImageToFirebaseStorage()



        }
        selectphoto_button_register.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")
            if(!permissionIfNeeded()) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
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

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/eatImage/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                     registerEat(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun performRegister() {
        val eatname = eatname_edittext_register.text.toString()
        val eatDetail = eatdetail_edittext_register.text.toString()

        if (eatname.isEmpty() || eatDetail.isEmpty()) {
            Toast.makeText(this, "Preencha os campos!", Toast.LENGTH_SHORT).show()
            return
        }


    }
    private fun registerEat(link: String){
        val date = txt_date.text.toString()
        val eatname = eatname_edittext_register.text.toString()
        val eatDetail = eatdetail_edittext_register.text.toString()
        eat = EatObject(eatname, eatDetail, mAuth!!.uid + "", date = date, image = link)
        mData!!.collection("eats").document(eatname + date!!)
            .set(eat!!).addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added ")

                Toast.makeText(this, "refeição registrada!",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "erro no registro",Toast.LENGTH_SHORT).show()
            }
    }
}