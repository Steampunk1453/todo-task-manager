package org.task.manager.client

import org.task.manager.web.rest.dto.AudiovisualResponse
import org.task.manager.web.rest.dto.Item

interface AudiovisualClient {
    fun getAudiovisuals(filter: String): AudiovisualResponse
    fun getItemInfo(filter: String, titleId: String?): Item
}
