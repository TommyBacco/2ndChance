package com.example.group15_lab2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ItemAdapter(val users:ArrayList<Item>): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {


    interface ClickListener {
        fun onItemClick(position: Int)
        fun onItemEdit(position:Int)
    }

    private lateinit var clickListener: ClickListener

    fun setOnItemClickListener(listener: ClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item,parent,false)
       return ViewHolder(v,clickListener)
    }


    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
       holder.bind(users[position])
    }

    class ViewHolder(v: View,listener: ClickListener) : RecyclerView.ViewHolder(v) {
        val edit_image:ImageView

        init {
        v.setOnClickListener {
            if(listener !=null && adapterPosition != RecyclerView.NO_POSITION)
                    listener.onItemClick(adapterPosition)
        }
        edit_image = v.findViewById(R.id.cardview_edit)
        edit_image.setOnClickListener {
            if(listener !=null && adapterPosition != RecyclerView.NO_POSITION)
                listener.onItemEdit(adapterPosition)
        }
    }

        val name:TextView = v.findViewById(R.id.cardview_name)
        val price:TextView = v.findViewById(R.id.cardview_price)
        val image:ImageView = v.findViewById(R.id.cardview_image)


        fun bind(item:Item){
            name.text = item.name
            price.text = item.price
            image.setImageResource(item.image)


        }
    }

    fun addItem(u:Item){
        users.add(u)
        this.notifyItemChanged(users.size-1)
    }
}