package com.example.android.codelabs.paging.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.api.IN_QUALIFIER
import com.example.android.codelabs.paging.model.Repo
import retrofit2.HttpException
import java.io.IOException

// GitHub page API is 1 based: https://developer.github.com/v3/#pagination
private const val GITHUB_STARTING_PAGE_INDEX = 1

private const val NETWORK_PAGE_SIZE = 30

class GithubReposPagingSource(
    private val service: GithubService,
    private val searchText: String,
): PagingSource<Int, Repo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        val position = params.key ?: GITHUB_STARTING_PAGE_INDEX
        val query = searchText + IN_QUALIFIER

        return try {
            val response = service.searchRepos(query, position, params.loadSize)
            val repos = response.items
            val prevKey = if (params.key == null) null else position
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }

            LoadResult.Page(repos, prevKey, nextKey)
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? {
//        // We need to get the previous key (or next key if previous is null) of the page
//        // that was closest to the most recently accessed index.
//        // Anchor position is the most recently accessed index
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
        TODO()
    }
}
