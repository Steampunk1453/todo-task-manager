package org.task.manager.service.dto

import org.task.manager.domain.Audiovisual
import org.task.manager.domain.User

data class NotificationDTO(val name: String?, val message: String?, val url: String?, val startDate: Boolean, val user: User?)

fun Audiovisual.toDto(isStartDate: Boolean): NotificationDTO = NotificationDTO(
    name = title,
    message = platform,
    url = platformUrl,
    startDate = isStartDate,
    user = user
)
