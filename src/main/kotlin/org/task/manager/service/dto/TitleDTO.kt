package org.task.manager.service.dto

import org.task.manager.domain.TitleInfo
import java.net.URI
import java.net.URISyntaxException

private const val PATTERN_ALL_AFTER_LAST_DOT = ".[^.]*$"
private const val APPLE_TV =  "Apple TV+"

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
    platform = if (website?.isNotEmpty() == true) getPlatformFromUrl(website!!) else null,
    website = website
)

@Throws(URISyntaxException::class)
private fun getPlatformFromUrl(url: String): String {
    val uri = URI(url)
    val host = uri.host
    return extractPlatform(host)
}

private fun extractPlatform(host: String): String {
    var platform = if (host.startsWith("www.")) host.substring(4, host.lastIndexOf(".")).capitalize() else host.capitalize()
    platform = if (platform.contains(".")) platform.replace(PATTERN_ALL_AFTER_LAST_DOT.toRegex(), "") else platform
    platform = checkAppleTv(platform)
    return platform
}

private fun checkAppleTv(platform: String): String {
    return if (platform.toLowerCase().contains("apple")) APPLE_TV else platform
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
