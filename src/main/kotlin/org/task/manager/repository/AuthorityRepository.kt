package org.task.manager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.task.manager.domain.Authority

/**
 * Spring Data JPA repository for the [Authority] entity.
 */

interface AuthorityRepository : JpaRepository<Authority, String>
