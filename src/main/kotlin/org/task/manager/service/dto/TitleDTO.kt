package org.task.manager.service.dto

import org.task.manager.domain.TitleInfo
import java.net.URI
import java.net.URISyntaxException

data class TitleDTO(
    val title: String,
    val type: Type,
    val genres: List<String>?,
    val platform: String?,
    val website: String?
)

fun TitleInfo.toDto(): TitleDTO = TitleDTO(
    title = title,
    type = Type.fromString(type.toString()),
    genres = genres?.split(","),
    platform =  if (website?.isNotEmpty() == true) getDomainName(website!!) else  "",
    website = website
)

@Throws(URISyntaxException::class)
private fun getDomainName(url: String): String {
    val uri = URI(url)
    val domain = uri.host
    return if (domain.startsWith("www.")) domain.substring(4, domain.lastIndexOf(".")).capitalize() else domain.capitalize()
}

enum class Type(val value: String) {
    MOVIE("Movie"),
    TV_SHOW("TVSeries");

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromString(type: String): Type {
            return values().firstOrNull { it.value == type }
                ?: throw IllegalArgumentException("Not type found for: $type")
        }
    }

}
