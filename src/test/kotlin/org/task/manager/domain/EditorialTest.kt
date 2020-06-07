package org.task.manager.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.task.manager.web.rest.equalsVerifier

class EditorialTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Editorial::class)
        val editorial1 = Editorial()
        editorial1.id = 1L
        val editorial2 = Editorial()
        editorial2.id = editorial1.id
        assertThat(editorial1).isEqualTo(editorial2)
        editorial2.id = 2L
        assertThat(editorial1).isNotEqualTo(editorial2)
        editorial1.id = null
        assertThat(editorial1).isNotEqualTo(editorial2)
    }
}
