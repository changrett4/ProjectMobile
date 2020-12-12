package com.example.helloworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.helloworld.model.EatObject
import com.google.firebase.firestore.FirebaseFirestore

class EatDetails : AppCompatActivity() {
    private val TAG="EatDetails"
    private var eatId: String?=null
    private var btn: Button?=null
    private val mData: FirebaseFirestore= FirebaseFirestore.getInstance()
    private var eatInf: EatObject?=null
    private var description: TextView?=null
    private var details: TextView?=null
    private var image: ImageView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat_details)
        recive()
        init()

    }

    private fun recive(){
        eatId= intent.getStringExtra("eat_id")

    }
    private fun init(){
        btn= findViewById(R.id.btn_goto_qr) as Button
        description= findViewById(R.id.text_desc) as TextView
        details= findViewById(R.id.text_details) as TextView
        image= findViewById(R.id.image_detail) as ImageView

        searchEatAndSetText()
        btn!!.setOnClickListener{onClick()}
    }
    private fun searchEatAndSetText(){
        mData.collection("eats").document(eatId!!).addSnapshotListener{value,e->
            if(e!=null){
                Log.w(TAG,"not sucess to listen")
                return@addSnapshotListener
            }
            eatInf= value!!.toObject(EatObject::class.java)
            description?.setText(eatInf?.description)
            details?.setText(eatInf?.details)
            Glide.with(this).load(eatInf?.image).into(image!!)

        }

    }
    private fun onClick(){
        val intent= Intent(this@EatDetails,GeneratedQr::class.java )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("eat_id",eatId)
        startActivity(intent)
    }

}