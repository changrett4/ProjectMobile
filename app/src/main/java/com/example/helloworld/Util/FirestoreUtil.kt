package com.example.helloworld.Util

object FirestoreUtil {
  /*  private val mDatabase: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var eat: EatObject?=null

    fun addEatListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return mDatabase.collection("eats")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("Firestore", "Eat Listener error", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    items.add(EatItem(it.toObject(EatObject::class.java)!!, it.id, context))
                    eat=it.toObject(EatObject::class.java)


                }
                onListen(items)
            }
    }
    fun removeListener(registration: ListenerRegistration){
        registration.remove()
    }

   */
}