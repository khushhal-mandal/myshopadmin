package com.example.myshoppingadminapp.common.di

import com.example.myshoppingadminapp.data.repoimpl.RepoImpl
import com.example.myshoppingadminapp.domain.repo.Repo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideRepo(firestore: FirebaseFirestore, storage: FirebaseStorage): Repo {
        return RepoImpl(firestore = firestore, storage = storage)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }
}