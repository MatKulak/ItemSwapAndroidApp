package com.mateusz.itemswap.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.mateusz.itemswap.R
import com.mateusz.itemswap.utils.Utils.createParams

class FindFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchTextField: TextInputLayout
    private lateinit var advertisementsListFragment: AdvertisementsListFragment
    private lateinit var showAllSwitch: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find, container, false)

        searchTextField = view.findViewById(R.id.searchTextField)
        searchEditText = view.findViewById(R.id.searchEditText)
        showAllSwitch = view.findViewById(R.id.toggleSwitch)

        advertisementsListFragment = AdvertisementsListFragment.newInstance(createParams("filter" to "matchmaking"))
        searchTextField.isEnabled = false
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

        showAllSwitch.setOnCheckedChangeListener { _, isChecked ->
            val params: Map<String, String>
            if (isChecked) {
                params = createParams("filter" to "all", "query" to "")
                searchTextField.isEnabled = true
            } else {
                params = createParams("filter" to "matchmaking")
                searchTextField.isEnabled = false
            }

            advertisementsListFragment.updateParams(params)
        }

        return view
    }
}


