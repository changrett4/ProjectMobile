package com.example.helloworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.helloworld.model.EatObject
import com.example.helloworld.model.UserObject
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.drawerlayout

private const val PERMISSION_REQUEST = 10

private val TAG = "MainActivity"
private var btn_go: Button? = null
private var eatList: List<EatObject> = ArrayList()
private val mData: FirebaseFirestore = FirebaseFirestore.getInstance()
private val listAdapter: com.example.helloworld.fragment.ListAdapter =
    com.example.helloworld.fragment.ListAdapter(eatList)


class MainActivity : AppCompatActivity() {
    //menuLateral
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var tool: Toolbar
    private var user:UserObject?=null
    private val mAuth= FirebaseAuth.getInstance()
    private val db= FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()

        recycler_eat.layoutManager = LinearLayoutManager(this)
        recycler_eat.adapter = listAdapter
        //menu lateral
        tool = findViewById(R.id.tbar)
        setSupportActionBar(tbar)

        toggle = ActionBarDrawerToggle(this, drawerlayout, R.string.open_drawer, R.string.close_drawer)
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        navigView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home ->
                        Toast.makeText(this,"você já está em home",Toast.LENGTH_SHORT).show()


                R.id.logout->logout()
                R.id.delete ->delete()


                R.id.nav_perfil -> startActivity(Intent(this@MainActivity,PerfActivity::class.java))

                R.id.nav_help -> Toast.makeText(
                    applicationContext,
                    "Clicou em Help", Toast.LENGTH_SHORT
                ).show()
                R.id.nav_rate -> Toast.makeText(
                    applicationContext,
                    "Clicou em Rate", Toast.LENGTH_SHORT
                ).show()
            }
            true
            //MenuLateral-Fim
        }

    }
    private fun loadUserData(){
        db.collection("user").document(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            user= it.toObject(UserObject::class.java)
            navigView.getHeaderView(0).findViewById<TextView>(R.id.txt_email_lat).text=user?.email
            navigView.getHeaderView(0).findViewById<TextView>(R.id.txt_username_lat).text=user?.username
            Glide.with(this).load(user?.image).into(navigView.getHeaderView(0).findViewById<ImageView>(R.id.image_perf_lat))

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData() {
        loadUserData()
        mData.collection("eats").addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "not sucess to listen")
                return@addSnapshotListener
            }
            eatList = value!!.toObjects(EatObject::class.java)
            listAdapter.list = eatList
            listAdapter.notifyDataSetChanged()
            println(eatList[0])
            Log.d(TAG, "current eat is: $eatList")
        }
    }
    private fun logout(){
        mAuth.signOut()
        updateUI()
    }
    private fun delete(){
        db.collection("eats").document(mAuth.currentUser!!.uid)
            .delete()
            .addOnSuccessListener {

                Toast.makeText(this,"Documento deletado!",Toast.LENGTH_SHORT).show()
                Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e)

                Toast.makeText(this,"Falha ao deletar, tente mais tarde!",Toast.LENGTH_SHORT).show()
            }
        mAuth.currentUser!!.delete().addOnSuccessListener {
            Toast.makeText(this,"conta deletada!",Toast.LENGTH_SHORT).show()
            updateUI()
        }
    }
    private fun updateUI(){
        startActivity(Intent(this@MainActivity,LoginActivity::class.java))
    }
}
