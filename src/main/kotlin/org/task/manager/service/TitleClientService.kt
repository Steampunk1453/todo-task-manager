package org.task.manager.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.task.manager.client.TitleClient
import org.task.manager.repository.TitleInfoRepository
import org.task.manager.service.dto.TitleDTO
import org.task.manager.service.dto.toDto
import org.task.manager.web.rest.dto.toEntity

private const val FILTER_BY_TITLE = "Title"
private const val FILTER_BY_EXTERNAL_SITES = "ExternalSites"

@Service
@Transactional
class TitleClientService(private val titleClient: TitleClient,
                         private val titleInfoRepository: TitleInfoRepository) {

    @Value("\${audiovisual.limit-size}")
    private lateinit var limitSize: String

    @Value("\${audiovisual.limit-size}")
    private lateinit var filter: String

    @Value("\${audiovisual.limit-size}")
    private lateinit var filter2: String

    /**
     * Save titles once every day
     *
     * This is scheduled to get fired every day, at 08:00 (am) in the system.
     */
    @Scheduled(cron = "0 00 08 * * ?")

    fun saveTitles() {
        saveShowTitles()
        saveMovieTitles()
    }

    fun saveShowTitles() {
        val items = titleClient.getItems(filter).items
        val titles = items.take(limitSize.toInt())
            .map { i -> titleClient.getItemInfo(FILTER_BY_TITLE, i.id).toEntity()
                .let { titleClient.getItemInfo(FILTER_BY_EXTERNAL_SITES, i.id).toEntity() }
            }
        titleInfoRepository.saveAll(titles)
    }

    fun saveMovieTitles() {
        val items = titleClient.getItems(filter2).items
        val titles = items.take(limitSize.toInt())
            .map { i -> titleClient.getItemInfo(FILTER_BY_TITLE, i.id).toEntity()
                .let { titleClient.getItemInfo(FILTER_BY_EXTERNAL_SITES, i.id).toEntity() }
            }
        titleInfoRepository.saveAll(titles)
    }


    fun getTitles(filter: String): List<TitleDTO> {
        return titleInfoRepository.findAll().map { t -> t.toDto() }
    }

}
