package com.example.helloworld.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helloworld.EatDetails
import com.example.helloworld.R
import com.example.helloworld.model.EatObject
import kotlinx.android.synthetic.main.item_eat.view.*


class ListAdapter(var list: List<EatObject>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class DescViewHolder(item:View):RecyclerView.ViewHolder(item){
        fun bind(eat:EatObject){
            itemView.textView_name.text= eat.description
            itemView.textView_bio.text= eat.details
            Glide.with(itemView.context).load(eat.image).into(itemView.imageView_profile_picture)
            itemView.setOnClickListener{
                val intent= Intent(itemView.context, EatDetails::class.java)


                intent.putExtra("eat_id",eat.description+eat.date)
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