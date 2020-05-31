package org.task.manager.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.task.manager.web.rest.equalsVerifier

class BookshopTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Bookshop::class)
        val bookshop1 = Bookshop()
        bookshop1.id = 1L
        val bookshop2 = Bookshop()
        bookshop2.id = bookshop1.id
        assertThat(bookshop1).isEqualTo(bookshop2)
        bookshop2.id = 2L
        assertThat(bookshop1).isNotEqualTo(bookshop2)
        bookshop1.id = null
        assertThat(bookshop1).isNotEqualTo(bookshop2)
    }
}
