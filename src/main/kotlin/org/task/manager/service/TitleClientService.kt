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
import org.task.manager.client.toEntity

private const val FILTER_BY_TITLE = "Title"
private const val FILTER_BY_EXTERNAL_SITES = "ExternalSites"

@Service
@Transactional
class TitleClientService(
    private val titleClient: TitleClient,
    private val titleInfoRepository: TitleInfoRepository
) {

    @Value("\${audiovisual.size-limit}")
    private lateinit var sizeLimit: String

    @Value("\${audiovisual.filter-movies}")
    private lateinit var movies: String

    @Value("\${audiovisual.filter-shows}")
    private lateinit var shows: String

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save titles once every day
     *
     * This is scheduled to get fired every day, at 07:05 (am) in the system.
     */
    @Scheduled(cron = "0 05 07 * * ?")
    fun saveTitles() {
        titleInfoRepository.deleteAll()
        saveTitlesInfo(movies)
        saveTitlesInfo(shows)
    }

    fun saveTitlesInfo(filter: String) {
        try {
            val items = titleClient.getItems(filter).items
            val titles = items.take(sizeLimit.toInt())
                .map { i ->
                    titleClient.getItemInfo(FILTER_BY_TITLE, i.id).toEntity()
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

    fun getTitles(): List<TitleDTO> {
        return titleInfoRepository.findAll().map { t -> t.toDto() }
    }

    fun getTitlesByFilter(filter: String): List<TitleDTO> {
        return titleInfoRepository.findAllByType(filter).map { t -> t.toDto() }
    }

}
