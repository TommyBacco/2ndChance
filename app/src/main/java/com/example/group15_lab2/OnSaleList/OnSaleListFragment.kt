package com.example.group15_lab2.OnSaleList

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.group15_lab2.MainActivity.MainActivity
import com.example.group15_lab2.R
import kotlinx.android.synthetic.main.fragment_on_sale_list.*
import java.util.*

class OnSaleListFragment : Fragment() {

    private val myViewModel: OnSaleListVM by viewModels()
    private val myAdapter: AdvertisementsAdapter by lazy { AdvertisementsAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_on_sale_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = resources.getString(R.string.onSaleList_title)
        (activity as MainActivity).setToolbarTitle(title)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = myAdapter

        myViewModel.getAdvertisements().observe(viewLifecycleOwner, Observer {
            myAdapter.setItemsList(it)
        })

        myAdapter.getSize().observe(viewLifecycleOwner, Observer {size ->
            if(size != 0)
                emptyMessage.visibility = View.GONE
            else
                emptyMessage.visibility = View.VISIBLE
        })

        myAdapter.setOnItemClickListener(object : AdvertisementsAdapter.ClickListener {
            override fun onItemClick(itemID: String?) {
                val bundle = bundleOf(Pair("group15.lab3.ITEM_ID", itemID))
                findNavController().navigate(
                    R.id.action_nav_advertisements_to_nav_itemDetails, bundle)
            }
        })

        myViewModel.getFilterParams().observe(viewLifecycleOwner, Observer {
            myAdapter.filterByCategoryPriceAndLocation(it)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val searchItem = menu.findItem(R.id.search_button)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchView.queryHint = resources.getString(R.string.onSaleList_query_hint)

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{

            override fun onQueryTextChange(newText: String?): Boolean {
                myAdapter.filter.filter(newText)
                return false
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter_button -> {

                val view = layoutInflater.inflate(R.layout.filter_layout,null)

                val price_from:com.google.android.material.textfield.TextInputEditText
                        = view.findViewById(R.id.price_from)
                val price_to:com.google.android.material.textfield.TextInputEditText
                        = view.findViewById(R.id.price_to)
                val location:com.google.android.material.textfield.TextInputEditText
                        = view.findViewById(R.id.location)

                val category_spinner:Spinner = view.findViewById(R.id.spinner_filter_category)
                val category_adapter = ArrayAdapter.createFromResource(
                    requireActivity(),
                    R.array.FilterCategories,
                    R.layout.spinner_category
                )
                category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                category_spinner.adapter = category_adapter

                AlertDialog.Builder(context)
                    .setView(view)
                    .setPositiveButton(resources.getString(R.string.alert_filter_positiveBN)) { _, _ ->

                        val category =
                            if(category_spinner.selectedItemPosition == 0) "ALL"
                            else category_spinner.selectedItem.toString()

                        val from = price_from.text.toString()
                        val int_from =
                            if(from == "") null
                            else from.toFloat()

                        val to = price_to.text.toString()
                        val int_to =
                            if(to == "") null
                            else to.toFloat()

                        val loc = location.text.toString()
                        val loc_to_find =
                            if(loc == "") "NONE"
                            else loc.toLowerCase(Locale.ROOT)

                        myViewModel.setFilterParams(category,int_from,int_to,loc_to_find)
                    }
                    .setNeutralButton(resources.getString(R.string.alert_filter_neutralBN)){ _, _ ->
                    }
                    .show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}