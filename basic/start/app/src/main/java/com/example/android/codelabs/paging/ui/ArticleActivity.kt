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

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadState.Loading
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.codelabs.paging.Injection
import com.example.android.codelabs.paging.databinding.ActivityArticlesBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityArticlesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Get the view model
        val viewModel by viewModels<ArticleViewModel>(
            factoryProducer = { Injection.provideViewModelFactory(owner = this) }
        )

        val items = viewModel.items
        val articleAdapter = ArticleAdapter()

        binding.bindAdapter(articleAdapter)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                articleAdapter.loadStateFlow.collect {
                    Log.i("DBG", "Load state changed: prepend=${it.source.prepend} append=${it.source.append}")

                    binding.initialProgress.isVisible = it.source.refresh is Loading
                    binding.prependProgress.isVisible = it.source.prepend is Loading
                    binding.appendProgress.isVisible = it.source.append is Loading
                }
            }
        }

        // Collect from the Article Flow in the ViewModel, and submit it to the ListAdapter.
        lifecycleScope.launch {
            // We repeat on the STARTED lifecycle because an Activity may be PAUSED
            // but still visible on the screen, for example in a multi window app
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                items.collectLatest {
                    articleAdapter.submitData(it)
                }
            }
        }
    }
}

/**
 * Sets up the [RecyclerView] and binds [ArticleAdapter] to it
 */
private fun ActivityArticlesBinding.bindAdapter(articleAdapter: ArticleAdapter) {
    val decoration = DividerItemDecoration(list.context, DividerItemDecoration.VERTICAL)

    list.adapter = articleAdapter
    list.layoutManager = LinearLayoutManager(list.context)
    list.addItemDecoration(decoration)
}
