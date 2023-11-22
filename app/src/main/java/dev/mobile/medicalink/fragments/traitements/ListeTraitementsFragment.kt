package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue


class ListeTraitementsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_liste_traitements, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())

        var isAddingTraitement  = arguments?.getString("isAddingTraitement")




        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.VISIBLE
        }


        if (isAddingTraitement=="true"){
            var newTraitement = arguments?.getSerializable("newTraitement") as Traitement

            var newMedoc : Medoc

            var newTraitmentUUID = UUID.randomUUID().toString()

            var newTraitementEffetsSec : String? = null
            if (newTraitement.effetsSecondaires!=null){
                var chaineDeChar = ""
                for (effet in newTraitement.effetsSecondaires!!){
                    chaineDeChar+="$effet;"
                }
                chaineDeChar=chaineDeChar.subSequence(0,chaineDeChar.length-1).toString()
                newTraitementEffetsSec=chaineDeChar
            }

            var newTraitementPrises : String? = null

            if (newTraitement.prises!=null){
                var chaineDeChar = ""
                for (prise in newTraitement.prises!!){
                    chaineDeChar+="${prise.toString()}/"
                }
                chaineDeChar=chaineDeChar.subSequence(0,chaineDeChar.length-1).toString()
                newTraitementPrises=chaineDeChar
            }

            //TODO("Changer l'uuid utilisateur par l'utilisateur courant")
            newMedoc = Medoc(
                newTraitmentUUID,
                "111111",
                newTraitement.nomTraitement,
                newTraitement.dosageNb.toString(),
                newTraitement.dosageUnite,
                newTraitement.dateFinTraitement.toString(),
                newTraitement.typeComprime,
                newTraitement.comprimesRestants,
                newTraitement.expire,
                newTraitementEffetsSec,
                newTraitementPrises
            )

            val queue2 = LinkedBlockingQueue<Boolean>()
            Thread{
                medocDatabaseInterface.insertMedoc(newMedoc)
                queue2.add(true)
            }.start()
            queue2.take()

        }

        val queue = LinkedBlockingQueue<MutableList<Traitement>>()

        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread{
            val listeTraitement : MutableList<Traitement> = mutableListOf()

            //TODO("Changer l'uuid utilisateur par l'utilisateur courant")
            val listeMedoc = medocDatabaseInterface.getAllMedocByUserId("111111")

            for (medoc in listeMedoc){

                var listeEffetsSec : MutableList<String>? = null
                if (medoc.effetsSecondaires!=null){
                    listeEffetsSec = medoc.effetsSecondaires.split(";").toMutableList()
                }


                val listePrise = mutableListOf<Prise>()

                if (medoc.prises != null){
                    for (prise in medoc.prises.split("/")){
                        val traitementPrise : MutableList<String> = prise.split(";").toMutableList()
                        val maPrise = Prise(traitementPrise[0].toInt(),traitementPrise[1],traitementPrise[2].toInt(),traitementPrise[3])
                        listePrise.add(maPrise)
                    }
                }

                val traitement = Traitement(
                    medoc.nom,
                    medoc.dosageNB.toInt(),
                    medoc.dosageUnite,
                    LocalDate.of(2023,12,12),
                    medoc.typeComprime,
                    medoc.comprimesRestants,
                    medoc.expire,
                    listeEffetsSec,
                    listePrise
                )

                listeTraitement.add(traitement)

            }
            queue.add(listeTraitement)
        }.start()

        val mesTraitements = queue.take()


        val traitementsTries = mesTraitements.sortedBy { it.expire }.toMutableList()


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewListeEffetsSecondaires)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ListeTraitementAdapterR(traitementsTries)

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        //Ajout de la fonctionnalité de retour à la page précédente
        val retour = view.findViewById<ImageView>(R.id.annulerListeEffetsSecondaires)
        retour.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, MainTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

}