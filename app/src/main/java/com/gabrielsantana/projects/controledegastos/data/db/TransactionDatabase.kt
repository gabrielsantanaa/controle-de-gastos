package com.gabrielsantana.projects.controledegastos.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction

private const val DATABASE_NAME = "transaction_database"

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(TransactionDatabaseConverters::class)
abstract class TransactionDatabase: RoomDatabase() {

    abstract val dao: TransactionDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        fun getInstance(context: Context): TransactionDatabase {

            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TransactionDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }


}