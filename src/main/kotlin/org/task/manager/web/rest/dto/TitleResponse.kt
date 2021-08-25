package org.task.manager.web.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.task.manager.domain.TitleInfo
import java.net.URI
import java.net.URISyntaxException

data class TitleResponse(
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

fun Item.toEntity(): TitleInfo = TitleInfo(
    id = id,
    title = title,
    rank = rank?.toInt(),
    year = year?.toInt(),
    type = type,
    genres = genres,
    website = officialWebsite
)

@Throws(URISyntaxException::class)
private fun getDomainName(url: String): String {
    val uri = URI(url)
    val domain = uri.host
    return if (domain.startsWith("www.")) domain.substring(4, domain.lastIndexOf(".")).capitalize() else domain.capitalize()
}



