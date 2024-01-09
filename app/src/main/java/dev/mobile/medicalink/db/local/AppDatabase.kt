package dev.mobile.medicalink.db.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.mobile.medicalink.db.local.dao.CisBdpmDao
import dev.mobile.medicalink.db.local.dao.MedocDao
import dev.mobile.medicalink.db.local.dao.PriseValideeDao
import dev.mobile.medicalink.db.local.dao.UserDao
import dev.mobile.medicalink.db.local.entity.CisBdpm
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.entity.PriseValidee
import dev.mobile.medicalink.db.local.entity.User

@Database(
    entities = [User::class, Medoc::class, CisBdpm::class, PriseValidee::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun medocDao(): MedocDao
    abstract fun cisBdpmDao(): CisBdpmDao
    abstract fun priseValideeDao(): PriseValideeDao


    companion object {
        private const val DATABASE_NAME = "medicalink.db"
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Créer la base de données si elle n'existe pas
                //Si on créé la base de données, alors on va la remplir avec les données de la base de données médicamenteuse
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                /*
                //On créer un thread pour remplir la base de données
                Thread(Runnable {
                    // On supprime les données de la base de données médicamenteuse
                    instance.cisBdpmDao().deleteAll()
                    // On ajoute les données de la base de données médicamenteuse avant de retourner l'instance
                    val cisBdpmRepository = CisBdpmRepository(instance.cisBdpmDao())
                    cisBdpmRepository.insertFromCsv(context)
                }).start()

                 */
                instance
            }
        }
    }

    fun tsarBomba() {
        clearAllTables()
    }

}
