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
import org.task.manager.domain.Title
import org.task.manager.repository.TitleRepository
import org.task.manager.web.rest.errors.ExceptionTranslator

/**
 * Integration tests for the [TitleResource] REST controller.
 *
 * @see TitleResource
 */
@SpringBootTest(classes = [ToDoTaskManagerApp::class])
class TitleResourceIT {

    @Autowired
    private lateinit var titleRepository: TitleRepository

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

    private lateinit var restTitleMockMvc: MockMvc

    private lateinit var title: Title

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val titleResource = TitleResource(titleRepository)
        this.restTitleMockMvc = MockMvcBuilders.standaloneSetup(titleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        title = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createTitle() {
        val databaseSizeBeforeCreate = titleRepository.findAll().size

        // Create the Title
        restTitleMockMvc.perform(
            post("/management/titles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(title))
        ).andExpect(status().isCreated)

        // Validate the Title in the database
        val titleList = titleRepository.findAll()
        assertThat(titleList).hasSize(databaseSizeBeforeCreate + 1)
        val testTitle = titleList[titleList.size - 1]
        assertThat(testTitle.name).isEqualTo(DEFAULT_NAME)
    }

    @Test
    @Transactional
    fun createTitleWithExistingId() {
        val databaseSizeBeforeCreate = titleRepository.findAll().size

        // Create the Title with an existing ID
        title.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restTitleMockMvc.perform(
            post("/management/titles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(title))
        ).andExpect(status().isBadRequest)

        // Validate the Title in the database
        val titleList = titleRepository.findAll()
        assertThat(titleList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = titleRepository.findAll().size
        // set the field null
        title.name = null

        // Create the Title, which fails.

        restTitleMockMvc.perform(
            post("/management/titles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(title))
        ).andExpect(status().isBadRequest)

        val titleList = titleRepository.findAll()
        assertThat(titleList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllTitles() {
        // Initialize the database
        titleRepository.saveAndFlush(title)

        // Get all the titleList
        restTitleMockMvc.perform(get("/api/titles?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(title.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
    }

    @Test
    @Transactional
    fun getTitle() {
        // Initialize the database
        titleRepository.saveAndFlush(title)

        val id = title.id
        assertNotNull(id)

        // Get the title
        restTitleMockMvc.perform(get("/api/titles/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
    }

    @Test
    @Transactional
    fun getNonExistingTitle() {
        // Get the title
        restTitleMockMvc.perform(get("/api/titles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateTitle() {
        // Initialize the database
        titleRepository.saveAndFlush(title)

        val databaseSizeBeforeUpdate = titleRepository.findAll().size

        // Update the title
        val id = title.id
        assertNotNull(id)
        val updatedTitle = titleRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedTitle are not directly saved in db
        em.detach(updatedTitle)
        updatedTitle.name = UPDATED_NAME

        restTitleMockMvc.perform(
            put("/management/titles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedTitle))
        ).andExpect(status().isOk)

        // Validate the Title in the database
        val titleList = titleRepository.findAll()
        assertThat(titleList).hasSize(databaseSizeBeforeUpdate)
        val testTitle = titleList[titleList.size - 1]
        assertThat(testTitle.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    fun updateNonExistingTitle() {
        val databaseSizeBeforeUpdate = titleRepository.findAll().size

        // Create the Title

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTitleMockMvc.perform(
            put("/management/titles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(title))
        ).andExpect(status().isBadRequest)

        // Validate the Title in the database
        val titleList = titleRepository.findAll()
        assertThat(titleList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteTitle() {
        // Initialize the database
        titleRepository.saveAndFlush(title)

        val databaseSizeBeforeDelete = titleRepository.findAll().size

        val id = title.id
        assertNotNull(id)

        // Delete the title
        restTitleMockMvc.perform(
            delete("/management/titles/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val titleList = titleRepository.findAll()
        assertThat(titleList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Title {
            val title = Title(
                name = DEFAULT_NAME
            )

            return title
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Title {
            val title = Title(
                name = UPDATED_NAME
            )

            return title
        }
    }
}
