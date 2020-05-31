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
import org.task.manager.domain.Bookshop
import org.task.manager.repository.BookshopRepository
import org.task.manager.web.rest.errors.ExceptionTranslator

/**
 * Integration tests for the [BookshopResource] REST controller.
 *
 * @see BookshopResource
 */
@SpringBootTest(classes = [ToDoTaskManagerApp::class])
class BookshopResourceIT {

    @Autowired
    private lateinit var bookshopRepository: BookshopRepository

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

    private lateinit var restBookshopMockMvc: MockMvc

    private lateinit var bookshop: Bookshop

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val bookshopResource = BookshopResource(bookshopRepository)
        this.restBookshopMockMvc = MockMvcBuilders.standaloneSetup(bookshopResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        bookshop = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createBookshop() {
        val databaseSizeBeforeCreate = bookshopRepository.findAll().size

        // Create the Bookshop
        restBookshopMockMvc.perform(
            post("/api/bookshops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bookshop))
        ).andExpect(status().isCreated)

        // Validate the Bookshop in the database
        val bookshopList = bookshopRepository.findAll()
        assertThat(bookshopList).hasSize(databaseSizeBeforeCreate + 1)
        val testBookshop = bookshopList[bookshopList.size - 1]
        assertThat(testBookshop.name).isEqualTo(DEFAULT_NAME)
        assertThat(testBookshop.url).isEqualTo(DEFAULT_URL)
    }

    @Test
    @Transactional
    fun createBookshopWithExistingId() {
        val databaseSizeBeforeCreate = bookshopRepository.findAll().size

        // Create the Bookshop with an existing ID
        bookshop.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookshopMockMvc.perform(
            post("/api/bookshops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bookshop))
        ).andExpect(status().isBadRequest)

        // Validate the Bookshop in the database
        val bookshopList = bookshopRepository.findAll()
        assertThat(bookshopList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = bookshopRepository.findAll().size
        // set the field null
        bookshop.name = null

        // Create the Bookshop, which fails.

        restBookshopMockMvc.perform(
            post("/api/bookshops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bookshop))
        ).andExpect(status().isBadRequest)

        val bookshopList = bookshopRepository.findAll()
        assertThat(bookshopList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkUrlIsRequired() {
        val databaseSizeBeforeTest = bookshopRepository.findAll().size
        // set the field null
        bookshop.url = null

        // Create the Bookshop, which fails.

        restBookshopMockMvc.perform(
            post("/api/bookshops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bookshop))
        ).andExpect(status().isBadRequest)

        val bookshopList = bookshopRepository.findAll()
        assertThat(bookshopList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllBookshops() {
        // Initialize the database
        bookshopRepository.saveAndFlush(bookshop)

        // Get all the bookshopList
        restBookshopMockMvc.perform(get("/api/bookshops?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookshop.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
    }

    @Test
    @Transactional
    fun getBookshop() {
        // Initialize the database
        bookshopRepository.saveAndFlush(bookshop)

        val id = bookshop.id
        assertNotNull(id)

        // Get the bookshop
        restBookshopMockMvc.perform(get("/api/bookshops/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
    }

    @Test
    @Transactional
    fun getNonExistingBookshop() {
        // Get the bookshop
        restBookshopMockMvc.perform(get("/api/bookshops/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateBookshop() {
        // Initialize the database
        bookshopRepository.saveAndFlush(bookshop)

        val databaseSizeBeforeUpdate = bookshopRepository.findAll().size

        // Update the bookshop
        val id = bookshop.id
        assertNotNull(id)
        val updatedBookshop = bookshopRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedBookshop are not directly saved in db
        em.detach(updatedBookshop)
        updatedBookshop.name = UPDATED_NAME
        updatedBookshop.url = UPDATED_URL

        restBookshopMockMvc.perform(
            put("/api/bookshops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedBookshop))
        ).andExpect(status().isOk)

        // Validate the Bookshop in the database
        val bookshopList = bookshopRepository.findAll()
        assertThat(bookshopList).hasSize(databaseSizeBeforeUpdate)
        val testBookshop = bookshopList[bookshopList.size - 1]
        assertThat(testBookshop.name).isEqualTo(UPDATED_NAME)
        assertThat(testBookshop.url).isEqualTo(UPDATED_URL)
    }

    @Test
    @Transactional
    fun updateNonExistingBookshop() {
        val databaseSizeBeforeUpdate = bookshopRepository.findAll().size

        // Create the Bookshop

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookshopMockMvc.perform(
            put("/api/bookshops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bookshop))
        ).andExpect(status().isBadRequest)

        // Validate the Bookshop in the database
        val bookshopList = bookshopRepository.findAll()
        assertThat(bookshopList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteBookshop() {
        // Initialize the database
        bookshopRepository.saveAndFlush(bookshop)

        val databaseSizeBeforeDelete = bookshopRepository.findAll().size

        val id = bookshop.id
        assertNotNull(id)

        // Delete the bookshop
        restBookshopMockMvc.perform(
            delete("/api/bookshops/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val bookshopList = bookshopRepository.findAll()
        assertThat(bookshopList).hasSize(databaseSizeBeforeDelete - 1)
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
        fun createEntity(em: EntityManager): Bookshop {
            val bookshop = Bookshop(
                name = DEFAULT_NAME,
                url = DEFAULT_URL
            )

            return bookshop
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Bookshop {
            val bookshop = Bookshop(
                name = UPDATED_NAME,
                url = UPDATED_URL
            )

            return bookshop
        }
    }
}
