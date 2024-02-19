package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.adapter.ListeEffetsSecondairesAdapterR
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.LinkedBlockingQueue

class ListeEffetsSecondairesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var annuler: ImageView

    private lateinit var textAucunEffetSec: TextView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_liste_effets_secondaires, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())

        annuler = view.findViewById(R.id.annulerListeEffetsSecondaires)
        textAucunEffetSec = view.findViewById(R.id.textAucunEffetsSec)

        val queue = LinkedBlockingQueue<MutableList<Traitement>>()

        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread {
            val listeTraitement: MutableList<Traitement> = mutableListOf()

            //On récuềre l'uuid de l'utilisateur courant
            val uuidUser = userDatabaseInterface.getUsersConnected()[0].uuid


            val listeMedoc = medocDatabaseInterface.getAllMedocByUserId(uuidUser)

            for (medoc in listeMedoc) {

                var listeEffetsSec: MutableList<String>? = null
                if (medoc.effetsSecondaires != null) {
                    listeEffetsSec = medoc.effetsSecondaires.split(";").toMutableList()
                }


                val listePrise = mutableListOf<Prise>()

                if (medoc.prises != null) {
                    for (prise in medoc.prises.split("/")) {
                        val traitementPrise: MutableList<String> = prise.split(";").toMutableList()
                        val maPrise = Prise(
                            traitementPrise[0],
                            traitementPrise[1],
                            traitementPrise[2].toInt(),
                            traitementPrise[3]
                        )
                        listePrise.add(maPrise)
                    }
                }

                var newTraitementFinDeTraitement: LocalDate? = null

                if (medoc.dateFinTraitement != "null") {
                    Log.d("test", medoc.dateFinTraitement.toString())
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val date = medoc.dateFinTraitement

                    newTraitementFinDeTraitement = LocalDate.parse(date, formatter)
                }

                var newTraitementDbtDeTraitement: LocalDate? = null

                if (medoc.dateDbtTraitement != "null") {
                    Log.d("test", medoc.dateDbtTraitement.toString())
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val date = medoc.dateDbtTraitement

                    newTraitementDbtDeTraitement = LocalDate.parse(date, formatter)
                }

                val traitement = Traitement(
                    medoc.nom,
                    medoc.codeCIS,
                    medoc.dosageNB.toInt(),
                    medoc.frequencePrise,
                    newTraitementFinDeTraitement,
                    medoc.typeComprime,
                    medoc.comprimesRestants,
                    effetsSecondaires = listeEffetsSec,
                    prises = listePrise,
                    totalQuantite = medoc.totalQuantite,
                    UUID = medoc.uuid,
                    UUIDUSER = medoc.uuidUser,
                    dateDbtTraitement = newTraitementDbtDeTraitement
                )

                listeTraitement.add(traitement)

            }
            queue.add(listeTraitement)
        }.start()

        val mesTraitements = queue.take()

        val traitementsTries = mesTraitements.sortedBy { it.expire }.toMutableList()

        val effetsSecondairesMedicaments = mutableMapOf<String, MutableList<Traitement>>()

        // Parcoure la liste de traitements (lp).
        traitementsTries.forEach { traitement ->
            traitement.effetsSecondaires.orEmpty().forEach { effetSecondaire ->
                // Vérifie si l'effet secondaire est déjà dans la carte
                if (effetSecondaire.lowercase() in effetsSecondairesMedicaments) {
                    // S'il est présent, ajoutez le traitement à la liste existante
                    effetsSecondairesMedicaments[effetSecondaire.lowercase()]!!.add(traitement)
                } else {
                    // S'il n'est pas présent, créé une nouvelle liste et ajoute le traitement
                    effetsSecondairesMedicaments[effetSecondaire.lowercase()] =
                        mutableListOf(traitement)
                }
            }
        }

        if (effetsSecondairesMedicaments.isEmpty()) {
            textAucunEffetSec.visibility = View.VISIBLE
        } else {
            textAucunEffetSec.visibility = View.GONE
        }

        recyclerView = view.findViewById(R.id.recyclerViewTypeMedic)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = ListeEffetsSecondairesAdapterR(traitementsTries)

        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))


        annuler.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, MainTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

}
