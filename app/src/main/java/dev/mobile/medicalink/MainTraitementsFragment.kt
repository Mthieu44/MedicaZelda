package dev.mobile.medicalink


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository

class MainTraitementsFragment : Fragment() {
    private lateinit var addTraitementButton: LinearLayout
    private lateinit var traitementsButton: LinearLayout
    private lateinit var journalButton: LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_traitements, container, false)

        //Create database connexion, use `userDatabaseInterface` to access to the database
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())

        addTraitementButton = view.findViewById(R.id.cardaddtraitements)
        traitementsButton = view.findViewById(R.id.cardtraitements)
        journalButton = view.findViewById(R.id.cardjournal)


        //Si on clique sur le bouton "Ajouter un traitement" alors on change le fragment actuel (MainTraitementsFragment) par le fragment AddTraitementsFragment
        addTraitementButton.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, AddTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        //Si on clique sur le bouton "Traitements" alors on change le fragment actuel (MainTraitementsFragment) par le fragment ListeTraitementsFragment
        traitementsButton.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, ListeTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()

        }

        journalButton.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, ListeEffetsSecondairesFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }


        return view
    }
}
