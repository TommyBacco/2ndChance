package com.example.group15_lab2.InterestedUserList

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
import com.example.group15_lab2.UserFirestoreAdapter
import kotlinx.android.synthetic.main.fragment_interested_user_list.*

class InterestedUserListFragment : Fragment() {

    private lateinit var myAdapter: UserFirestoreAdapter
    private val myViewModel: InterestedUserListVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_interested_user_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle("Interested Users")

        if(savedInstanceState == null) {
            val id = arguments?.getString("group15.lab3.ITEM_ID")
            myViewModel.setItemID(id)
        }

        val options = myViewModel.getInterestedUsersOptions()
        myAdapter = UserFirestoreAdapter(options)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = myAdapter

        myAdapter.setOnItemClickListener(object : UserFirestoreAdapter.ClickListener {
            override fun onUserClick(userID: String?) {
                val bundle = bundleOf(Pair("group15.lab3.USER_ID",userID))
                findNavController().navigate(R.id.action_iterestedUserListFragment_to_nav_profile,bundle)
            }
        })

        myAdapter.getSize().observe(viewLifecycleOwner, Observer { size ->
            if(size != 0)
                emptyMessage.visibility = View.GONE
            else
                emptyMessage.visibility = View.VISIBLE
        })
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