package com.example.group15_lab2.OnSaleList

import android.content.ClipData
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.group15_lab2.DataClass.Item
import com.example.group15_lab2.FirebaseRepository
import com.example.group15_lab2.ItemAdapter
import com.example.group15_lab2.ItemFirestoreAdapter
import com.example.group15_lab2.R
import kotlinx.android.synthetic.main.fragment_on_sale_list.*

class OnSaleListFragment : Fragment() {

    private val myViewModel: OnSaleListVM by viewModels()
    private val myAdapter:ItemAdapter by lazy {ItemAdapter()}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_on_sale_list,container,false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.search_button)


        if(searchItem != null){
            val searchView  = searchItem.actionView as SearchView

            searchView.setOnCloseListener(object : SearchView.OnCloseListener {

                override fun onClose(): Boolean {
                    myViewModel.getAdvertisements().observe(viewLifecycleOwner, Observer {
                        myAdapter.setItemsList(it)
                        myAdapter.notifyDataSetChanged()
                    })
                    return true
                }
            })

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {


                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val display : MutableList<Item> = mutableListOf()

                    if(newText!!.isNotEmpty()){
                        val search = newText.toLowerCase()
                        val items: MutableLiveData<List<Item>> = myViewModel.getAdvertisements()

                        for( item: Item in items.value!!){

                            if(item.title!= null && item.title?.toLowerCase()?.contains(search)!!){
                                display.add(item)
                            }else if(item.category!=null && item.category?.toLowerCase()?.contains(search)!!){
                                display.add(item)
                            }else if(item.subcategory!=null && item.subcategory?.toLowerCase()?.contains(search)!!){
                                display.add(item)
                                }else if(item.price!=null && item.price?.toLowerCase()?.contains(search)!!){
                                display.add(item)
                            }else if(item.currency!=null && item.currency?.toLowerCase()?.contains(search)!!) {
                                display.add(item)
                            }else if(item.location!=null && item.location?.toLowerCase()?.contains(search)!!){
                                display.add(item)
                            }else if(item.deliveryType!=null && item.deliveryType?.toLowerCase()?.contains(search)!!){
                                display.add(item)
                            }else if(item.description!=null && item.description?.toLowerCase()?.contains(search)!!){
                                display.add(item)

                            }

                        }
                        myAdapter.setItemsList(display)
                        myAdapter.notifyDataSetChanged()
                    }else{
                        display.clear()
                        myViewModel.getAdvertisements().observe(viewLifecycleOwner, Observer {
                            myAdapter.setItemsList(it)
                            myAdapter.notifyDataSetChanged()
                        })
                    }

                    return true
                }
            })

        }
        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = myAdapter

        myViewModel.getAdvertisements().observe(viewLifecycleOwner, Observer {
            myAdapter.setItemsList(it)
            myAdapter.notifyDataSetChanged()
        })

        myAdapter.getSize().observe(viewLifecycleOwner, Observer {size ->
            if(size != 0)
                emptyMessage.visibility = View.GONE
            else
                emptyMessage.visibility = View.VISIBLE
        })

        myAdapter.setOnItemClickListener(object : ItemAdapter.ClickListener {
            override fun onItemClick(itemID: String?) {
                val bundle = bundleOf(Pair("group15.lab3.ITEM_ID", itemID))
                findNavController().navigate(
                    R.id.action_nav_advertisements_to_nav_itemDetails, bundle)
            }
        })

    }

}




