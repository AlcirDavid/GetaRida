package com.getaride.android.di

import com.getaride.android.AppCoroutineContexts
import com.getaride.android.coroutines.AsyncTasksManager
import com.getaride.android.coroutines.CoroutinesManager
import com.getaride.android.coroutines.DefaultAsyncTasksManager
import com.getaride.android.coroutines.DefaultCoroutinesManager
import com.getaride.android.ui.main.MainViewModel
import com.getaride.android.ui.movies.MoviesViewModel
import com.getaride.android.ui.newServiceArea.NewServiceAreaViewModel
import kotlinx.coroutines.experimental.Job
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

// Koin module
val appModule : Module = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { MoviesViewModel(get(), get()) }
    viewModel { NewServiceAreaViewModel(get(), get(), get()) }
//    viewModel { MainViewModel(get()) } // get() will resolve Repository instance
//    bean { MyRepository() as Repository }

    // ViewModel for Detail View
    //viewModel { params -> DetailViewModel(params["id"],get(), get()) }

    // Weather Data Repository
    //bean { WeatherRepositoryImpl(get()) as WeatherRepository }
}

//val rxModule : Module = module {
    // provided components
//    bean { ApplicationSchedulerProvider() as SchedulerProvider }
//}


val managerModule : Module = module {
    // provide CoroutinesManager
    bean { DefaultCoroutinesManager() as CoroutinesManager }
    // provide AsyncTasksManager
    bean { DefaultAsyncTasksManager() as AsyncTasksManager }
    // provide AppCoroutineContexts
    bean { AppCoroutineContexts() }
    //provide new instance of viewModel Job
    factory { Job() }
}

// Gather all app modules
val getaRideAppModules = listOf(appModule, managerModule, networkModule) + dataSourceModules