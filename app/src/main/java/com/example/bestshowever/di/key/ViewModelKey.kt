package com.example.bestshowever.di.key

import androidx.lifecycle.ViewModel
import com.example.bestshowever.di.scope.ApplicationScope
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
@ApplicationScope
annotation class ViewModelKey(val value: KClass<out ViewModel>)

