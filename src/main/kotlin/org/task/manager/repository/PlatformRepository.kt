package org.task.manager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.task.manager.domain.Platform

/**
 * Spring Data  repository for the [Platform] entity.
 */
@Suppress("unused")
@Repository
interface PlatformRepository : JpaRepository<Platform, Long>
