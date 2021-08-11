package org.task.manager.web.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.task.manager.service.dto.TitleDTO
import org.task.manager.service.dto.Type
import java.net.URI
import java.net.URISyntaxException

data class AudiovisualResponse(
    @JsonProperty("items")
    val items: List<Item>
)

data class Item(
    @JsonProperty("id")
    val id: String?,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("rank")
    val rank: String?,
    @JsonProperty("year")
    val year: String?,
    @JsonProperty("type")
    val type: String?,
    @JsonProperty("genres")
    val genres: String?,
    @JsonProperty("officialWebsite")
    val officialWebsite: String?
)

fun Item.toEntity(): TitleDTO = TitleDTO(
    title = title,
    type = Type.fromString(type.toString()),
    genres = genres?.split(","),
    platform =  if (officialWebsite?.isNotEmpty() == true) getDomainName(officialWebsite) else  "",
    website = officialWebsite
)

@Throws(URISyntaxException::class)
private fun getDomainName(url: String): String {
    val uri = URI(url)
    val domain = uri.host
    return if (domain.startsWith("www.")) domain.substring(4, domain.lastIndexOf(".")).capitalize() else domain.capitalize()
}



