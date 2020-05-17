package org.task.manager.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.task.manager.web.rest.equalsVerifier

class PlatformTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Platform::class)
        val platform1 = Platform()
        platform1.id = 1L
        val platform2 = Platform()
        platform2.id = platform1.id
        assertThat(platform1).isEqualTo(platform2)
        platform2.id = 2L
        assertThat(platform1).isNotEqualTo(platform2)
        platform1.id = null
        assertThat(platform1).isNotEqualTo(platform2)
    }
}
