package com.mateusz.itemswap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.mateusz.itemswap.R
import com.mateusz.itemswap.utils.Utils.createParams


class FindFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var advertisementsListFragment: AdvertisementsListFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)

        advertisementsListFragment = AdvertisementsListFragment.newInstance(createParams("filter" to "all"))
        childFragmentManager.beginTransaction()
            .replace(R.id.advertisementsListContainer, advertisementsListFragment)
            .commit()

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty())
                advertisementsListFragment.updateParams(createParams("filter" to "all", "query" to query))
            else
                advertisementsListFragment.updateParams(createParams("filter" to "all", "query" to ""))
        }

        return view
    }
}


