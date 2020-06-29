package com.example.group15_lab2.OnSaleList

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.group15_lab2.DataClasses.FilterParams
import com.example.group15_lab2.DataClasses.Item
import com.example.group15_lab2.FirebaseRepository
import com.example.group15_lab2.R
import com.squareup.picasso.Picasso
import java.util.*

class AdvertisementsAdapter: RecyclerView.Adapter<AdvertisementsAdapter.ViewHolder>(), Filterable {

    private var items = mutableListOf<Item>()
    private var itemsFullList = mutableListOf<Item>()
    private var itemsFilteredList = mutableListOf<Item>()
    private val size : MutableLiveData<Int> by lazy { MutableLiveData<Int>().apply { value=items.size }}

    fun setItemsList(newList:List<Item>){
        items.clear()
        items.addAll(newList)
        itemsFullList.clear()
        itemsFullList.addAll(newList)
        itemsFilteredList.clear()
        itemsFilteredList.addAll(newList)
        size.value = items.size
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onItemClick(itemID:String?)
    }

    private lateinit var clickListener: ClickListener

    fun setOnItemClickListener(listener: ClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item,parent,false)
        return ViewHolder(v,clickListener)
    }

    override fun getItemCount() = size.value ?: 0
    fun getSize() : LiveData<Int> = size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(v: View, listener: ClickListener) : RecyclerView.ViewHolder(v) {


        init {
            v.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION)
                    listener.onItemClick(items[adapterPosition].ID)
            }
        }

        private val name: TextView = v.findViewById(R.id.cardview_name)
        private val price: TextView = v.findViewById(R.id.cardview_price)
        private val expire_date: TextView = v.findViewById(R.id.cardview_date)
        private val image: ImageView = v.findViewById(R.id.cardview_image)
        private val currency: TextView = v.findViewById(R.id.cardview_currency)
        private val bn_edit:ImageView = v.findViewById(R.id.cardview_bn_edit)
        private val card:View =v.findViewById(R.id.cardview)
        private val category:TextView = v.findViewById(R.id.cardview_category)
        private val status:TextView = v.findViewById(R.id.cardview_status)
        private val ic_bookmark:ImageView = v.findViewById(R.id.bookmark)
        private val ic_bought:ImageView = v.findViewById(R.id.bought)
        private val location:TextView = v.findViewById(R.id.cardview_location)

        fun bind(item: Item){
            name.text = item.title
            price.text = item.price
            expire_date.text = item.expireDate
            currency.text = item.currency
            bn_edit.visibility = View.GONE
            ic_bought.visibility = View.GONE
            category.text = item.category
            status.text = item.status
            location.text = item.location

            Picasso.get()
                .load(item.imageURL.toUri())
                .fit()
                .centerInside()
                .error(R.drawable.item_icon)
                .into(image)

            if (item.interestedUsers.contains(FirebaseRepository.getUserAccount().value?.uid))
                ic_bookmark.visibility = View.VISIBLE
            else
                ic_bookmark.visibility = View.GONE

            val color =
                when (item.status) {
                    "Sold" -> {
                        if(item.soldTo == FirebaseRepository.getUserAccount().value?.uid){
                            ic_bought.visibility = View.VISIBLE
                            ic_bookmark.visibility = View.GONE
                        }
                        Color.parseColor("#FFCDD2") //Red
                    }
                    "No longer on sale" -> Color.parseColor("#F5F5F5") //Gray
                    else -> Color.parseColor("#BBDEFB") //Blue
                }

            card.setBackgroundColor(color)

        }
    }

    private val myFilter:Filter = object : Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Item>()

            if(constraint == null || constraint.isEmpty())
                filteredList.addAll(itemsFilteredList)
            else {
                val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()

                for(item in itemsFilteredList){
                    when {
                        item.title?.toLowerCase(Locale.ROOT)?.startsWith(filterPattern) == true -> filteredList.add(item)
                        item.category?.toLowerCase(Locale.ROOT)?.startsWith(filterPattern) == true -> filteredList.add(item)
                        item.price?.toLowerCase(Locale.ROOT)?.startsWith(filterPattern) == true -> filteredList.add(item)
                        item.status?.toLowerCase(Locale.ROOT)?.startsWith(filterPattern) == true -> filteredList.add(item)
                        item.expireDate?.toLowerCase(Locale.ROOT)?.startsWith(filterPattern) == true -> filteredList.add(item)
                        item.location?.toLowerCase(Locale.ROOT)?.startsWith(filterPattern) == true -> filteredList.add(item)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            items.clear()
            items.addAll((results?.values) as List<Item>)
            size.value = items.size
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter {
       return myFilter
    }

    fun filterByCategoryPriceAndLocation(filterParams: FilterParams){
        val min = filterParams.from_price ?: 0F
        val max = filterParams.to_price ?: Float.MAX_VALUE
        val list = mutableListOf<Item>()

        for(item in itemsFullList){
            val price:Float =
                if(item.price == null || item.price == "" || item.price == "-") 0F
                else item.price!!.toFloat()

            if( (filterParams.category == "ALL" || item.category == filterParams.category)
                && price >= min && price <= max
                && (filterParams.location == "NONE"
                        || item.location?.toLowerCase(Locale.ROOT)?.startsWith(filterParams.location) == true))
                list.add(item)
        }

        itemsFilteredList.clear()
        itemsFilteredList.addAll(list)
        items.clear()
        items.addAll(list)
        size.value = items.size
        notifyDataSetChanged()
    }


}