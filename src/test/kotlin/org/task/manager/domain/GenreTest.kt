package org.task.manager.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.task.manager.web.rest.equalsVerifier

class GenreTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Genre::class)
        val genre1 = Genre()
        genre1.id = 1L
        val genre2 = Genre()
        genre2.id = genre1.id
        assertThat(genre1).isEqualTo(genre2)
        genre2.id = 2L
        assertThat(genre1).isNotEqualTo(genre2)
        genre1.id = null
        assertThat(genre1).isNotEqualTo(genre2)
    }
}
