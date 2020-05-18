package com.example.group15_lab2.OnSaleList

import android.content.ClipData
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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