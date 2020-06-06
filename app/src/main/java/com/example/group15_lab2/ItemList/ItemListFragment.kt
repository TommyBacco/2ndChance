package com.example.group15_lab2.ItemList

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
import kotlinx.android.synthetic.main.fragment_item_list.*

class ItemListFragment : Fragment() {

    private val myAdapter: MyItemsAdapter by lazy { MyItemsAdapter() }
    private val myViewModel: ItemListVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle("My Items for sale")

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = myAdapter

        myViewModel.getMyItems().observe(viewLifecycleOwner, Observer {
            myAdapter.setItemsList(it)
        })

        myAdapter.getSize().observe(viewLifecycleOwner, Observer {size ->
            if(size != 0)
                emptyMessage.visibility = View.GONE
            else
                emptyMessage.visibility = View.VISIBLE
        })

        myAdapter.setOnItemClickListener(object :
            MyItemsAdapter.ClickListener {

            override fun onItemClick(itemID: String?) {
                val bundle = bundleOf(Pair("group15.lab3.ITEM_ID",itemID))
                findNavController().navigate(R.id.action_itemListFragment_to_itemDetailsFragment,bundle)
            }
            override fun onItemEdit(itemID: String?) {
                val bundle = bundleOf(Pair("group15.lab3.ITEM_ID",itemID))
                findNavController().navigate(R.id.action_itemListFragment_to_itemEditFragment,bundle)
            }
        })

        fab_new_item.setOnClickListener {
            findNavController().navigate(R.id.action_itemListFragment_to_itemEditFragment)
        }

    }
}