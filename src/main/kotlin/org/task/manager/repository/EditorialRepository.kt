package org.task.manager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.task.manager.domain.Editorial

/**
 * Spring Data  repository for the [Editorial] entity.
 */
@Suppress("unused")
@Repository
interface EditorialRepository : JpaRepository<Editorial, Long>
