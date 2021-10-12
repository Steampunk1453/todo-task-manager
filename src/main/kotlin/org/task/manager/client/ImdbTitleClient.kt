package org.task.manager.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class ImdbTitleClient(private val restTemplate: RestTemplate) : TitleClient {

    @Value("\${audiovisual.host}")
    private lateinit var host: String

    @Value("\${audiovisual.api-key}")
    private lateinit var apiKey: String

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getItems(filter: String): TitleResponse {
        log.info("Get items from: $host/$filter/$apiKey")
        return restTemplate.getForEntity("$host/$filter/$apiKey", TitleResponse::class.java).body

    }

    override fun getItemInfo(filter: String, titleId: String?): Item {
        log.info("Get item info from: $host/$filter/$apiKey/$titleId")
        return restTemplate.getForEntity("$host/$filter/$apiKey/$titleId", Item::class.java).body
    }

}
