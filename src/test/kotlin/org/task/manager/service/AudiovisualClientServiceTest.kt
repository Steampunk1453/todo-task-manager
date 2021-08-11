package org.task.manager.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.task.manager.client.AudiovisualClient
import org.task.manager.stub.ItemStub
import org.task.manager.web.rest.dto.AudiovisualResponse

@ExtendWith(MockKExtension::class)
internal class AudiovisualClientServiceTest {

    @InjectMockKs
    private lateinit var service: AudiovisualClientService

    @MockK
    private lateinit var client: AudiovisualClient

    private lateinit var audiovisualResponse: AudiovisualResponse


    @BeforeEach
    fun init() {
        audiovisualResponse = AudiovisualResponse(
            items = listOf(ItemStub.buildItem())
        )
    }

    @Test
    fun `should get titles when get by filter and title`() {
        val itemInfo = ItemStub.buildItemInfo()
        every { client.getAudiovisuals(any()) } returns audiovisualResponse
        every { client.getItemInfo(any(), any()) } returns itemInfo

        val result = service.getTitles("MostPopularTVSeries")

        verify(exactly = 2) { client.getItemInfo(any(), any()) }

        assertThat(result, `is`(not(nullValue())))
        assertThat(result[0].title, `is`(itemInfo.title))
        assertThat(result[0].type.value, `is`(itemInfo.type))
        assertThat(result[0].genres, `is`(itemInfo.genres?.split(",")))
        assertThat(result[0].platform, `is`("Amazon"))
        assertThat(result[0].website, `is`(itemInfo.officialWebsite))
    }

}
