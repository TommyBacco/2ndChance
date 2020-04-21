package com.example.group15_lab2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(val users:ArrayList<User>): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
       return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_item,parent,false))
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
       holder.bind(users[position])
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val name:TextView = v.findViewById(R.id.name)
        val surname:TextView = v.findViewById(R.id.surname)

        fun bind(u:User){
            name.text= u.name
            surname.text = u.surname
        }
    }

     fun addItem(u:User){
        users.add(u)
        this.notifyItemChanged(users.size-1)
    }
}