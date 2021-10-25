package com.gabrielsantana.projects.controledegastos.domain.usecase.di

import com.gabrielsantana.projects.controledegastos.domain.usecase.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface UseCaseModule {

    @Binds
    fun bindCreateTransactionUseCase(useCase: CreateTransactionUseCaseImpl): CreateTransactionUseCase

    @Binds
    fun bindDeleteTransactionUseCase(useCase: DeleteTransactionUseCaseImpl): DeleteTransactionUseCase

    @Binds
    fun bindGetTotalAmountByUserTransactionType(useCase: GetTotalAmountByTransactionTypeUseCaseImpl): GetTotalAmountByTransactionTypeUseCase

    @Binds
    fun bindObserveTransactionsByTransactionFilterUseCase(
        useCase: ObserveTransactionsByTransactionFilterUseCaseImpl
    ): ObserveTransactionsByTransactionFilterUseCase

    @Binds
    fun bindDeleteManyTransactionsById(useCase: DeleteManyTransactionsByIdUseCaseImpl): DeleteManyTransactionsByIdUseCase
}