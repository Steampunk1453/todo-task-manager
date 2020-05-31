package org.task.manager.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A Book.
 */
@Entity
@Table(name = "book")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Column(name = "author")
    var author: String? = null,

    @Column(name = "genre")
    var genre: String? = null,

    @Column(name = "editorial")
    var editorial: String? = null,

    @Column(name = "bookshop")
    var bookshop: String? = null,

    @Column(name = "bookshop_url")
    var bookshopUrl: String? = null,

    @get: NotNull
    @Column(name = "start_date", nullable = false)
    var startDate: Instant? = null,

    @get: NotNull
    @Column(name = "deadline", nullable = false)
    var deadline: Instant? = null,

    @Column(name = "jhi_check")
    var check: Int? = null,

    @ManyToOne @JsonIgnoreProperties("books")
    var user: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Book) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Book{" +
        "id=$id" +
        ", title='$title'" +
        ", author='$author'" +
        ", genre='$genre'" +
        ", editorial='$editorial'" +
        ", bookshop='$bookshop'" +
        ", bookshopUrl='$bookshopUrl'" +
        ", startDate='$startDate'" +
        ", deadline='$deadline'" +
        ", check=$check" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
