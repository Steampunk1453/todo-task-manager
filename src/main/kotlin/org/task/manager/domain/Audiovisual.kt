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
 * A Audiovisual.
 */
@Entity
@Table(name = "audiovisual")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
data class Audiovisual(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Column(name = "genre")
    var genre: String? = null,

    @Column(name = "platform")
    var platform: String? = null,

    @Column(name = "platform_url")
    var platformUrl: String? = null,

    @get: NotNull
    @Column(name = "start_date", nullable = false)
    var startDate: Instant? = null,

    @get: NotNull
    @Column(name = "deadline", nullable = false)
    var deadline: Instant? = null,

    @Column(name = "jhi_check")
    var check: Int? = null,

    @ManyToOne @JsonIgnoreProperties("audiovisuals")
    var user: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Audiovisual) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Audiovisual{" +
        "id=$id" +
        ", title='$title'" +
        ", genre='$genre'" +
        ", platform='$platform'" +
        ", platformUrl='$platformUrl'" +
        ", startDate='$startDate'" +
        ", deadline='$deadline'" +
        ", check=$check" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
