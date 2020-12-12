package com.example.helloworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.helloworld.fragment.ListEatAdm
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_admin_main.*
import com.example.helloworld.model.EatObject
import com.example.helloworld.model.UserObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AdminMainActivity : AppCompatActivity() {
    private var eatList: List<EatObject> = ArrayList()
    private val listAdapter: ListEatAdm = ListEatAdm(eatList)
    private val TAG= "AdminMainActivity"
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var tb: Toolbar
    private val mAuth= FirebaseAuth.getInstance()
    private val db= FirebaseFirestore.getInstance()
    private var user:UserObject?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)
        loadData()

        toggle = ActionBarDrawerToggle(this, drawerlayout, R.string.open_drawer, R.string.close_drawer)
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()
        recycler_eat_adm.layoutManager= LinearLayoutManager(this)
        recycler_eat_adm.adapter = listAdapter


        tb = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> Toast.makeText(
                    applicationContext,
                    "Clicou em Home", Toast.LENGTH_SHORT
                ).show()
                R.id.nav_cardapio ->
                    startActivity(Intent(this@AdminMainActivity,EatRegister::class.java ))
                R.id.scanner ->
                    startActivity(Intent(this@AdminMainActivity,ScanQr::class.java))
                R.id.rel_drawer ->
                    startActivity(Intent(this@AdminMainActivity,RelatorioActivity::class.java))
                R.id.logout->logout()
             R.id.delete ->delete()

                R.id.nav_perfil -> startActivity(Intent(this@AdminMainActivity,PerfActivity::class.java))
                R.id.cad_adm -> startActivity(Intent(this@AdminMainActivity, CreateAdmActivity::class.java))
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
        }
    }
    private fun loadUserData(){
        db.collection("user").document(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            user= it.toObject(UserObject::class.java)
            navView.getHeaderView(0).findViewById<TextView>(R.id.txt_email_lat).text=user?.email
            navView.getHeaderView(0).findViewById<TextView>(R.id.txt_username_lat).text=user?.username
            Glide.with(this).load(user?.image).into(navView.getHeaderView(0).findViewById<ImageView>(R.id.image_perf_lat))
        }
    }
    private fun loadData() {
        loadUserData()
        db.collection("eats").addSnapshotListener { value, e ->
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
                return true
        }

        return super.onOptionsItemSelected(item)

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
        startActivity(Intent(this@AdminMainActivity, LoginActivity::class.java))
    }


    }

