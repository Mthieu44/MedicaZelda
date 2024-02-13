package dev.mobile.medicalink.db.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uuid"],
            childColumns = ["uuidUser"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = CisBdpm::class,
            parentColumns = ["codeCIS"],
            childColumns = ["codeCIS"],
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["uuidUser"]),
        Index(value = ["codeCIS"])
            ]
)

//TODO("rajouter un uuid lié au médicament dans la base de donnée médicamenteuse")
data class Medoc(
    @PrimaryKey val uuid: String,
    @ColumnInfo(name = "uuidUser") var uuidUser: String,
    @ColumnInfo(name = "nom") val nom: String,
    @ColumnInfo(name = "codeCIS") val codeCIS: String,
    @ColumnInfo(name = "dosageNB") val dosageNB: String,
    @ColumnInfo(name = "dosageUnite") val dosageUnite: String,
    @ColumnInfo(name = "dateFinTraitement") var dateFinTraitement: String?,
    @ColumnInfo(name = "typeComprime") val typeComprime: String,
    @ColumnInfo(name = "comprimesRestants") var comprimesRestants: Int?,
    @ColumnInfo(name = "expire") var expire: Boolean,
    @ColumnInfo(name = "effetsSecondaires") val effetsSecondaires: String?,
    @ColumnInfo(name = "prises") val prises: String?,
    @ColumnInfo(name = "totalQuantite") val totalQuantite: Int?,
    @ColumnInfo(name = "dateDbtTraitement") val dateDbtTraitement: String?,
)
