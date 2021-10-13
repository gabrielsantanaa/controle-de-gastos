package com.gabrielsantana.projects.controledegastos.data.di

import android.content.Context
import com.gabrielsantana.projects.controledegastos.data.db.TransactionDatabase
import com.gabrielsantana.projects.controledegastos.data.db.TransactionDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object TransactionDatabaseModule {

    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context): TransactionDatabase {
        return TransactionDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun getDao(database: TransactionDatabase): TransactionDatabaseDao {
        return database.dao
    }
}