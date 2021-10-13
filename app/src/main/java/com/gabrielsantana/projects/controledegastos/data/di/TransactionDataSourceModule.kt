package com.gabrielsantana.projects.controledegastos.data.di

import com.gabrielsantana.projects.controledegastos.data.datasource.RoomTransactionDataSource
import com.gabrielsantana.projects.controledegastos.data.datasource.TransactionDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface TransactionDataSourceModule {

    @Singleton
    @Binds
    fun bindTransactionDatasource(dataSource: RoomTransactionDataSource): TransactionDataSource

}