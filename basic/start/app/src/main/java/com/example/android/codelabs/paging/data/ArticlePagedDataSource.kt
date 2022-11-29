package com.example.android.codelabs.paging.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import java.lang.Integer.max
import java.time.LocalDateTime

private const val STARTING_KEY = 0
private const val INITIAL_LOAD_TIME = 3_000L
private const val APPEND_LOAD_TIME = 1_000L

private val firstArticleCreatedTime = LocalDateTime.now()

class ArticlePagingSource() : PagingSource<Int, ArticleEntity>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleEntity> {
        val start = if (isInitialLoad(params)) params.key!! else STARTING_KEY
        val range = start.until(start + params.loadSize)

        Log.d("DBG", "Loading $range...")
        val loadTime = if (isInitialLoad(params)) INITIAL_LOAD_TIME else APPEND_LOAD_TIME
        delay(loadTime)

        return LoadResult.Page(
            data = range.map { index ->
                ArticleEntity(
                    id = index,
                    title = "Article #$index",
                    description = if (Math.random() > 0.5) "An interesting article" else "A boring article",
                    created = firstArticleCreatedTime.minusDays(index.toLong()),
                )
            },
            prevKey = if (start == STARTING_KEY) null else ensureValidKey(start - params.loadSize),
            nextKey = range.last + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleEntity>): Int? {
        TODO("getRefreshKey")
    }

    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)

    private fun isInitialLoad(params: LoadParams<Int>): Boolean
        = (params.key != null)
}
