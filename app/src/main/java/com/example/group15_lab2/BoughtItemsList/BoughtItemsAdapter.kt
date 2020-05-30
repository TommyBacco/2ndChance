package com.example.group15_lab2.BoughtItemsList

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.group15_lab2.DataClasses.Item
import com.example.group15_lab2.R
import com.squareup.picasso.Picasso

class BoughtItemsAdapter: RecyclerView.Adapter<BoughtItemsAdapter.ViewHolder>() {

    private var items = mutableListOf<Item>()

    private val size: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            value = items.size
        }
    }

    fun setItemsList(newList: List<Item>) {
        items.clear()
        items.addAll(newList)
        size.value = items.size
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onItemClick(itemID: String?)
        fun onItemReview(itemID:String?)
    }

    private lateinit var clickListener: ClickListener

    fun setOnItemClickListener(listener: ClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false)
        return ViewHolder(v, clickListener)
    }

    override fun getItemCount() = size.value ?: 0
    fun getSize(): LiveData<Int> = size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(v: View, listener: ClickListener) : RecyclerView.ViewHolder(v) {

        private val bn_review: ImageView

        init {
            v.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    listener.onItemClick(items[adapterPosition].ID)
            }

            bn_review = v.findViewById(R.id.cardview_bn_rating)
            bn_review.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION){
                    listener.onItemReview(items[adapterPosition].ID)
                }
            }
        }

        private val name: TextView = v.findViewById(R.id.cardview_name)
        private val price: TextView = v.findViewById(R.id.cardview_price)
        private val expire_date: TextView = v.findViewById(R.id.cardview_date)
        private val image: ImageView = v.findViewById(R.id.cardview_image)
        private val currency: TextView = v.findViewById(R.id.cardview_currency)
        private val bn_edit: ImageView = v.findViewById(R.id.cardview_bn_edit)
        private val category: TextView = v.findViewById(R.id.cardview_category)
        private val status: TextView = v.findViewById(R.id.cardview_status)

        fun bind(item: Item) {
            name.text = item.title
            price.text = item.price
            expire_date.text = item.expireDate
            currency.text = item.currency
            bn_edit.visibility = View.GONE
            category.text = item.category
            status.text = item.status
            bn_review.visibility = View.VISIBLE

            Picasso.get()
                .load(item.imageURL.toUri())
                .fit()
                .centerInside()
                .error(R.drawable.item_icon)
                .into(image)
        }
    }
}