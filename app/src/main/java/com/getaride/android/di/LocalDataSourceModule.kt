package com.getaride.android.di

import android.content.Context
import androidx.room.Room
import com.getaride.android.db.GithubDb
import com.getaride.android.db.RepoDao
import com.getaride.android.repository.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

val remoteDataSourceModule : Module = module {

    // provide UserRepository component
    single { UserRepository(get(), get(), get()) }
}

val localDataSourceModule : Module = module {
    // provide GithubDb component
//    single { provideDb(androidApplication())}
    single { provideDb(androidContext())}
    // UserDao instance (get instance from GithubDb)
    single { get<GithubDb>().userDao() }
    // provide RepoDao component
    single { provideRepoDao(get()) }
}

val dataSourceModules = listOf(localDataSourceModule, remoteDataSourceModule)

fun provideDb(app: Context): GithubDb {
    return Room
            .databaseBuilder(app, GithubDb::class.java, "github.db")
            .fallbackToDestructiveMigration()
            .build()
}

fun provideRepoDao(db: GithubDb): RepoDao {
    return db.repoDao()
}