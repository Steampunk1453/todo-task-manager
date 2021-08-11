package org.task.manager.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.task.manager.client.AudiovisualClient
import org.task.manager.service.dto.TitleDTO
import org.task.manager.web.rest.dto.toEntity

private const val FILTER_BY_TITLE = "Title"
private const val FILTER_BY_EXTERNAL_SITES = "ExternalSites"

@Service
class AudiovisualClientService(private val audiovisualClient: AudiovisualClient) {

    @Value("\${audiovisual.limit-size}")
    private lateinit var limitSize: String

    fun getTitles(filter: String): List<TitleDTO> {
        val items = audiovisualClient.getAudiovisuals(filter).items
        val titles = items.take(limitSize.toInt())
            .map { i -> audiovisualClient.getItemInfo(FILTER_BY_TITLE, i.id).toEntity()
                .let { audiovisualClient.getItemInfo(FILTER_BY_EXTERNAL_SITES, i.id).toEntity() }
        }
        return titles
    }

}
