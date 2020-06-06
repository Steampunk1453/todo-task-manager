package org.task.manager.repository

import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.task.manager.domain.Book

/**
 * Spring Data  repository for the [Book] entity.
 */
@Suppress("unused")
@Repository
interface BookRepository : JpaRepository<Book, Long> {

    @Query("select book from Book book where book.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(pageable: Pageable): Page<Book>

    fun findAllByStartDateBetweenAndCheck(fromDate: Instant, toDate: Instant, check: Int): List<Book>

    fun findAllByDeadlineBetweenAndCheck(fromDate: Instant, toDate: Instant, check: Int): List<Book>
}
