package org.task.manager.service.dto

data class TitleDTO(
    val title: String,
    val type: Type,
    val genres: List<String>?,
    val platform: String?,
    val website: String?
)

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
