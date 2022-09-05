package org.commcare.dalvik.abha.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.commcare.dalvik.data.repository.AbdmRepositoryImpl
import org.commcare.dalvik.domain.repositories.AbdmRepository

@Module
@InstallIn(ViewModelComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun provideAbdmRespositoryImpl(abdmRepositoryImpl: AbdmRepositoryImpl):AbdmRepository

}