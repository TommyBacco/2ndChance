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
import kotlinx.android.synthetic.main.fragment_interested_user_list.*

class InterestedUserListFragment : Fragment() {

    private val myAdapter: InterestedUsersAdapter by lazy { InterestedUsersAdapter() }
    private val myViewModel: InterestedUserListVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_interested_user_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = resources.getString(R.string.interestedUsers_title)
        (activity as MainActivity).setToolbarTitle(title)

        if(savedInstanceState == null) {
            val id = arguments?.getString("group15.lab3.ITEM_ID")
            myViewModel.setItemID(id)
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = myAdapter

        myViewModel.getInterestedUsers().observe(viewLifecycleOwner, Observer {
            myAdapter.setUsersList(it)
        })

        myAdapter.setOnItemClickListener(object : InterestedUsersAdapter.ClickListener {
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

}