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
import org.task.manager.domain.Genre
import org.task.manager.repository.GenreRepository
import org.task.manager.web.rest.errors.ExceptionTranslator

/**
 * Integration tests for the [GenreResource] REST controller.
 *
 * @see GenreResource
 */
@SpringBootTest(classes = [ToDoTaskManagerApp::class])
class GenreResourceIT {

    @Autowired
    private lateinit var genreRepository: GenreRepository

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

    private lateinit var restGenreMockMvc: MockMvc

    private lateinit var genre: Genre

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val genreResource = GenreResource(genreRepository)
        this.restGenreMockMvc = MockMvcBuilders.standaloneSetup(genreResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        genre = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createGenre() {
        val databaseSizeBeforeCreate = genreRepository.findAll().size

        // Create the Genre
        restGenreMockMvc.perform(
            post("/api/management/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(genre))
        ).andExpect(status().isCreated)

        // Validate the Genre in the database
        val genreList = genreRepository.findAll()
        assertThat(genreList).hasSize(databaseSizeBeforeCreate + 1)
        val testGenre = genreList[genreList.size - 1]
        assertThat(testGenre.name).isEqualTo(DEFAULT_NAME)
        assertThat(testGenre.literary).isEqualTo(DEFAULT_LITERARY)
    }

    @Test
    @Transactional
    fun createGenreWithExistingId() {
        val databaseSizeBeforeCreate = genreRepository.findAll().size

        // Create the Genre with an existing ID
        genre.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restGenreMockMvc.perform(
            post("/api/management/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(genre))
        ).andExpect(status().isBadRequest)

        // Validate the Genre in the database
        val genreList = genreRepository.findAll()
        assertThat(genreList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = genreRepository.findAll().size
        // set the field null
        genre.name = null

        // Create the Genre, which fails.

        restGenreMockMvc.perform(
            post("/api/management/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(genre))
        ).andExpect(status().isBadRequest)

        val genreList = genreRepository.findAll()
        assertThat(genreList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllGenres() {
        // Initialize the database
        genreRepository.saveAndFlush(genre)

        // Get all the genreList
        restGenreMockMvc.perform(get("/api/genres?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(genre.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].literary").value(hasItem(DEFAULT_LITERARY)))
    }

    @Test
    @Transactional
    fun getGenre() {
        // Initialize the database
        genreRepository.saveAndFlush(genre)

        val id = genre.id
        assertNotNull(id)

        // Get the genre
        restGenreMockMvc.perform(get("/api/genres/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.literary").value(DEFAULT_LITERARY))
    }

    @Test
    @Transactional
    fun getNonExistingGenre() {
        // Get the genre
        restGenreMockMvc.perform(get("/api/genres/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateGenre() {
        // Initialize the database
        genreRepository.saveAndFlush(genre)

        val databaseSizeBeforeUpdate = genreRepository.findAll().size

        // Update the genre
        val id = genre.id
        assertNotNull(id)
        val updatedGenre = genreRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedGenre are not directly saved in db
        em.detach(updatedGenre)
        updatedGenre.name = UPDATED_NAME
        updatedGenre.literary = UPDATED_LITERARY

        restGenreMockMvc.perform(
            put("/api/management/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedGenre))
        ).andExpect(status().isOk)

        // Validate the Genre in the database
        val genreList = genreRepository.findAll()
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate)
        val testGenre = genreList[genreList.size - 1]
        assertThat(testGenre.name).isEqualTo(UPDATED_NAME)
        assertThat(testGenre.literary).isEqualTo(UPDATED_LITERARY)
    }

    @Test
    @Transactional
    fun updateNonExistingGenre() {
        val databaseSizeBeforeUpdate = genreRepository.findAll().size

        // Create the Genre

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenreMockMvc.perform(
            put("/api/management/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(genre))
        ).andExpect(status().isBadRequest)

        // Validate the Genre in the database
        val genreList = genreRepository.findAll()
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteGenre() {
        // Initialize the database
        genreRepository.saveAndFlush(genre)

        val databaseSizeBeforeDelete = genreRepository.findAll().size

        val id = genre.id
        assertNotNull(id)

        // Delete the genre
        restGenreMockMvc.perform(
            delete("/api/management/genres/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val genreList = genreRepository.findAll()
        assertThat(genreList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_LITERARY: Int = 0
        private const val UPDATED_LITERARY: Int = 1

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Genre {
            val genre = Genre(
                name = DEFAULT_NAME,
                literary = DEFAULT_LITERARY
            )

            return genre
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Genre {
            val genre = Genre(
                name = UPDATED_NAME,
                literary = UPDATED_LITERARY
            )

            return genre
        }
    }
}
