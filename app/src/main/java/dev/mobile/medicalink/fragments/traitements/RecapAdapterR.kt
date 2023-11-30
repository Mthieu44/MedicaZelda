package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R


class RecapAdapterR(private val list: MutableList<Prise>) :
    RecyclerView.Adapter<RecapAdapterR.AjoutManuelViewHolder>() {

    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val heurePrise = view.findViewById<TextView>(R.id.heurePrise)
        val dosageRecap = view.findViewById<TextView>(R.id.dosageRecap)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_recap_ajout_manuel, parent, false)
        return AjoutManuelViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        val item = list[position]

        Log.d("test",item.heurePrise)
        holder.heurePrise.text=item.heurePrise
        holder.dosageRecap.text="${item.quantite} ${item.dosageUnite}"


        /*
        A check pour afficher les détails d'un traitement quand cliqué

        holder.naissance.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
            false
        }
         */

        holder.view.setOnLongClickListener {

            val context = holder.itemView.context
            true
        }
    }

}