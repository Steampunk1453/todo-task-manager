package org.task.manager.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.task.manager.web.rest.equalsVerifier

class AudiovisualTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Audiovisual::class)
        val audiovisual1 = Audiovisual()
        audiovisual1.id = 1L
        val audiovisual2 = Audiovisual()
        audiovisual2.id = audiovisual1.id
        assertThat(audiovisual1).isEqualTo(audiovisual2)
        audiovisual2.id = 2L
        assertThat(audiovisual1).isNotEqualTo(audiovisual2)
        audiovisual1.id = null
        assertThat(audiovisual1).isNotEqualTo(audiovisual2)
    }
}
