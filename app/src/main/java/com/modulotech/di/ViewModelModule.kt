package com.modulotech.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.modulotech.di.factory.ViewModelFactory
import com.modulotech.screen.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap

@Suppress("unused")
@InstallIn(ApplicationComponent::class)
@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel
}
