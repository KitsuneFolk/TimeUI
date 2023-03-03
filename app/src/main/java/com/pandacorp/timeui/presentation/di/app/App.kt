package com.pandacorp.timeui.presentation.di.app

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class App : Application(), HasAndroidInjector {
    private var fragmentId: Int = 0
    
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    
    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder().application(this).build().inject(this)
        
    }
    
    override fun androidInjector(): AndroidInjector<Any> = androidInjector
    
    fun setFragmentId(fragmentId: Int) {
        this.fragmentId = fragmentId
    }
    
    fun getFragmentId(): Int = fragmentId
}