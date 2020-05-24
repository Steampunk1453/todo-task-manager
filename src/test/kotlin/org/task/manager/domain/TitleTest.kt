package org.task.manager.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.task.manager.web.rest.equalsVerifier

class TitleTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Title::class)
        val title1 = Title()
        title1.id = 1L
        val title2 = Title()
        title2.id = title1.id
        assertThat(title1).isEqualTo(title2)
        title2.id = 2L
        assertThat(title1).isNotEqualTo(title2)
        title1.id = null
        assertThat(title1).isNotEqualTo(title2)
    }
}
