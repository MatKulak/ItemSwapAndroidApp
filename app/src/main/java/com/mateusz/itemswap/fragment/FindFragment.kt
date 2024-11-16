package com.mateusz.itemswap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.mateusz.itemswap.R
import com.mateusz.itemswap.utils.Utils.createParams

class FindFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchTextField: TextInputLayout
    private lateinit var advertisementsListFragment: AdvertisementsListFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find, container, false)

        searchTextField = view.findViewById(R.id.searchTextField)
        searchEditText = view.findViewById(R.id.searchEditText)

        advertisementsListFragment = AdvertisementsListFragment.newInstance(createParams("filter" to "all"))
        childFragmentManager.beginTransaction()
            .replace(R.id.advertisementsListContainer, advertisementsListFragment)
            .commit()

        searchTextField.setEndIconOnClickListener {
            val query = searchEditText.text.toString().trim()
            val params = if (query.isNotEmpty()) {
                createParams("filter" to "all", "query" to query)
            } else {
                createParams("filter" to "all", "query" to "")
            }
            advertisementsListFragment.updateParams(params)
        }

        return view
    }
}


