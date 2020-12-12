package com.example.helloworld.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helloworld.R
import com.example.helloworld.UserListDetails
import com.example.helloworld.model.UserObject
import kotlinx.android.synthetic.main.item_eat.view.*
class ListUsers(var list:List<UserObject>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class DescViewHolder(item: View): RecyclerView.ViewHolder(item){
        fun bind(user: UserObject){
            itemView.textView_name.text=user.username
            itemView.textView_bio.text= user.id
            if(user.image!=null){
                Glide.with(itemView.context).load(user.image).into(itemView.imageView_profile_picture)
            }

            itemView.setOnClickListener{
                val intent= Intent(itemView.context, UserListDetails::class.java)
                intent.putExtra("user_id",user.authId)
                itemView.context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_eat,parent,false)
        return DescViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DescViewHolder).bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}