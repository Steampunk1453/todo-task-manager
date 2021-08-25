package org.task.manager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.task.manager.domain.TitleInfo

@Repository
interface TitleInfoRepository : JpaRepository<TitleInfo, String>
