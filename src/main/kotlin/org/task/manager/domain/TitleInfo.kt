package org.task.manager.domain

import org.hibernate.Hibernate
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TitleInfo(
    @Id
    val id: String?,
    @Column(name = "title", nullable = false)
    val title: String,
    @Column(name = "rank")
    val rank: Int?,
    @Column(name = "year")
    val year: Int?,
    @Column(name = "type", nullable = false)
    val type: String?,
    @Column(name = "genres", nullable = false)
    val genres: String?,
    @Column(name = "website", nullable = false)
    val website: String?
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TitleInfo

        return id != null && id == other.id
    }

    override fun hashCode(): Int = 1986846516

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , title = $title , rank = $rank , year = $year , type = $type , genres = $genres , website = $website )"
    }
}
