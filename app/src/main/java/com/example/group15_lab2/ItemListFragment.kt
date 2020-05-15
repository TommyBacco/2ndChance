package com.example.group15_lab2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.group15_lab2.DataClass.Item
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_list.*

class ItemListFragment : Fragment() {

    private lateinit var myAdapter: ItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = FirebaseRepository.getMyItemsList()
        myAdapter = ItemAdapter(options)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context,2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = myAdapter


        myAdapter.setOnItemClickListener(object : ItemAdapter.ClickListener{

            override fun onItemClick(itemID: String?) {
                val bundle = bundleOf(Pair("group15.lab3.ITEM_ID",itemID))
                findNavController().navigate(R.id.action_itemListFragment_to_itemDetailsFragment,bundle)
            }

            override fun onItemEdit(itemID: String?) {
                val bundle = bundleOf(Pair("group15.lab3.ITEM_ID",itemID))
                findNavController().navigate(R.id.action_itemListFragment_to_itemEditFragment,bundle)
            }

        })

        myAdapter.getSize().observe(viewLifecycleOwner, Observer { size ->
            if(size != 0)
                emptyMessage.visibility = View.INVISIBLE
            else
                emptyMessage.visibility = View.VISIBLE
        })

        fab_new_item.setOnClickListener {
            findNavController().navigate(R.id.action_itemListFragment_to_itemEditFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }


}