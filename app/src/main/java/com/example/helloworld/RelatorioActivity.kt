package com.example.helloworld

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.fragment.ListUsers
import com.example.helloworld.model.EatObject
import com.example.helloworld.model.EatUserObject
import com.example.helloworld.model.UserObject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_eat_register.*
import kotlinx.android.synthetic.main.activity_relatorio.*
import java.util.*
import kotlin.collections.ArrayList

class RelatorioActivity : AppCompatActivity() {
    private val TAG= "RelatorioActivity"
    private var userList: List<UserObject> = ArrayList()
    private val listAdapter:ListUsers= ListUsers(userList)
    private val mData= FirebaseFirestore.getInstance()
    private var subListUserEat:MutableList<EatUserObject> = mutableListOf()
    private var subListUser:MutableList<UserObject> = mutableListOf()
    private var eatList:List<EatObject> = ArrayList()
    private var subListEats: MutableList<EatObject> = mutableListOf()
    private var count=0
    private var subcount=0
    private var valid=false
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relatorio)
        recycler_users.layoutManager= LinearLayoutManager(this)
        recycler_users.adapter= listAdapter
        loadEats()
        loadData()
       // search_user.setOnClickListener{
          //  perfSearch()
       //     if(valid==true){searchUserByDate(txt_date_rel.text.toString())}
//
      //  }
       // search_all.setOnClickListener{loadData()}

          //  date_picker_rel.setOnClickListener{
            //    val dpd = DatePickerDialog(this,
              //      R.style.DialogTheme,
                //    DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->

                  //      txt_date_rel.setText(""+ mDay + "" + mMonth + "" + mYear)
                    //},
                    //year,
                    //month,
                    //day)

                //dpd.show()

            //}


    }
    private fun perfSearch(){
        //if(txt_date_rel.text=="escolha uma data"){
          //  valid=false
            //Toast.makeText(this,"insira uma data!",Toast.LENGTH_SHORT).show()
            //return
        //}
        //else{
          //  valid=true
        //}
    }
    private fun loadData() {
       // loadUserData()
        mData.collection("user").whereEqualTo("role","user").addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "not sucess to listen")
                return@addSnapshotListener
            }
            userList = value!!.toObjects(UserObject::class.java)
            listAdapter.list = userList
            listAdapter.notifyDataSetChanged()
            println(userList[0])
            Log.d(TAG, "current user is: $userList")
        }
    }
    private fun loadEats(){
        mData.collection("eats").addSnapshotListener { value, e->
            if (e != null) {
                Log.w(TAG, "not sucess to listen")
                return@addSnapshotListener
            }
            eatList=value!!.toObjects(EatObject::class.java)
        }
    }

    private fun searchUserByDate(date:String){
    subListUserEat.clear()
    subListEats.clear()
            mData.collection("eats").whereEqualTo("date",date).addSnapshotListener{value,e->
                subListEats= value!!.toObjects(EatObject::class.java)
                for(i in userList){
                   println(subListEats[count].description+subListEats[count].date)
                    mData.collection("user").document(i.authId!!).collection("eats").whereEqualTo("eatId",subListEats[count].description+subListEats[count].date).addSnapshotListener{value,e->
                        if (e != null) {
                            Log.w(TAG, "not sucess to listen")
                            return@addSnapshotListener
                        }
                        subListUserEat= value!!.toObjects(EatUserObject::class.java)

                        for(u in subListUserEat){
                            println("this is the subListUserEat"+u.uid)
                            mData.collection("user").whereEqualTo("authId",u.uid).addSnapshotListener{value,e->
                                if (e != null) {
                                    Log.w(TAG, "not sucess to listen")
                                    return@addSnapshotListener
                                }
                                    subListUser= value!!.toObjects(UserObject::class.java)
                                    listAdapter.list = subListUser
                                    listAdapter.notifyDataSetChanged()
                                    println(subListUser[subcount])
                                    subcount=subcount+1
                            }
                        }
                        count=count+1
                    }

                }
            }
            count=0
            subcount=0
        //search_all.isClickable=true
    }



}