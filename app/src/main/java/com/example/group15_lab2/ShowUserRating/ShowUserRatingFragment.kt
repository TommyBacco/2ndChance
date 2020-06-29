package com.example.group15_lab2.ShowUserRating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.group15_lab2.MainActivity.MainActivity
import com.example.group15_lab2.R
import kotlinx.android.synthetic.main.fragment_item_edit.*
import kotlinx.android.synthetic.main.fragment_show_user_rating.*

class ShowUserRatingFragment : Fragment() {

    private lateinit var myViewModel:ShowUserRatingVM
    private val myAdapter: ShowUserRaringAdapter by lazy { ShowUserRaringAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_user_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = resources.getString(R.string.showUserRating_title)
        (activity as MainActivity).setToolbarTitle(title)

        myViewModel = ViewModelProvider(this).get(ShowUserRatingVM::class.java)

        if(savedInstanceState == null) {
            val id = arguments?.getString("group15.lab4.USER_ID",null)
            myViewModel.setUserID(id)
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = myAdapter

        myViewModel.getUserRatings().observe(viewLifecycleOwner, Observer {
            myAdapter.setItemsList(it)
        })

        myAdapter.getSize().observe(viewLifecycleOwner, Observer { size ->
            if (size != 0)
                emptyMessage.visibility = View.GONE
             else
                emptyMessage.visibility = View.VISIBLE
        })

    }
}