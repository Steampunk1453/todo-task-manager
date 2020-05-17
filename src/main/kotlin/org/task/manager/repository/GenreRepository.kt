package org.task.manager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.task.manager.domain.Genre

/**
 * Spring Data  repository for the [Genre] entity.
 */
@Suppress("unused")
@Repository
interface GenreRepository : JpaRepository<Genre, Long>
