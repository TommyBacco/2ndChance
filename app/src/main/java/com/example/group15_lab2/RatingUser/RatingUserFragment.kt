package com.example.group15_lab2.RatingUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.group15_lab2.MainActivity.MainActivity
import com.example.group15_lab2.R
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

        (activity as MainActivity).setToolbarTitle("Rating User")

        myViewModel.getRatingUser().observe(viewLifecycleOwner, Observer { review ->
            user_comment.setText(review.comment)
            user_rating.rating = review.rating
        })
        enableEditMode()
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