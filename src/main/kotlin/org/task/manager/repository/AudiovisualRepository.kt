package org.task.manager.repository

import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.task.manager.domain.Audiovisual

/**
 * Spring Data  repository for the [Audiovisual] entity.
 */
@Suppress("unused")
@Repository
interface AudiovisualRepository : JpaRepository<Audiovisual, Long> {

    @Query("select audiovisual from Audiovisual audiovisual where audiovisual.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(pageable: Pageable): Page<Audiovisual>

    fun findAllByStartDateBetweenAndCheck(fromDate: Instant, toDate: Instant, check: Int): List<Audiovisual>

    fun findAllByDeadlineBetweenAndCheck(fromDate: Instant, toDate: Instant, check: Int): List<Audiovisual>
}
