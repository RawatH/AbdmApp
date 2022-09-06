package org.commcare.dalvik.abha.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import org.commcare.dalvik.data.repository.AbdmRepositoryImpl
import org.commcare.dalvik.data.repository.DataStoreRepositoryImpl
import org.commcare.dalvik.domain.repositories.AbdmRepository
import org.commcare.dalvik.domain.repositories.DatastoreRepository

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataModule {

    @Binds
    abstract fun provideAbdmRespositoryImpl(abdmRepositoryImpl: AbdmRepositoryImpl):AbdmRepository

    @Binds
    abstract fun provideDataStoreRepository(dataStoreRepositoryImpl: DataStoreRepositoryImpl):DatastoreRepository


}