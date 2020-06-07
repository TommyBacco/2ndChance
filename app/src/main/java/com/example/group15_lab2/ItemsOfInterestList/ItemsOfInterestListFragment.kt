package com.example.group15_lab2.ItemsOfInterestList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.group15_lab2.MainActivity.MainActivity
import com.example.group15_lab2.R
import kotlinx.android.synthetic.main.fragment_items_of_interest.*

class ItemsOfInterestListFragment : Fragment() {

    private val myViewModel: ItemsOfInterestListVM by viewModels()
    private val myAdapter: ItemsOfInterestAdapter by lazy { ItemsOfInterestAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_items_of_interest,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = resources.getString(R.string.itemsOfInterest_title)
        (activity as MainActivity).setToolbarTitle(title)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = myAdapter

        myViewModel.getItemsOfInterest().observe(viewLifecycleOwner, Observer {
            myAdapter.setItemsList(it)
        })

        myAdapter.getSize().observe(viewLifecycleOwner, Observer {size ->
            if(size != 0)
                emptyMessage.visibility = View.GONE
            else
                emptyMessage.visibility = View.VISIBLE
        })

        myAdapter.setOnItemClickListener(object : ItemsOfInterestAdapter.ClickListener {
            override fun onItemClick(itemID: String?) {
                val bundle = bundleOf(Pair("group15.lab3.ITEM_ID", itemID))
                findNavController().navigate(
                    R.id.action_nav_itemsOfInterest_to_nav_itemDetails, bundle)
            }
        })

    }
}