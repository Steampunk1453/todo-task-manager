package org.task.manager.domain

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A Bookshop.
 */
@Entity
@Table(name = "bookshop")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
data class Bookshop(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @get: NotNull
    @Column(name = "url", nullable = false)
    var url: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Bookshop) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Bookshop{" +
        "id=$id" +
        ", name='$name'" +
        ", url='$url'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
