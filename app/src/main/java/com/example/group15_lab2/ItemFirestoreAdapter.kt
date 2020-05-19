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
import com.example.group15_lab2.DataClass.Item
import com.example.group15_lab2.DataClass.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.squareup.picasso.Picasso


class ItemFirestoreAdapter(options: FirestoreRecyclerOptions<Item>) :
    FirestoreRecyclerAdapter<Item, ItemFirestoreAdapter.ViewHolder>(options) {

    private val size : MutableLiveData<Int> by lazy { MutableLiveData<Int>().apply { value=snapshots.size }}

    interface ClickListener {
        fun onItemClick(itemID:String?)
        fun onItemEdit(itemID:String?)
    }

    fun getSize() : LiveData<Int> = size

    override fun onDataChanged() {
        super.onDataChanged()
        size.value = snapshots.size
    }


    private lateinit var clickListener: ClickListener

    fun setOnItemClickListener(listener: ClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item,parent,false)
       return ViewHolder(v,clickListener,snapshots)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Item) {
        holder.bind(model)
    }

    class ViewHolder(v: View, listener: ClickListener,
                     snapshots: ObservableSnapshotArray<Item>) : RecyclerView.ViewHolder(v) {
        private val bn_edit: ImageView

        init {
            v.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION){
                    val item = snapshots[adapterPosition]
                    listener.onItemClick(item.ID)
                }

            }
             bn_edit =  v.findViewById(R.id.cardview_bn_edit)
            bn_edit.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION){
                    val item = snapshots[adapterPosition]
                    listener.onItemEdit(item.ID)
                }
            }

    }
        private val name: TextView = v.findViewById(R.id.cardview_name)
        private val price: TextView = v.findViewById(R.id.cardview_price)
        private val expire_date: TextView = v.findViewById(R.id.cardview_date)
        private val image: ImageView = v.findViewById(R.id.cardview_image)
        private val currency:TextView = v.findViewById(R.id.cardview_currency)
        private val category:TextView = v.findViewById(R.id.cardview_category)

        fun bind(item: Item){
            name.text = item.title
            price.text = item.price
            expire_date.text = item.expireDate
            currency.text = item.currency
            category.text = item.category

            Picasso.get()
                .load(item.imageURL.toUri())
                .fit()
                .centerInside()
                .error(R.drawable.item_icon)
                .into(image)

        }
    }

}