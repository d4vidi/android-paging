/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.android.codelabs.paging.data.ArticleEntity
import com.example.android.codelabs.paging.data.ArticleRepository
import com.example.android.codelabs.paging.domain.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * ViewModel for the [ArticleActivity] screen.
 * The ViewModel works with the [ArticleRepository] to get the data.
 */
class ArticleViewModel(private val repository: ArticleRepository)
    : ViewModel() {

    /**
     * Stream of [ArticleEntity]s for the UI.
     */
    val items: Flow<PagingData<Article>> = Pager(
        config = PagingConfig(10, maxSize = 50, prefetchDistance = 1, enablePlaceholders = false),
        pagingSourceFactory = { repository.articlePagingSource },
    )
        .flow
        .map { it.map { entity -> Article(entity) } }
        .cachedIn(viewModelScope)
}
