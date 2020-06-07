package com.example.group15_lab2.RatingUser

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.group15_lab2.MainActivity.MainActivity
import com.example.group15_lab2.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_rating_user.*

class RatingUserFragment : Fragment() {

    private val myViewModel: RatingUserVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rating_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = resources.getString(R.string.ratingUsers_title)
        (activity as MainActivity).setToolbarTitle(title)

        if(savedInstanceState == null){
            val itemID = arguments?.getString("group15.lab3.ITEM_ID")
            val ownerID = arguments?.getString("group15.lab3.ITEM_OWNER")
            myViewModel.setItemData(itemID,ownerID)
        }

        myViewModel.getRatingUser().observe(viewLifecycleOwner, Observer { review ->
            user_comment.setText(review.comment)
            user_rating.rating = review.rating
        })
        enableEditMode()

        button_send_rating.setOnClickListener {
            val user_nickname = (activity as MainActivity).getUserNickname()
            myViewModel.sendUserRating(user_nickname)
            val text = resources.getString(R.string.ratingUsers_snack)
            val snack: Snackbar = Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG)
            val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
            tv.setTextColor(Color.WHITE)
            tv.typeface = Typeface.DEFAULT_BOLD
            snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editedItem))
            snack.show()
            findNavController().popBackStack()
        }
    }

    private fun enableEditMode(){
        user_comment.doOnTextChanged { text, _, _, _ ->
            myViewModel.editComment(text.toString())
        }
        user_rating.setOnRatingBarChangeListener { _, rating, _ ->
            myViewModel.editRating(rating)
        }
    }
}