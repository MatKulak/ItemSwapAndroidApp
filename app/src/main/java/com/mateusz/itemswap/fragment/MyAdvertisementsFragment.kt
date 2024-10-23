package com.mateusz.itemswap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mateusz.itemswap.R
import com.mateusz.itemswap.utils.Utils.createParams

class MyAdvertisementsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_find, container, false)
        val fragment = AdvertisementsListFragment.newInstance(createParams("filter" to "user"))
        childFragmentManager.beginTransaction()
            .replace(R.id.advertisementsListContainer, fragment)
            .commit()

        return view
    }
}