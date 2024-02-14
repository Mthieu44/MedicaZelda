package dev.mobile.medicalink.fragments.traitements.ajouts

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.ListeTraitementsFragment
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement
import dev.mobile.medicalink.fragments.traitements.adapter.RecapAdapterR


class AjoutManuelRecapitulatif : Fragment() {

    private lateinit var retour: ImageView
    private lateinit var suivant: Button

    private lateinit var nomMedoc: TextView
    private lateinit var textUnite: TextView
    private lateinit var textStock: TextView
    private lateinit var dateFindeTraitement: TextView
    private lateinit var sousNomPeriodicite: TextView

    private lateinit var nomLayout: ConstraintLayout
    private lateinit var caracteristiqueLayout: ConstraintLayout
    private lateinit var periodiciteLayout: ConstraintLayout
    private lateinit var priseLayout: ConstraintLayout
    private lateinit var reapprovisionnementLayout: ConstraintLayout


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_ajout_manuel_recapitulatif, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        val traitement = arguments?.getSerializable("traitement") as Traitement
        val isAddingTraitement = arguments?.getString("isAddingTraitement")
        val schema_prise1 = arguments?.getString("schema_prise1")
        val provenance = arguments?.getString("provenance")
        val dureePriseDbt = arguments?.getString("dureePriseDbt")
        val dureePriseFin = arguments?.getString("dureePriseFin")



        nomMedoc = view.findViewById(R.id.nomMedoc)
        textUnite = view.findViewById(R.id.textUnite)
        textStock = view.findViewById(R.id.textStock)
        dateFindeTraitement = view.findViewById(R.id.dateFinTraitementText)
        sousNomPeriodicite = view.findViewById(R.id.sousNomPeriodicite)

        nomLayout = view.findViewById(R.id.nomLayout)
        caracteristiqueLayout = view.findViewById(R.id.caracteristiqueLayout)
        periodiciteLayout = view.findViewById(R.id.periodiciteLayout)
        priseLayout = view.findViewById(R.id.priseLayout)
        reapprovisionnementLayout = view.findViewById(R.id.reapprovionnementLayout)

        var schemaPriseFormatee = ""
        if (schema_prise1 != null) {
            when (schema_prise1) {
                "Quotidiennement" -> {
                    schemaPriseFormatee = "Quotidiennement"
                }

                "Intervalle" -> {
                    schemaPriseFormatee =
                        "Tous les ${traitement.dosageNb} ${traitement.dosageUnite}"
                }

                "auBesoin" -> {
                    schemaPriseFormatee = "Au besoin"
                }
            }
        }

        nomMedoc.text = traitement.nomTraitement
        textUnite.text = traitement.typeComprime
        textStock.text = "${traitement.comprimesRestants} ${traitement.typeComprime}"
        if (traitement.comprimesRestants!! > 1) {
            textStock.text = "${textStock.text}s"
        }
        if (traitement.dateFinTraitement == null) {
            dateFindeTraitement.text = resources.getString(R.string.indetermine)
        } else {
            dateFindeTraitement.text =
                "${traitement.dateFinTraitement?.dayOfMonth}/${traitement.dateFinTraitement?.monthValue}/${traitement.dateFinTraitement?.year}"
        }

        sousNomPeriodicite.text = schemaPriseFormatee

        if (schemaPriseFormatee != "Au besoin") {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_recap)
            recyclerView.layoutManager = LinearLayoutManager(context)
            var liste: MutableList<Prise>
            liste = mutableListOf()
            if (traitement.prises != null) {
                liste = traitement.prises!!
            }
            Log.d("test", liste.toString())
            recyclerView.adapter = RecapAdapterR(liste)
            // Gestion de l'espacement entre les éléments du RecyclerView
            val espacementEnDp = 5
            recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))
        } else {
            priseLayout.visibility = View.GONE
        }



        suivant.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(
                "newTraitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.codeCIS,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    traitement.dateFinTraitement,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = ListeTraitementsFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }



        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.codeCIS,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    traitement.dateFinTraitement,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelStock()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
        //TODO("Si possible opti un peu les duplications")
        nomLayout.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.codeCIS,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    traitement.dateFinTraitement,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelSearchFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        caracteristiqueLayout.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.codeCIS,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    traitement.dateFinTraitement,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelTypeMedic()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }


        periodiciteLayout.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.codeCIS,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    traitement.dateFinTraitement,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelDateSchemaPrise()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        priseLayout.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.codeCIS,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    traitement.dateFinTraitement,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelSchemaPrise2Fragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        reapprovisionnementLayout.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.codeCIS,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    traitement.dateFinTraitement,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelStock()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }
}
