package dev.mobile.medicalink.fragments.messages

import dev.mobile.medicalink.fragments.messages.adapter.MessagesFragmentAdapterR
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView


class MessagesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)


        val messagesDeTest = mutableListOf<Pair<String, String>>(
            Pair("Durandal", "Je vous envoie la prescription..."),
            Pair("Richard", "Programmation rendez-vous le..."),
            Pair("Hervouet", "Avez-vous un conseil pour..."),
        )

        recyclerView = view.findViewById(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = MessagesFragmentAdapterR(messagesDeTest)

        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))


        return view
    }
}