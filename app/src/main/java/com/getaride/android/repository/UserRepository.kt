package com.getaride.android.repository

import com.getaride.android.AppCoroutineContexts
import com.getaride.android.api.ApiResponse
import com.getaride.android.api.GithubService
import com.getaride.android.db.UserDao
import com.getaride.android.vo.Authorization
import com.getaride.android.vo.Resource
import com.getaride.android.vo.User
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SubscriptionReceiveChannel

class UserRepository constructor(
        private val appExecutors: AppCoroutineContexts,
        private val userDao: UserDao,
        private val githubService: GithubService
) {

    fun authenticateUser(login: String, password: String)
            = githubService.authenticateUser(login, password)

    fun authenticate(scopes : Array<String>, note: String) :Channel<ApiResponse<Authorization>> {
        return githubService.getAuthorization(scopes, note)
    }

    fun authenticate(note: String) :Channel<ApiResponse<Authorization>> {
        return githubService.authenticate(note)
    }

//    fun loadUser(login: String): LiveData<Resource<User>> {
    fun loadUser(login: String, parentJob :Job): SubscriptionReceiveChannel<Resource<User>> {
        return object : CoroutinesNetworkBoundResource<User, User>(appExecutors, parentJob) {
            override suspend fun saveCallResult(item: User) {
                userDao.insert(item)
            }

            override fun shouldFetch(data: User?)= data == null

            override suspend fun loadFromDb()= userDao.findByLogin(login)

            override suspend fun createCall()= githubService.getUser(login)
        }.asChannel()
    }
}