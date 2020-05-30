package org.task.manager.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.task.manager.web.rest.equalsVerifier

class BookTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Book::class)
        val book1 = Book()
        book1.id = 1L
        val book2 = Book()
        book2.id = book1.id
        assertThat(book1).isEqualTo(book2)
        book2.id = 2L
        assertThat(book1).isNotEqualTo(book2)
        book1.id = null
        assertThat(book1).isNotEqualTo(book2)
    }
}
