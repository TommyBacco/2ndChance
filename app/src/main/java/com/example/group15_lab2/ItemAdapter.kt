package com.example.group15_lab2

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class ItemAdapter(val items: ArrayList<Item>): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

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

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
       holder.bind(items[position])
    }

    class ViewHolder(v: View,listener: ClickListener) : RecyclerView.ViewHolder(v) {
        private val edit_image: ImageView

        init {
        v.setOnClickListener {
            if(listener != null && adapterPosition != RecyclerView.NO_POSITION)
                    listener.onItemClick(adapterPosition)
        }
        edit_image = v.findViewById(R.id.cardview_edit)
        edit_image.setOnClickListener {
            if(listener != null && adapterPosition != RecyclerView.NO_POSITION)
                listener.onItemEdit(adapterPosition)
        }
    }

        val name: TextView = v.findViewById(R.id.cardview_name)
        private val price: TextView = v.findViewById(R.id.cardview_price)
        private val expire_date: TextView = v.findViewById(R.id.cardview_date)
        private val image: ImageView = v.findViewById(R.id.cardview_image)


        fun bind(item: Item){
            name.text = item.title
            price.text = item.price
            expire_date.text = item.expireDate
            if(item.image != null)
                image.setImageBitmap(item.image)
            else
                image.setImageResource(R.drawable.item_icon)
        }
    }

}