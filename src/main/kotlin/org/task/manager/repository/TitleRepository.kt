package org.task.manager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.task.manager.domain.Title

/**
 * Spring Data  repository for the [Title] entity.
 */
@Suppress("unused")
@Repository
interface TitleRepository : JpaRepository<Title, Long>
