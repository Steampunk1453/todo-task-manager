package org.task.manager.web.rest

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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import org.task.manager.ToDoTaskManagerApp
import org.task.manager.domain.Platform
import org.task.manager.repository.PlatformRepository
import org.task.manager.web.rest.errors.ExceptionTranslator

/**
 * Integration tests for the [PlatformResource] REST controller.
 *
 * @see PlatformResource
 */
@SpringBootTest(classes = [ToDoTaskManagerApp::class])
class PlatformResourceIT {

    @Autowired
    private lateinit var platformRepository: PlatformRepository

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

    private lateinit var restPlatformMockMvc: MockMvc

    private lateinit var platform: Platform

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val platformResource = PlatformResource(platformRepository)
        this.restPlatformMockMvc = MockMvcBuilders.standaloneSetup(platformResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        platform = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createPlatform() {
        val databaseSizeBeforeCreate = platformRepository.findAll().size

        // Create the Platform
        restPlatformMockMvc.perform(
            post("/api/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(platform))
        ).andExpect(status().isCreated)

        // Validate the Platform in the database
        val platformList = platformRepository.findAll()
        assertThat(platformList).hasSize(databaseSizeBeforeCreate + 1)
        val testPlatform = platformList[platformList.size - 1]
        assertThat(testPlatform.name).isEqualTo(DEFAULT_NAME)
        assertThat(testPlatform.url).isEqualTo(DEFAULT_URL)
    }

    @Test
    @Transactional
    fun createPlatformWithExistingId() {
        val databaseSizeBeforeCreate = platformRepository.findAll().size

        // Create the Platform with an existing ID
        platform.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlatformMockMvc.perform(
            post("/api/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(platform))
        ).andExpect(status().isBadRequest)

        // Validate the Platform in the database
        val platformList = platformRepository.findAll()
        assertThat(platformList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = platformRepository.findAll().size
        // set the field null
        platform.name = null

        // Create the Platform, which fails.

        restPlatformMockMvc.perform(
            post("/api/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(platform))
        ).andExpect(status().isBadRequest)

        val platformList = platformRepository.findAll()
        assertThat(platformList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkUrlIsRequired() {
        val databaseSizeBeforeTest = platformRepository.findAll().size
        // set the field null
        platform.url = null

        // Create the Platform, which fails.

        restPlatformMockMvc.perform(
            post("/api/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(platform))
        ).andExpect(status().isBadRequest)

        val platformList = platformRepository.findAll()
        assertThat(platformList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllPlatforms() {
        // Initialize the database
        platformRepository.saveAndFlush(platform)

        // Get all the platformList
        restPlatformMockMvc.perform(get("/api/platforms?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(platform.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
    }

    @Test
    @Transactional
    fun getPlatform() {
        // Initialize the database
        platformRepository.saveAndFlush(platform)

        val id = platform.id
        assertNotNull(id)

        // Get the platform
        restPlatformMockMvc.perform(get("/api/platforms/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
    }

    @Test
    @Transactional
    fun getNonExistingPlatform() {
        // Get the platform
        restPlatformMockMvc.perform(get("/api/platforms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updatePlatform() {
        // Initialize the database
        platformRepository.saveAndFlush(platform)

        val databaseSizeBeforeUpdate = platformRepository.findAll().size

        // Update the platform
        val id = platform.id
        assertNotNull(id)
        val updatedPlatform = platformRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedPlatform are not directly saved in db
        em.detach(updatedPlatform)
        updatedPlatform.name = UPDATED_NAME
        updatedPlatform.url = UPDATED_URL

        restPlatformMockMvc.perform(
            put("/api/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedPlatform))
        ).andExpect(status().isOk)

        // Validate the Platform in the database
        val platformList = platformRepository.findAll()
        assertThat(platformList).hasSize(databaseSizeBeforeUpdate)
        val testPlatform = platformList[platformList.size - 1]
        assertThat(testPlatform.name).isEqualTo(UPDATED_NAME)
        assertThat(testPlatform.url).isEqualTo(UPDATED_URL)
    }

    @Test
    @Transactional
    fun updateNonExistingPlatform() {
        val databaseSizeBeforeUpdate = platformRepository.findAll().size

        // Create the Platform

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatformMockMvc.perform(
            put("/api/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(platform))
        ).andExpect(status().isBadRequest)

        // Validate the Platform in the database
        val platformList = platformRepository.findAll()
        assertThat(platformList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deletePlatform() {
        // Initialize the database
        platformRepository.saveAndFlush(platform)

        val databaseSizeBeforeDelete = platformRepository.findAll().size

        val id = platform.id
        assertNotNull(id)

        // Delete the platform
        restPlatformMockMvc.perform(
            delete("/api/platforms/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val platformList = platformRepository.findAll()
        assertThat(platformList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_URL = "AAAAAAAAAA"
        private const val UPDATED_URL = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Platform {
            val platform = Platform(
                name = DEFAULT_NAME,
                url = DEFAULT_URL
            )

            return platform
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Platform {
            val platform = Platform(
                name = UPDATED_NAME,
                url = UPDATED_URL
            )

            return platform
        }
    }
}
