package com.getaride.android.di

import androidx.room.Room
import com.getaride.android.AppCoroutineContexts
import com.getaride.android.db.GithubDb
import kotlinx.coroutines.experimental.Unconfined
import org.koin.dsl.module.module

val testModule = module {
    // provide CoroutinesManager
//    bean { DefaultCoroutinesManager() as CoroutinesManager }
    // provide AsyncTasksManager
//    bean { DefaultAsyncTasksManager() as AsyncTasksManager }

    // provide single instance AppCoroutineContexts
    single { AppCoroutineContexts(Unconfined, Unconfined, Unconfined) }
}

/**
 * In-Memory Room Database definition
 */
val roomTestModule = module {
    single {
        // In-Memory database config
        Room.inMemoryDatabaseBuilder(get(), GithubDb::class.java)
                .allowMainThreadQueries()
                .build()
    }
}

// Gather all app modules
//val testAppModules = listOf(testModule, roomTestModule, networkModule)
val testAppModules = listOf(testModule, roomTestModule)