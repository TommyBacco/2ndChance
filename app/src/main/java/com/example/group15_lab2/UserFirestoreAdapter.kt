package com.example.group15_lab2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.group15_lab2.DataClass.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.squareup.picasso.Picasso

class UserFirestoreAdapter(options: FirestoreRecyclerOptions<User>) :
    FirestoreRecyclerAdapter<User, UserFirestoreAdapter.ViewHolder>(options) {

    private val size : MutableLiveData<Int> by lazy { MutableLiveData<Int>().apply { value=snapshots.size }}

    interface ClickListener {
        fun onUserClick(userID: String?)
    }

    fun getSize(): LiveData<Int> = size

    override fun onDataChanged() {
        super.onDataChanged()
        size.value = snapshots.size
    }


    private lateinit var clickListener: ClickListener

    fun setOnItemClickListener(listener: ClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_user, parent, false)
        return ViewHolder(v, clickListener, snapshots)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: User) {
        holder.bind(model)
    }

    class ViewHolder(v: View, listener: ClickListener, snapshots: ObservableSnapshotArray<User>)
        : RecyclerView.ViewHolder(v) {

        init {
            v.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val user = snapshots[adapterPosition]
                    listener.onUserClick(user.ID)
                }
            }
        }

        private val nickname: TextView = v.findViewById(R.id.cardview_user_nickname)
        private val image: ImageView = v.findViewById(R.id.cardview_user_image)


        fun bind(user: User) {
            nickname.text = user.nickname
            Picasso.get()
                .load(user.avatarURL.toUri())
                .fit()
                .centerInside()
                .error(R.drawable.user_icon)
                .into(image)
        }
    }

}