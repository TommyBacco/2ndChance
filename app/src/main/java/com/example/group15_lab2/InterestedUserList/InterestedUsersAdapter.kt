package com.example.group15_lab2.InterestedUserList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.group15_lab2.DataClasses.User
import com.example.group15_lab2.R
import com.squareup.picasso.Picasso

class InterestedUsersAdapter : RecyclerView.Adapter<InterestedUsersAdapter.ViewHolder>() {

    private var users = mutableListOf<User>()
    private val size : MutableLiveData<Int> by lazy { MutableLiveData<Int>().apply { value=users.size }}
    private lateinit var clickListener: ClickListener

    fun setUsersList(newList:List<User>){
        users.clear()
        users.addAll(newList)
        size.value = users.size
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onUserClick(userID: String?)
    }

    fun setOnItemClickListener(listener: ClickListener) {
        clickListener = listener
    }

    override fun getItemCount() = size.value ?: 0
    fun getSize() : LiveData<Int> = size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_user,parent,false)
        return ViewHolder(v,clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    inner class ViewHolder(v: View, listener: ClickListener) : RecyclerView.ViewHolder(v) {

        init {
            v.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onUserClick(users[adapterPosition].ID)
                }
            }
        }

        private val nickname: TextView = v.findViewById(R.id.cardview_user_nickname)
        private val image: ImageView = v.findViewById(R.id.cardview_user_image)
        private val position:TextView = v.findViewById(R.id.cardview_user_position)


        fun bind(user: User) {
            nickname.text = user.nickname
            val pos:Int = adapterPosition + 1
            position.text = pos.toString()
            Picasso.get()
                .load(user.avatarURL.toUri())
                .fit()
                .centerInside()
                .error(R.drawable.user_icon)
                .into(image)
        }
    }

}