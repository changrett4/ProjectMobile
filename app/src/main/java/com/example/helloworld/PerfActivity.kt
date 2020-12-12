package com.example.helloworld

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.helloworld.model.UserObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_eat_register.*
import kotlinx.android.synthetic.main.activity_perf.*
import kotlinx.android.synthetic.main.activity_perf.selectphoto_button_register
import kotlinx.android.synthetic.main.item_eat.view.*
import org.jetbrains.anko.email
import java.util.*

class PerfActivity : AppCompatActivity() {
    private var mAuth= FirebaseAuth.getInstance()
    private var db= FirebaseFirestore.getInstance()
    private var nome:String?=null
    private var matricula:String?=null
    private var user:UserObject?=null
    private var userUpdate:UserObject?=null

    private var verify:Boolean?=true
    private val  MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE= 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perf)
        showInformations()

        selectphoto_button_register.setOnClickListener {
            Log.d(EatRegister.TAG, "Try to show photo selector")
            if (!permissionIfNeeded()) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
        }
        update_button.setOnClickListener {
            val builder = AlertDialog.Builder(this@PerfActivity)
            builder.setMessage("Deseja Atualizar seus dados?")
                .setCancelable(false)
                .setPositiveButton("Sim") { dialog, id ->
                    perfUpdate()
                    if(verify==true){
                        uploadImageToFirebaseStorage()
                    }

                }
                .setNegativeButton("Não") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
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
            Log.d(EatRegister.TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_user.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

     val bitmapDrawable = BitmapDrawable(bitmap)
    selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/userImage/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(EatRegister.TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(EatRegister.TAG, "File Location: $it")

                  update(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(EatRegister.TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun showInformations(){
        db!!.collection("user").document(mAuth.currentUser!!.uid).get().addOnSuccessListener {
          user= it.toObject(UserObject::class.java)
            edit_username.setText(user!!.username)
            edit_email.setText(user!!.email)
            edit_mat.setText(user!!.id)
            Glide.with(this).load(user!!.image).into(selectphoto_imageview_user)
        }

    }
    private fun  perfUpdate(){
        if (edit_username.text.toString()==null || edit_mat.text.toString()==null){
            Toast.makeText(this,"Por favor preencha seus dados!",Toast.LENGTH_SHORT).show()
            verify=false
        }


    }
    private fun update(link: String){
        nome= edit_username.text.toString()
        matricula=edit_mat.text.toString()
        val data = hashMapOf(
            "username" to nome,
            "id" to matricula,
            "photo" to link
        )
        if(user!!.username==nome && user!!.id==matricula){
            Toast.makeText(this,"dados inalterados",Toast.LENGTH_SHORT).show()
            return
        }
        else {

            db!!.collection("user").document(mAuth.currentUser!!.uid).set(data, SetOptions.merge())
                .addOnSuccessListener {

                    Toast.makeText(this,"Dados atualizados!",Toast.LENGTH_SHORT).show()

                }.addOnFailureListener{
                    Log.d("PerfActivity","an error in update items")
                }
        }
    }

}