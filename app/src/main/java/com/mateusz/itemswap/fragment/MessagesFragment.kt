package com.mateusz.itemswap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mateusz.itemswap.R

class MessagesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)
        val fragment = ConversationsListFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.conversationsListContainer, fragment)
            .commit()

        return view
    }
}