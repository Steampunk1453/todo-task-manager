package org.task.manager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.task.manager.domain.Bookshop

/**
 * Spring Data  repository for the [Bookshop] entity.
 */
@Suppress("unused")
@Repository
interface BookshopRepository : JpaRepository<Bookshop, Long>
