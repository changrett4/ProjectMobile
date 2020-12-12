package com.example.helloworld

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.helloworld.R
import com.example.helloworld.fragment.ListEatAdm
import com.example.helloworld.fragment.ListEatuser
import com.example.helloworld.model.EatObject
import com.example.helloworld.model.EatUserObject
import com.example.helloworld.model.UserObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_list_details.*

class UserListDetails : AppCompatActivity() {
    private val TAG= "UserListDetail"
    private var recieve: String?=null
    private var eatList: List<EatObject> = ArrayList()
    private val listAdapter: ListEatuser = ListEatuser(eatList)
    private var listusereat: List<EatUserObject> = ArrayList()
    private var user:UserObject?=null


    private val mData:FirebaseFirestore= FirebaseFirestore.getInstance()
    private val mAuth:FirebaseAuth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list_details)
        recieve()
        loadPerfData()
        loadData()
        recycler_eatuser.layoutManager= LinearLayoutManager(this)
        recycler_eatuser.adapter= listAdapter
    }

    private fun recieve(){
       recieve= intent.getStringExtra("user_id")
    }
    private fun loadData(){
        mData.collection("user").document(recieve!!).collection("eats").addSnapshotListener{value,e->
            if (e != null) {
                Log.w(TAG, "not sucess to listen")
                return@addSnapshotListener
            }
            listusereat=value!!.toObjects(EatUserObject::class.java)
            for(u in listusereat){
                println(u.eatId)
                mData.collection("eat").whereEqualTo("eatId",u.eatId).addSnapshotListener { value, e ->
                        eatList= value!!.toObjects(EatObject::class.java)
                        listAdapter.list=eatList
                        listAdapter.notifyDataSetChanged()

                }

            }

        }
    }
    private fun loadPerfData(){
        mData.collection("user").document(recieve!!).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "not sucess to listen")
                return@addSnapshotListener
            }
            user= value!!.toObject(UserObject::class.java)
            if(user!!.image!=null){
                Glide.with(this).load(user!!.image).into(selectphoto_imageview_userEat)
            }
            textView8.text= "Email: "+ user?.email
            txt_mat.text="Matricula: "+    user?.id
            textView7.text="Nome: "+ user?.username
        }

    }

}