package com.example.android.codelabs.paging.domain

import com.example.android.codelabs.paging.data.ArticleEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val articleDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

class Article(private val entity: ArticleEntity) {
    val id: Int
        get() = entity.id

    val title: String
        get() = entity.title

    val description: String
        get() = entity.description

    val created: LocalDateTime
        get() = entity.created

    val selected: Boolean
        get() = entity.selected

    val createdText: String
        get() = articleDateFormatter.format(created)

    override fun equals(other: Any?): Boolean {
        if (other !is Article) {
            return false
        }
        return entity == other.entity
    }

    override fun hashCode(): Int = entity.hashCode()
}
