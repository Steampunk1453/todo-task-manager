package org.task.manager.domain

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A Editorial.
 */
@Entity
@Table(name = "editorial")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
data class Editorial(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @Column(name = "name")
    var name: String? = null,

    @get: NotNull
    @Column(name = "url", nullable = false)
    var url: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Editorial) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Editorial{" +
        "id=$id" +
        ", name='$name'" +
        ", url='$url'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
