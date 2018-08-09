/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.getaride.android.api

import androidx.lifecycle.LiveData
import com.getaride.android.vo.Authorization
import com.getaride.android.vo.Contributor
import com.getaride.android.vo.Repo
import com.getaride.android.vo.User
import kotlinx.coroutines.experimental.channels.Channel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST API access points
 */
interface GithubService {
    @POST("/authorizations")
    fun getAuthorization(
            @Query("scopes") scopes: Array<String>,
            @Query("note") note: String
    ): Channel<ApiResponse<Authorization>>

    @POST("/")
    fun authenticate(@Query("access_token") accessToken: String
    ): Channel<ApiResponse<Authorization>>


    //    @POST("user")
    @POST("/")
    fun authenticateUser(@Query("username") username: String,
                         @Query("password") password: String
    ): Channel<ApiResponse<Authorization>>

    @GET("users/{login}")
    fun getUser(@Path("login") login: String): Channel<ApiResponse<User>>
//    fun getUser(@Path("login") login: String): LiveData<ApiResponse<User>>
//    fun getUser(@Path("login") login: String): Call<ApiResponse<User>>

    @GET("users/{login}/repos")
    fun getRepos(@Path("login") login: String): LiveData<ApiResponse<List<Repo>>>

    @GET("repos/{owner}/{name}")
    fun getRepo(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): LiveData<ApiResponse<Repo>>

    @GET("repos/{owner}/{name}/contributors")
    fun getContributors(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): LiveData<ApiResponse<List<Contributor>>>

    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String): LiveData<ApiResponse<RepoSearchResponse>>

    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String, @Query("page") page: Int): Call<RepoSearchResponse>
}
