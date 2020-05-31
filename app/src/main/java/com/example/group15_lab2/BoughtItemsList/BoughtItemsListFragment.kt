package com.example.group15_lab2.BoughtItemsList

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.group15_lab2.MainActivity.MainActivity
import com.example.group15_lab2.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_bought_items_list.*

class BoughtItemsListFragment : Fragment() {

    private val myViewModel: BoughtItemsListVM by viewModels()
    private val myAdapter: BoughtItemsAdapter by lazy { BoughtItemsAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bought_items_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle("Bought Items")

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = myAdapter

        myViewModel.getBoughtItems().observe(viewLifecycleOwner, Observer {
            myAdapter.setItemsList(it)
        })

        myAdapter.getSize().observe(viewLifecycleOwner, Observer {size ->
            if(size != 0)
                emptyMessage.visibility = View.GONE
            else
                emptyMessage.visibility = View.VISIBLE
        })

        myAdapter.setOnItemClickListener(object : BoughtItemsAdapter.ClickListener {
            override fun onItemClick(itemID: String?) {
                val bundle = bundleOf(Pair("group15.lab3.ITEM_ID", itemID))
                findNavController().navigate(
                    R.id.action_nav_boughtItems_to_nav_itemDetails, bundle)
            }

            override fun onItemReview(itemID: String?, itemOwner: String?, isRated: Boolean) {
                if(isRated){
                    val snack: Snackbar = Snackbar.make(requireView(), "You have already rated the seller for this item!", Snackbar.LENGTH_LONG)
                    val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
                    tv.setTextColor(Color.WHITE)
                    tv.typeface = Typeface.DEFAULT_BOLD
                    snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.longTitle))
                    snack.show()
                }
                else{
                    val bundle =
                        bundleOf(Pair("group15.lab3.ITEM_ID",itemID),
                            Pair("group15.lab3.ITEM_OWNER",itemOwner))
                    findNavController().navigate(R.id.action_nav_boughtItems_to_ratingUserFragment,bundle)
                }
            }
        })

    }
}