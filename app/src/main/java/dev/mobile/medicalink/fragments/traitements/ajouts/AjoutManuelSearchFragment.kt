package dev.mobile.medicalink.fragments.traitements.ajouts

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.CisBdpm
import dev.mobile.medicalink.db.local.entity.CisSubstance
import dev.mobile.medicalink.db.local.repository.CisBdpmRepository
import dev.mobile.medicalink.db.local.repository.CisSubstanceRepository
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement
import dev.mobile.medicalink.fragments.traitements.adapter.AjoutManuelSearchAdapterR
import java.util.concurrent.LinkedBlockingQueue


class AjoutManuelSearchFragment : Fragment() {


    private lateinit var addManuallySearchBar: TextInputEditText
    private lateinit var addManuallyButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var addManuallyButtonLauncher: ActivityResultLauncher<Intent>
    private lateinit var supprimerSearch: ImageView
    private lateinit var originalItemList: List<CisBdpm>
    private lateinit var filteredItemList: List<CisBdpm>
    private lateinit var itemAdapter: AjoutManuelSearchAdapterR


    private lateinit var retour: ImageView

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_search, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        val db = AppDatabase.getInstance(view.context.applicationContext)
        val cisBdpmDatabaseInterface = CisBdpmRepository(db.cisBdpmDao())

        //Récupération de la liste des Médicaments pour l'afficher
        val queue = LinkedBlockingQueue<List<CisBdpm>>()
        Thread {
            val listCisBdpm = cisBdpmDatabaseInterface.getAllCisBdpm()
            Log.d("CisBDPM list", listCisBdpm.toString())
            queue.add(listCisBdpm)
        }.start()
        originalItemList = queue.take()
        filteredItemList = originalItemList


        addManuallySearchBar = view.findViewById(R.id.add_manually_search_bar)
        addManuallyButton = view.findViewById(R.id.add_manually_button)
        supprimerSearch = view.findViewById(R.id.supprimerSearch)

        supprimerSearch.setOnClickListener {
            addManuallySearchBar.setText("")
        }
        addManuallySearchBar.setText(viewModel.nomTraitement.value)
        updateButtonState()

        addManuallySearchBar.addTextChangedListener(textWatcher)
        addManuallyButtonLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                }
            }


        recyclerView = view.findViewById(R.id.recyclerViewSearch)

        Log.d("ICI", filteredItemList.toString())

        itemAdapter = AjoutManuelSearchAdapterR(filteredItemList) { clickedItem ->
            searchForDuplcateSubstance(view.context, clickedItem.codeCIS)
            updateSearchBar(clickedItem.denomination)
            viewModel.setNomTraitement(clickedItem.denomination)
            viewModel.setCodeCIS(clickedItem.codeCIS)
        }
        recyclerView.adapter = itemAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        addManuallyButton.setOnClickListener {
            val destinationFragment = AjoutManuelTypeMedic()
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        retour = view.findViewById(R.id.retour_schema_prise2)

        retour.setOnClickListener {
            viewModel.setIsAddingTraitement(null)
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, AddTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            filterItems(s.toString())
        }

        override fun afterTextChanged(editable: Editable?) {
            updateButtonState()
        }
    }

    /**
     * Mise à jour de l'état du bouton "Ajouter" pour l'activer uniquement quand le champ de recherche n'est pas vide
     */
    private fun updateButtonState() {
        val allFieldsFilled = addManuallySearchBar.text!!.isNotBlank()

        if (allFieldsFilled) {
            addManuallyButton.isEnabled = true
            addManuallyButton.alpha = 1.0f
        } else {
            addManuallyButton.isEnabled = false
            addManuallyButton.alpha = 0.3.toFloat()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.setIsAddingTraitement(null)
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, AddTraitementsFragment())
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /**
     * Fonction de filtrage de la liste de médicaments sur une chaine de caractère (ici le contenu de la barre de recherche)
     * @param query la chaine de caractère sur laquelle on filtre la liste des médicaments
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterItems(query: String) {
        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)
        val filteredItemList = originalItemList.filter { item ->
            item.denomination.contains(query, ignoreCase = true)
        }
        requireActivity().runOnUiThread {
            itemAdapter = AjoutManuelSearchAdapterR(filteredItemList) { clickedItem ->
               searchForDuplcateSubstance(requireContext(), clickedItem.codeCIS)
                updateSearchBar(clickedItem.denomination)
                viewModel.setNomTraitement(clickedItem.denomination)
                viewModel.setCodeCIS(clickedItem.codeCIS)
            }
            recyclerView.adapter = itemAdapter
            itemAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Fonction utilisé pour mettre à jour le contenu de la barre de recherche
     * (utilisé quand on clique sur un médicament pour l'ajouter directement dans la barre de recherche)
     * @param query la chaine de caractère représentant le médicament sur lequel on a cliqué, à remplacer dans la barre de recherche
     */
    private fun updateSearchBar(query: String) {
        addManuallySearchBar.setText(query)
    }

    /**
     * Fonction utilisé pour rechercher si un médicament est déjà présent dans la liste des médicaments de l'utilisateur
     * S'il y a un médicament en conflit, on affiche un dialog pour prévenir l'utilisateur
     * @param context le contexte de l'application
     * @param codeCis le codeCis du médicament que l'on veut ajouter
     * @return la liste des médicaments en conflit avec le médicament que l'on veut ajouter
     */
    private fun searchForDuplcateSubstance(context: Context, codeCis: String) {
        val db = AppDatabase.getInstance(context)
        val userInterface = UserRepository(db.userDao())
        val medocInterface = MedocRepository(db.medocDao())
        val cisSubstanceInterface = CisSubstanceRepository(db.cisSubstanceDao())
        val queue = LinkedBlockingQueue<List<CisSubstance>>()
        Thread {
            try {
                //Récupération de tout les codes de substances déjà pris par l'utilisateur
                val userUuid = userInterface.getUsersConnected()[0].uuid
                val codeCisMedicamentDejaPris : List<String> = medocInterface.getAllMedocByUserId(userUuid).map {
                    it.codeCIS
                }
                val medicamentCisDejaPris : MutableList<CisSubstance> = mutableListOf()
                for (code in codeCisMedicamentDejaPris) {
                    medicamentCisDejaPris.add(cisSubstanceInterface.getOneCisSubstanceById(code)!!)
                }

                //Réupération du code de substance du médicament que l'on veut ajouter
                val codeSubstanceMedicamentAjoute = cisSubstanceInterface.getOneCisSubstanceById(codeCis)!!.codeSubstance
                //Vérification de la présence de ce code de substance dans la liste des médicaments déjà pris
                val medicamentEnConflit = medicamentCisDejaPris.filter { it.codeSubstance == codeSubstanceMedicamentAjoute }

                queue.add(medicamentEnConflit)
            } catch (e: Exception) {
                queue.add(listOf())
                Log.e("Erreur", e.toString())
            }
        }.start()

        val res = queue.take()

        if (res.isNotEmpty()) {
            afficheDialogMedicamentEnConflit(context, res)
        }
    }

    /**
     * Fonction pour afficher un dialog prévenant l'utilisateur qu'il veut ajouter un médicament en conflit avec un médicament déjà présent dans sa liste
     * @param context le contexte de l'application
     * @param lstDuplicate la liste des médicaments en conflit
     */
    private fun afficheDialogMedicamentEnConflit(context: Context, lstDuplicate: List<CisSubstance>) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_duplicate_substance, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val dosageDialog = builder.create()

        val dial = dialogView.findViewById<TextView>(R.id.ajouterVrm)
        dial.text = context.resources.getString(R.string.ajouter_vrm_medoc_conflit, lstDuplicate.size.toString(), lstDuplicate[0].denominationSubstance)
        val jaiCompris = dialogView.findViewById<Button>(R.id.jaiCompris)

        jaiCompris.setOnClickListener {
            dosageDialog.dismiss()
        }

        dosageDialog.show()
    }

}








