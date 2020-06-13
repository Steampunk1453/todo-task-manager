package org.task.manager.web.rest

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import org.springframework.web.context.WebApplicationContext
import org.task.manager.ToDoTaskManagerApp
import org.task.manager.domain.Audiovisual
import org.task.manager.repository.AudiovisualRepository
import org.task.manager.service.AudiovisualService
import org.task.manager.service.UserService
import org.task.manager.web.rest.errors.ExceptionTranslator

/**
 * Integration tests for the [AudiovisualResource] REST controller.
 *
 * @see AudiovisualResource
 */
@SpringBootTest(classes = [ToDoTaskManagerApp::class])
class AudiovisualResourceIT {

    @Autowired
    private lateinit var audiovisualRepository: AudiovisualRepository

    @Autowired
    private lateinit var audiovisualService: AudiovisualService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var restAudiovisualMockMvc: MockMvc

    private lateinit var audiovisual: Audiovisual

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val audiovisualResource = AudiovisualResource(audiovisualService, userService)
        this.restAudiovisualMockMvc = MockMvcBuilders.standaloneSetup(audiovisualResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        audiovisual = createEntity()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createAudiovisual() {
        val databaseSizeBeforeCreate = audiovisualRepository.findAll().size

        // Create the Audiovisual
        restAudiovisualMockMvc.perform(
            post("/api/audiovisuals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(audiovisual))
        ).andExpect(status().isCreated)

        // Validate the Audiovisual in the database
        val audiovisualList = audiovisualRepository.findAll()
        assertThat(audiovisualList).hasSize(databaseSizeBeforeCreate + 1)
        val testAudiovisual = audiovisualList[audiovisualList.size - 1]
        assertThat(testAudiovisual.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testAudiovisual.genre).isEqualTo(DEFAULT_GENRE)
        assertThat(testAudiovisual.platform).isEqualTo(DEFAULT_PLATFORM)
        assertThat(testAudiovisual.startDate).isEqualTo(DEFAULT_START_DATE)
        assertThat(testAudiovisual.deadline).isEqualTo(DEFAULT_DEADLINE)
        assertThat(testAudiovisual.check).isEqualTo(DEFAULT_CHECK)
    }

    @Test
    @Transactional
    fun createAudiovisualWithExistingId() {
        val databaseSizeBeforeCreate = audiovisualRepository.findAll().size

        // Create the Audiovisual with an existing ID
        audiovisual.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restAudiovisualMockMvc.perform(
            post("/api/audiovisuals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(audiovisual))
        ).andExpect(status().isBadRequest)

        // Validate the Audiovisual in the database
        val audiovisualList = audiovisualRepository.findAll()
        assertThat(audiovisualList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = audiovisualRepository.findAll().size
        // set the field null
        audiovisual.title = null

        // Create the Audiovisual, which fails.

        restAudiovisualMockMvc.perform(
            post("/api/audiovisuals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(audiovisual))
        ).andExpect(status().isBadRequest)

        val audiovisualList = audiovisualRepository.findAll()
        assertThat(audiovisualList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkStartDateIsRequired() {
        val databaseSizeBeforeTest = audiovisualRepository.findAll().size
        // set the field null
        audiovisual.startDate = null

        // Create the Audiovisual, which fails.

        restAudiovisualMockMvc.perform(
            post("/api/audiovisuals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(audiovisual))
        ).andExpect(status().isBadRequest)

        val audiovisualList = audiovisualRepository.findAll()
        assertThat(audiovisualList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkDeadlineIsRequired() {
        val databaseSizeBeforeTest = audiovisualRepository.findAll().size
        // set the field null
        audiovisual.deadline = null

        // Create the Audiovisual, which fails.

        restAudiovisualMockMvc.perform(
            post("/api/audiovisuals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(audiovisual))
        ).andExpect(status().isBadRequest)

        val audiovisualList = audiovisualRepository.findAll()
        assertThat(audiovisualList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllAudiovisuals() {
        // Initialize the database
        audiovisualRepository.saveAndFlush(audiovisual)

        // Create security-aware mockMvc
        restAudiovisualMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()

        // Get all the audiovisualList
        restAudiovisualMockMvc.perform(get("/api/audiovisuals?sort=id,desc")
            .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(audiovisual.id?.toInt())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].genre").value(hasItem(DEFAULT_GENRE)))
            .andExpect(jsonPath("$.[*].platform").value(hasItem(DEFAULT_PLATFORM)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].deadline").value(hasItem(DEFAULT_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].check").value(hasItem(DEFAULT_CHECK)))
    }

    @Test
    @Transactional
    fun getAudiovisual() {
        // Initialize the database
        audiovisualRepository.saveAndFlush(audiovisual)

        val id = audiovisual.id
        assertNotNull(id)

        // Get the audiovisual
        restAudiovisualMockMvc.perform(get("/api/audiovisuals/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.genre").value(DEFAULT_GENRE))
            .andExpect(jsonPath("$.platform").value(DEFAULT_PLATFORM))
            .andExpect(jsonPath("$.platformUrl").value(DEFAULT_PLATFORM_URL))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.deadline").value(DEFAULT_DEADLINE.toString()))
            .andExpect(jsonPath("$.check").value(DEFAULT_CHECK))
    }

    @Test
    @Transactional
    fun getNonExistingAudiovisual() {
        // Get the audiovisual
        restAudiovisualMockMvc.perform(get("/api/audiovisuals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateAudiovisual() {
        // Initialize the database
        audiovisualService.save(audiovisual)

        val databaseSizeBeforeUpdate = audiovisualRepository.findAll().size

        // Update the audiovisual
        val id = audiovisual.id
        assertNotNull(id)
        val updatedAudiovisual = audiovisualRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedAudiovisual are not directly saved in db
        em.detach(updatedAudiovisual)
        updatedAudiovisual.title = UPDATED_TITLE
        updatedAudiovisual.genre = UPDATED_GENRE
        updatedAudiovisual.platform = UPDATED_PLATFORM
        updatedAudiovisual.platformUrl = UPDATED_PLATFORM_URL
        updatedAudiovisual.startDate = UPDATED_START_DATE
        updatedAudiovisual.deadline = UPDATED_DEADLINE
        updatedAudiovisual.check = UPDATED_CHECK

        restAudiovisualMockMvc.perform(
            put("/api/audiovisuals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedAudiovisual))
        ).andExpect(status().isOk)

        // Validate the Audiovisual in the database
        val audiovisualList = audiovisualRepository.findAll()
        assertThat(audiovisualList).hasSize(databaseSizeBeforeUpdate)
        val testAudiovisual = audiovisualList[audiovisualList.size - 1]
        assertThat(testAudiovisual.title).isEqualTo(UPDATED_TITLE)
        assertThat(testAudiovisual.genre).isEqualTo(UPDATED_GENRE)
        assertThat(testAudiovisual.platform).isEqualTo(UPDATED_PLATFORM)
        assertThat(testAudiovisual.platformUrl).isEqualTo(UPDATED_PLATFORM_URL)
        assertThat(testAudiovisual.startDate).isEqualTo(UPDATED_START_DATE)
        assertThat(testAudiovisual.deadline).isEqualTo(UPDATED_DEADLINE)
        assertThat(testAudiovisual.check).isEqualTo(UPDATED_CHECK)
    }

    @Test
    @Transactional
    fun updateNonExistingAudiovisual() {
        val databaseSizeBeforeUpdate = audiovisualRepository.findAll().size

        // Create the Audiovisual

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAudiovisualMockMvc.perform(
            put("/api/audiovisuals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(audiovisual))
        ).andExpect(status().isBadRequest)

        // Validate the Audiovisual in the database
        val audiovisualList = audiovisualRepository.findAll()
        assertThat(audiovisualList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteAudiovisual() {
        // Initialize the database
        audiovisualService.save(audiovisual)

        val databaseSizeBeforeDelete = audiovisualRepository.findAll().size

        val id = audiovisual.id
        assertNotNull(id)

        // Delete the audiovisual
        restAudiovisualMockMvc.perform(
            delete("/api/audiovisuals/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val audiovisualList = audiovisualRepository.findAll()
        assertThat(audiovisualList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_GENRE = "AAAAAAAAAA"
        private const val UPDATED_GENRE = "BBBBBBBBBB"

        private const val DEFAULT_PLATFORM = "AAAAAAAAAA"
        private const val UPDATED_PLATFORM = "BBBBBBBBBB"

        private const val DEFAULT_PLATFORM_URL = "AAAAAAAAAA"
        private const val UPDATED_PLATFORM_URL = "BBBBBBBBBB"

        private val DEFAULT_START_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_START_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_DEADLINE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DEADLINE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_CHECK: Int = 1
        private const val UPDATED_CHECK: Int = 2

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Audiovisual {

            return Audiovisual(
                title = DEFAULT_TITLE,
                genre = DEFAULT_GENRE,
                platform = DEFAULT_PLATFORM,
                platformUrl = DEFAULT_PLATFORM_URL,
                startDate = DEFAULT_START_DATE,
                deadline = DEFAULT_DEADLINE,
                check = DEFAULT_CHECK
            )
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Audiovisual {
            return Audiovisual(
                title = UPDATED_TITLE,
                genre = UPDATED_GENRE,
                platform = UPDATED_PLATFORM,
                platformUrl = UPDATED_PLATFORM_URL,
                startDate = UPDATED_START_DATE,
                deadline = UPDATED_DEADLINE,
                check = UPDATED_CHECK
            )
        }
    }
}
