package com.example.mindon.activity

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    /*
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var nome: TextView
    private lateinit var email: TextView
    private lateinit var rbBasico: RadioButton
    private lateinit var rbInter: RadioButton
    private lateinit var rbAvancado: RadioButton
    private lateinit var imagemPerfil: CircleImageView
    private val permisssaoCamera= arrayOf(Manifest.permission.CAMERA) //array com as permições que o app precisará (camera)
    private val permisssaoGaleria = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE) //array com as permições que o app precisará (Galeria)
    private val SELECAO_CAMERA = 100
    private val SELECAO_GALERIA = 200
    private var imagem: Bitmap? = null
    private lateinit var storageReference : StorageReference
    private lateinit var u:Usuario
    private lateinit var progressImage: ProgressBar
    private lateinit var pd  : ProgressDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pd = ProgressDialog(this)
        pd.setMessage("Recuperando Dados ...")
        pd.show()

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        rbBasico = findViewById(R.id.radio_basico)
        rbInter = findViewById(R.id.radio_inter)
        rbAvancado = findViewById(R.id.radio_avancado)
        imagemPerfil = findViewById(R.id.imagem_perfil_editar)
        storageReference = FirebaseStorage.getInstance().reference
        progressImage = findViewById(R.id.progressImage)

        carregarDados()

        alterar_imagem.setOnClickListener {
            mudarFoto()
        }

        btn_atualizar.setOnClickListener{
            editar()
        }
        btn_cancelar.setOnClickListener {
            finish()
        }
        alterar_senha.setOnClickListener {
            alterarSenha()
        }
    }

    fun alterarSenha(){
        val builder = AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog)
        builder.setTitle("Confirmação para alterar senha")
        builder.setMessage("Enviaremos um email para alteração de sua senha")
        builder.setCancelable(false)
        builder.setPositiveButton("Alterar Senha" ){ dialogInterface, i ->
            resetar()
        }
        builder.setNegativeButton("CANCELAR"){dialogInterface, i ->

        }

        val dialog = builder.create()
        dialog.show()
    }

    fun resetar(){
        if(!txt_email_perfil.text.toString().isEmpty()){
            val pd = ProgressDialog(this)
            pd.setMessage("Enviando email ...")
            pd.show()
            auth.sendPasswordResetEmail(txt_email_perfil.text.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this,"Email enviado com sucesso!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,HomeActivity::class.java))
                    pd.dismiss()
                }else{
                    Toast.makeText(this, "Erro ao enviar o email, tente novamente.", Toast.LENGTH_SHORT).show();
                    pd.dismiss()
                }
            }
        }
    }

    fun mudarFoto() {
        val modes = arrayOf("Tirar da camera", "Selecione da galeria")

        //Cria uma AlertDialog
        val builder = AlertDialog.Builder(this)

        //Seta o título
        builder.setTitle("Selecionar uma foto") //Seta os itens de opção
            .setItems(modes) { dialogInterface, i ->

                //Verifica o índice do item
                when (i) {
                    0 -> //abre as permissoes para camera/ se o usuario tiver permissao irá abrir a camera
                        if (Permissao.validarPermissao(permisssaoCamera, this, SELECAO_CAMERA)) {
                            if (baseContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                if (intent.resolveActivity(packageManager) != null) {
                                    startActivityForResult(intent, SELECAO_CAMERA)
                                }
                            } else {
                                //notFound
                            }
                        }
                    1 -> {
                        //abre as permissoes para galeria/ se o usuario tiver permissao irá abrir a galeria
                        if (Permissao.validarPermissao(permisssaoGaleria, this, SELECAO_GALERIA)) {
                            val intent =
                                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivityForResult(intent, SELECAO_GALERIA)
                            }
                        }
                    }
                }
            }
            .create()
            .show()
    }

    private fun carregarDados(){
        val user =auth.currentUser
        val usuario = database.child("usuarios").child(auth.uid!!)


        user?.let{
            usuario.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    u = dataSnapshot.getValue(Usuario::class.java)!!

                    if(!u.foto.isEmpty()){
                        Picasso.get()
                            .load(u?.foto)
                            .into(imagemPerfil)
                        progressImage.visibility = View.GONE
                    }else{
                        progressImage.visibility = View.GONE
                    }

                    nome = findViewById(R.id.txt_nome_perfil)
                    email = findViewById(R.id.txt_email_perfil)

                    nome.text = u.nome
                    email.text = u.email

                    if(u.nivel.equals("basico")){
                        rbBasico.isChecked = true
                        rbInter.isChecked = false
                        rbAvancado.isChecked = false
                    }else if(u.nivel.equals("inter")){
                        rbBasico.isChecked = false
                        rbInter.isChecked = true
                        rbAvancado.isChecked = false
                    }else if(u.nivel.equals("avancado")){
                        rbBasico.isChecked = false
                        rbInter.isChecked = false
                        rbAvancado.isChecked = true
                    }
                    pd.dismiss()
                }


            })

        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permissaoResultado in grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaPermissao()

            }else if (requestCode == SELECAO_GALERIA) { //se foi aceita a permissao ira abrir a opcao da camera ou galeria
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, SELECAO_GALERIA)
                }

            } else if (requestCode == SELECAO_CAMERA) {
                if (baseContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivityForResult(intent, SELECAO_CAMERA)
                    }
                } else {
                    //notFound
                }
            }

        }
    }

    fun alertaPermissao(){
        val builder = AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog)
        builder.setTitle("Permissões Negadas")
        builder.setMessage("Para a melhor utilização do app é necessário aceitar as permissões")
        builder.setCancelable(false)
        builder.setPositiveButton("Confirmar" ){ dialogInterface, i ->  }



        val dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            try {
                when (requestCode) {
                    SELECAO_CAMERA -> {
                        imagem = data?.extras?.get("data") as Bitmap
                    }
                    SELECAO_GALERIA -> {
                        val localImagemSelecionada = data?.data
                        imagem = MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            localImagemSelecionada
                        )

                    }
                }
                if(imagem != null){
                    //recuperar dados da imagem para o firebase
                    imagemPerfil.setImageBitmap(imagem)
                }
            } catch (e:java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun editar(){
        val pd = ProgressDialog(this)
        pd.setMessage("Salvando...")
        pd.show()

        val user =auth.currentUser!!
        val usuario = Usuario()

        usuario.email =email.text.toString()
        usuario.nome = nome.text.toString()
        usuario.foto = u.foto

        if(rbBasico.isChecked){
            usuario.nivel = "basico"
        }else if(rbInter.isChecked){
            usuario.nivel = "inter"
        }else if(rbAvancado.isChecked){
            usuario.nivel = "avancado"
        }

        database.child("usuarios").child(user.uid!!).setValue(usuario)
        if (imagem!=null){
            salvarFoto(database.child("usuarios").child(user.uid))
        }else{
            finish()
        }
    }

    fun salvarFoto(ref:DatabaseReference){
        //recuperar dados da imagem para o firebase
        val baos =  ByteArrayOutputStream()

        imagem?.compress(Bitmap.CompressFormat.JPEG, 70, baos)

        val dadosImagem = baos.toByteArray()

        //Salvar no Firebase
        val imagemRef = storageReference
            .child("imagens")
            .child("perfil")
            .child(auth!!.currentUser?.uid.toString())
            .child("perfil.jpeg")

        val uploadTask = imagemRef.putBytes(dadosImagem)
        uploadTask.addOnFailureListener{
            //Se houve erro no upload da imageFile
            Toast.makeText(this, "Erro ao salvar  a foto", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            imagemRef.downloadUrl.addOnSuccessListener {
                ref.child("foto").setValue(it.toString())
            }
            //Se o upload da imageFile foi realizado com sucesso
            Toast.makeText(this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

 */
}

