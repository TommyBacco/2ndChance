package com.example.group15_lab2.ShowUserRating

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.group15_lab2.DataClasses.Rating
import com.example.group15_lab2.R

class ShowUserRaringAdapter: RecyclerView.Adapter<ShowUserRaringAdapter.ViewHolder>() {

    private var ratings = mutableListOf<Rating>()

    private val size: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            value = ratings.size
        }
    }

    fun setItemsList(newList: List<Rating>) {
        ratings.clear()
        ratings.addAll(newList)
        size.value = ratings.size
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_review, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = size.value ?: 0
    fun getSize(): LiveData<Int> = size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ratings[position])
    }

     class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val user_nickname: TextView = v.findViewById(R.id.rate_user_nickname)
        private val date: TextView = v.findViewById(R.id.rate_date)
        private val comment: TextView = v.findViewById(R.id.rate_comment)
        private val ratingBar: RatingBar = v.findViewById(R.id.rate_ratingBar)

        fun bind(rating: Rating) {
            user_nickname.text = rating.buyer_nickname
            date.text = rating.date
            comment.text = rating.comment
            ratingBar.rating = rating.rating
        }
    }
}