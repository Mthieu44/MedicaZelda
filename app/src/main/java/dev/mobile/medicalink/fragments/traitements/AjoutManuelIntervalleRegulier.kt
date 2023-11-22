package dev.mobile.medicalink.fragments.traitements

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.os.Build
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R


class AjoutManuelIntervalleRegulier : Fragment() {

    private lateinit var inputIntervalle: TextInputEditText
    private lateinit var retour: ImageView
    private lateinit var suivant : Button



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_intervalle_regulier, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        inputIntervalle = view.findViewById(R.id.inputIntervalle)
        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        val traitement = arguments?.getSerializable("traitement") as Traitement
        var schema_prise1  = arguments?.getString("schema_prise1")
        var dureePriseDbt = arguments?.getString("dureePriseDbt")
        var dureePriseFin = arguments?.getString("dureePriseFin")


        inputIntervalle.setOnClickListener {
            val traitement = arguments?.getSerializable("traitement") as Traitement
            showIntervalleRegulierDialog(traitement, view.context)
        }


        suivant.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,null,traitement.typeComprime,25,false,null,traitement.prises))
            bundle.putString("provenance", "intervalleRegulier")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")

            val destinationFragment = AjoutManuelSchemaPrise2Fragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }



        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,null,traitement.typeComprime,25,false,null,traitement.prises))
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelSchemaPriseFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
        return view
    }

    private fun showIntervalleRegulierDialog(traitement: Traitement, context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_intervalle_regulier, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val intervalleRegulierDialog = builder.create()

        val firstNumberPicker = dialogView.findViewById<NumberPicker>(R.id.firstNumberPicker)
        val secondNumberPicker = dialogView.findViewById<NumberPicker>(R.id.secondNumberPicker)
        val annulerButton = dialogView.findViewById<Button>(R.id.annulerButton)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)

        firstNumberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        secondNumberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        // Configuration du deuxième NumberPicker (jours, semaines, mois)
        secondNumberPicker.displayedValues = arrayOf("Jours", "Semaines", "Mois")
        secondNumberPicker.minValue = 0
        secondNumberPicker.maxValue = 2
        secondNumberPicker.value = when (traitement.dosageUnite) {
            "Jours" -> 0
            "Semaines" -> 1
            "Mois" -> 2
            else -> 0
        }

        // Mise à jour des valeurs du premier NumberPicker en fonction de la sélection du deuxième
        updateFirstNumberPickerValues(firstNumberPicker, secondNumberPicker.value, traitement.dosageNb)

        // Écouteur de changement de valeur pour le deuxième NumberPicker
        secondNumberPicker.setOnValueChangedListener { _, _, newVal ->
            // Mise à jour des valeurs du premier NumberPicker en fonction de la nouvelle sélection
            updateFirstNumberPickerValues(firstNumberPicker, newVal, traitement.dosageNb)
        }

        annulerButton.setOnClickListener {
            intervalleRegulierDialog.dismiss()
        }

        okButton.setOnClickListener {
            // Mettre à jour les valeurs de l'objet Traitement avec les nouvelles valeurs
            traitement.dosageNb = firstNumberPicker.value
            traitement.dosageUnite = when (secondNumberPicker.value) {
                0 -> "Jours"
                1 -> "Semaines"
                2 -> "Mois"
                else -> "Jours"
            }

            // Mettre à jour l'interface utilisateur
            // Vous devez définir la logique appropriée pour mettre à jour votre interface utilisateur
            // Par exemple, si vous avez un TextView nommé inputIntervalle, vous pouvez faire quelque chose comme :
            inputIntervalle.setText("${traitement.dosageNb} ${traitement.dosageUnite}(s)")

            intervalleRegulierDialog.dismiss()
        }

        intervalleRegulierDialog.show()
    }

    private fun updateFirstNumberPickerValues(firstNumberPicker: NumberPicker, selectedValue: Int, currentDosage: Int) {
        when (selectedValue) {
            0 -> {
                firstNumberPicker.minValue = 2
                firstNumberPicker.maxValue = 99
            }
            1 -> {
                firstNumberPicker.minValue = 1
                firstNumberPicker.maxValue = 52
            }
            2 -> {
                firstNumberPicker.minValue = 1
                firstNumberPicker.maxValue = 12
            }
            else -> {
                firstNumberPicker.minValue = 2
                firstNumberPicker.maxValue = 99
            }
        }

        firstNumberPicker.value = currentDosage.coerceIn(firstNumberPicker.minValue, firstNumberPicker.maxValue)
    }




    override fun onResume() {
        super.onResume()

        // Attacher le gestionnaire du bouton de retour arrière de l'appareil
        val callback = object : OnBackPressedCallback(true) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun handleOnBackPressed() {
                // Code à exécuter lorsque le bouton de retour arrière est pressé
                val traitement = arguments?.getSerializable("traitement") as Traitement
                val schema_prise1 = arguments?.getString("schema_prise1")
                val dureePriseDbt = arguments?.getString("dureePriseDbt")
                val dureePriseFin = arguments?.getString("dureePriseFin")

                val bundle = Bundle()
                bundle.putSerializable("traitement", Traitement(traitement.nomTraitement, traitement.dosageNb, traitement.dosageUnite, null, traitement.typeComprime, 25, false, null, traitement.prises))
                bundle.putString("provenance", "intervalleRegulier")
                bundle.putString("schema_prise1", "$schema_prise1")
                bundle.putString("dureePriseDbt", "$dureePriseDbt")
                bundle.putString("dureePriseFin", "$dureePriseFin")

                val destinationFragment = AjoutManuelSchemaPriseFragment()
                destinationFragment.arguments = bundle

                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}