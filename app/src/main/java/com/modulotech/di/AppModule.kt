package com.modulotech.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module(includes = [ViewModelModule::class, NetworkModule::class])
class AppModule {
}