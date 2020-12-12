package com.example.helloworld.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helloworld.R
import com.example.helloworld.UserListDetails
import com.example.helloworld.model.EatObject
import com.example.helloworld.model.EatUserObject
import com.example.helloworld.model.UserObject
import kotlinx.android.synthetic.main.item_eat.view.*
import kotlinx.android.synthetic.main.item_eatuser.view.*

class ListEatuser(var list:List<EatObject>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class DescViewHolder(item: View): RecyclerView.ViewHolder(item){
        fun bind(user: EatObject){
            itemView.textView_eatUser_list.text=user.description
            itemView.textView_date.text= user.date

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_eatuser,parent,false)
        return DescViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DescViewHolder).bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}