package com.example.helloworld

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.helloworld.model.EatObject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_eat_det_adm.*

class EatDetAdm : AppCompatActivity() {
    private var eatId: String?=null
    private val TAG= "EatDetAdm"
    private val mData: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var eatInf: EatObject?=null
    private var mProgress:ProgressDialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat_det_adm)
        recive()
        initialize()
        mProgress=ProgressDialog(this)
    }
    private fun recive(){
        eatId= intent.getStringExtra("eat_id")

    }
    private fun initialize(){

        btn_exc_eat.setOnClickListener{
            val builder = AlertDialog.Builder(this@EatDetAdm,R.style.AlertDialogStyle)

            builder.setMessage("Deseja deletar essa refeição?")
                .setCancelable(false)
                .setPositiveButton("Sim") { dialog, id ->

                    deleteEat()
                }
                .setNegativeButton("Não") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
        searchEatAndSetText()
    }
    private fun searchEatAndSetText(){
        mData.collection("eats").document(eatId!!).addSnapshotListener{value,e->
            if(e!=null){
                Log.w(TAG,"not sucess to listen")
                return@addSnapshotListener
            }
            eatInf= value!!.toObject(EatObject::class.java)

            text_desc.setText(eatInf?.description)
            text_details?.setText(eatInf?.details)
            Glide.with(this).load(eatInf?.image).into(image_detail)

        }
    }
    private fun deleteEat(){
        mProgress?.setMessage("deletando...")
        mProgress?.show()
        mData.collection("eats").document(eatId!!)
            .delete()
            .addOnSuccessListener {
                mProgress?.hide()
                Toast.makeText(this,"Documento deletado!",Toast.LENGTH_SHORT).show()
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                updateUI()
            }

            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e)
                mProgress?.hide()
                Toast.makeText(this,"Falha ao deletar, tente mais tarde!",Toast.LENGTH_SHORT).show()
                updateUI()
            }
    }
    private fun updateUI(){
        startActivity(Intent(this@EatDetAdm,AdminMainActivity::class.java))
    }


}