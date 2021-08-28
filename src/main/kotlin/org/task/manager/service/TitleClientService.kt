package org.task.manager.service

import org.slf4j.LoggerFactory
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

    @Value("\${audiovisual.filter-movies}")
    private lateinit var movies: String

    @Value("\${audiovisual.filter-shows}")
    private lateinit var shows: String

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save titles once every day
     *
     * This is scheduled to get fired every day, at 08:00 (am) in the system.
     */
    @Scheduled(cron = "0 00 08 * * ?")
    fun saveTitles() {
        titleInfoRepository.deleteAll()
        saveTitlesInfo(movies)
        saveTitlesInfo(shows)
    }

    fun saveTitlesInfo(filter: String) {
        try {
            val items = titleClient.getItems(filter).items
            val titles = items.take(limitSize.toInt())
                .map { i -> titleClient.getItemInfo(FILTER_BY_TITLE, i.id).toEntity()
                    .apply {
                        rank = i.rank?.toInt()
                        website = titleClient.getItemInfo(FILTER_BY_EXTERNAL_SITES, i.id).officialWebsite
                    }
                }
            titleInfoRepository.saveAll(titles)
        } catch (ex: Exception) {
            log.error("Error retrieving title info: ", ex)
        }
    }

    fun getTitles(type: String): List<TitleDTO> {
        return titleInfoRepository.findAllByType(type).map { t -> t.toDto() }
    }

}
