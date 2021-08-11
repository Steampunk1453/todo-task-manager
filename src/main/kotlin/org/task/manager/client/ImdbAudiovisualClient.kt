package org.task.manager.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.task.manager.web.rest.dto.AudiovisualResponse
import org.task.manager.web.rest.dto.Item

@Component
class ImdbAudiovisualClient(private val restTemplate: RestTemplate) : AudiovisualClient {

    @Value("\${audiovisual.host}")
    private lateinit var host: String

    @Value("\${audiovisual.api-key}")
    private lateinit var apiKey: String

    override fun getAudiovisuals(filter: String): AudiovisualResponse {
        return restTemplate.getForEntity("$host/$filter/$apiKey", AudiovisualResponse::class.java).body

    }

    override fun getItemInfo(filter: String, titleId: String?): Item {
        return restTemplate.getForEntity("$host/$filter/$apiKey/$titleId", Item::class.java).body
    }

}
