package dev.mobile.medicalink.fragments.traitements.ajouts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.time.LocalDate


class AjoutSharedViewModel : ViewModel() {

    private val _nomTraitement = MutableLiveData("")
    val nomTraitement: LiveData<String> get() = _nomTraitement
    fun setNomTraitement(nom: String) {
        _nomTraitement.value = nom
    }

    private val _codeCIS = MutableLiveData("")
    val codeCIS: LiveData<String> get() = _codeCIS
    fun setCodeCIS(code: String) {
        _codeCIS.value = code
    }

    private val _dosageNb = MutableLiveData(0)
    val dosageNb: LiveData<Int> get() = _dosageNb
    fun setDosageNb(dosage: Int) {
        _dosageNb.value = dosage
    }

    private val _frequencePrise = MutableLiveData("")
    val frequencePrise: LiveData<String> get() = _frequencePrise
    fun setFrequencePrise(unite: String) {
        _frequencePrise.value = unite
    }

    private val _dateFinTraitement = MutableLiveData<LocalDate?>()
    val dateFinTraitement: LiveData<LocalDate?> get() = _dateFinTraitement
    fun setDateFinTraitement(date: LocalDate?) {
        _dateFinTraitement.value = date
    }

    private val _typeComprime = MutableLiveData("")
    val typeComprime: LiveData<String> get() = _typeComprime
    fun setTypeComprime(type: String) {
        _typeComprime.value = type
    }

    private val _comprimesRestants = MutableLiveData(0)
    val comprimesRestants: LiveData<Int> get() = _comprimesRestants
    fun setComprimesRestants(comprimes: Int) {
        _comprimesRestants.value = comprimes
    }

    private val _effetsSecondaires = MutableLiveData<MutableList<String>>(mutableListOf())
    val effetsSecondaires: LiveData<MutableList<String>> get() = _effetsSecondaires
    fun setEffetsSecondaires(effets: MutableList<String>) {
        _effetsSecondaires.value = effets
    }

    private val _prises = MutableLiveData<MutableList<Prise>>(mutableListOf())
    val prises: LiveData<MutableList<Prise>> get() = _prises
    fun setPrises(prises: MutableList<Prise>) {
        _prises.value = prises
    }

    private val _totalQuantite = MutableLiveData(0)
    val totalQuantite: LiveData<Int> get() = _totalQuantite
    fun setTotalQuantite(quantite: Int) {
        _totalQuantite.value = quantite
    }

    private val _uuid = MutableLiveData("")
    private val uuid: LiveData<String> get() = _uuid
    fun setUUID(uuid: String) {
        _uuid.value = uuid
    }

    private val _uuidUser = MutableLiveData("")
    val uuidUser: LiveData<String> get() = _uuidUser
    fun setUUIDUSER(uuid: String) {
        _uuidUser.value = uuid
    }

    private val _dateDbtTraitement = MutableLiveData<LocalDate>(null)
    val dateDbtTraitement: LiveData<LocalDate> get() = _dateDbtTraitement
    fun setDateDbtTraitement(date: LocalDate) {
        _dateDbtTraitement.value = date
    }

    private val _traitements = MutableLiveData<MutableList<Traitement>>(mutableListOf())
    val traitements: LiveData<MutableList<Traitement>> get() = _traitements
    private fun setTraitements(traitements: MutableList<Traitement>) {
        _traitements.value = traitements
    }

    private val _isAddingTraitement = MutableLiveData<Boolean?>(null)
    val isAddingTraitement: LiveData<Boolean?> get() = _isAddingTraitement
    fun setIsAddingTraitement(isAdding: Boolean?) {
        _isAddingTraitement.value = isAdding
    }

    private val _schemaPrise1 = MutableLiveData("")
    val schemaPrise1: LiveData<String> get() = _schemaPrise1
    fun setSchemaPrise1(schema: String) {
        _schemaPrise1.value = schema
    }

    private val _provenance = MutableLiveData("")
    val provenance: LiveData<String> get() = _provenance
    fun setProvenance(provenance: String) {
        _provenance.value = provenance
    }

    fun makeTraitement() = Traitement(
        nomTraitement.value ?: "",
        codeCIS.value ?: "",
        dosageNb.value ?: 0,
        frequencePrise.value ?: "",
        dateFinTraitement.value,
        typeComprime.value ?: "",
        comprimesRestants.value ?: 0,
        effetsSecondaires = effetsSecondaires.value ?: mutableListOf(),
        prises = prises.value ?: mutableListOf(),
        totalQuantite = totalQuantite.value ?: 0,
        uuid = uuid.value ?: "",
        uuidUser = uuidUser.value ?: "",
        dateDbtTraitement = dateDbtTraitement.value ?: LocalDate.now()
    )

    /**
     * Convertit les données du traitement en un objet Medoc
     * @return Medoc : le médicament
     */
    fun toMedoc() = Medoc(
        if (isAddingTraitement.value!!) java.util.UUID.randomUUID()
            .toString() else uuid.value!!,
        "",
        nomTraitement.value ?: "",
        codeCIS.value ?: "",
        dosageNb.value.toString(),
        frequencePrise.value ?: "",
        dateFinTraitement.value?.toString() ?: "null",
        typeComprime.value ?: "",
        comprimesRestants.value ?: 0,
        dateFinTraitement.value != null && dateFinTraitement.value!! < LocalDate.now(),
        effectsSec() ?: "null",
        prises() ?: "null",
        totalQuantite.value ?: 0,
        dateDbtTraitement.value?.toString() ?: "null"
    )

    /**
     * Convertit la liste d'effets secondaires en une chaine de caractères
     * @return String? : la chaine de caractères
     */
    private fun effectsSec() : String? {
        var newTraitementEffetsSec: String? = null
        if (effetsSecondaires.value != null) {
            var chaineDeChar = ""
            for (effet in effetsSecondaires.value!!) {
                chaineDeChar += "$effet;"
            }
            if (chaineDeChar != "") chaineDeChar =
                chaineDeChar.subSequence(0, chaineDeChar.length - 1).toString()
            newTraitementEffetsSec = chaineDeChar
        }
        return newTraitementEffetsSec
    }

    /**
     * Convertit la liste de prises en une chaine de caractères
     * @return String? : la chaine de caractères
     */
    private fun prises() : String? {
        var newTraitementPrises: String? = null
        if (prises.value != null) {
            var chaineDeChar = ""
            for (prise in prises.value!!) {
                chaineDeChar += "${prise}/"
            }
            if (chaineDeChar != "") chaineDeChar =
                chaineDeChar.subSequence(0, chaineDeChar.length - 1).toString()
            newTraitementPrises = chaineDeChar
        }
        return newTraitementPrises
    }

    fun addTraitement(traitement: Traitement = makeTraitement()) {
        val list = _traitements.value
        if (list != null) {
            list.add(traitement)
            setTraitements(list)
        } else {
            setTraitements(mutableListOf(traitement))
        }
    }

    fun removeTraitement(traitement: Traitement) {
        val list = _traitements.value
        if (list != null) {
            list.remove(traitement)
            setTraitements(list)
        }
    }

    fun reset() {
        setNomTraitement("")
        setCodeCIS("")
        setDosageNb(0)
        setFrequencePrise("")
        setDateFinTraitement(null)
        setTypeComprime("")
        setComprimesRestants(0)
        setEffetsSecondaires(mutableListOf())
        setPrises(mutableListOf())
        setTotalQuantite(0)
        setUUID("")
        setUUIDUSER("")
        setDateDbtTraitement(LocalDate.now())
        setSchemaPrise1("")
        setProvenance("")
        setIsAddingTraitement(null)
    }

}