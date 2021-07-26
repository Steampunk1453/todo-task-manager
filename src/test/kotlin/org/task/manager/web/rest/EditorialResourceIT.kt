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
import org.task.manager.domain.Editorial
import org.task.manager.repository.EditorialRepository
import org.task.manager.web.rest.errors.ExceptionTranslator

/**
 * Integration tests for the [EditorialResource] REST controller.
 *
 * @see EditorialResource
 */
@SpringBootTest(classes = [ToDoTaskManagerApp::class])
class EditorialResourceIT {

    @Autowired
    private lateinit var editorialRepository: EditorialRepository

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

    private lateinit var restEditorialMockMvc: MockMvc

    private lateinit var editorial: Editorial

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val editorialResource = EditorialResource(editorialRepository)
        this.restEditorialMockMvc = MockMvcBuilders.standaloneSetup(editorialResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        editorial = createEntity()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createEditorial() {
        val databaseSizeBeforeCreate = editorialRepository.findAll().size

        // Create the Editorial
        restEditorialMockMvc.perform(
            post("/api/management/editorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(editorial))
        ).andExpect(status().isCreated)

        // Validate the Editorial in the database
        val editorialList = editorialRepository.findAll()
        assertThat(editorialList).hasSize(databaseSizeBeforeCreate + 1)
        val testEditorial = editorialList[editorialList.size - 1]
        assertThat(testEditorial.name).isEqualTo(DEFAULT_NAME)
        assertThat(testEditorial.url).isEqualTo(DEFAULT_URL)
    }

    @Test
    @Transactional
    fun createEditorialWithExistingId() {
        val databaseSizeBeforeCreate = editorialRepository.findAll().size

        // Create the Editorial with an existing ID
        editorial.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restEditorialMockMvc.perform(
            post("/api/management/editorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(editorial))
        ).andExpect(status().isBadRequest)

        // Validate the Editorial in the database
        val editorialList = editorialRepository.findAll()
        assertThat(editorialList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkUrlIsRequired() {
        val databaseSizeBeforeTest = editorialRepository.findAll().size
        // set the field null
        editorial.url = null

        // Create the Editorial, which fails.

        restEditorialMockMvc.perform(
            post("/api/management/editorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(editorial))
        ).andExpect(status().isBadRequest)

        val editorialList = editorialRepository.findAll()
        assertThat(editorialList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllEditorials() {
        // Initialize the database
        editorialRepository.saveAndFlush(editorial)

        // Get all the editorialList
        restEditorialMockMvc.perform(get("/api/editorials?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(editorial.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
    }

    @Test
    @Transactional
    fun getEditorial() {
        // Initialize the database
        editorialRepository.saveAndFlush(editorial)

        val id = editorial.id
        assertNotNull(id)

        // Get the editorial
        restEditorialMockMvc.perform(get("/api/editorials/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
    }

    @Test
    @Transactional
    fun getNonExistingEditorial() {
        // Get the editorial
        restEditorialMockMvc.perform(get("/api/editorials/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateEditorial() {
        // Initialize the database
        editorialRepository.saveAndFlush(editorial)

        val databaseSizeBeforeUpdate = editorialRepository.findAll().size

        // Update the editorial
        val id = editorial.id
        assertNotNull(id)
        val updatedEditorial = editorialRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedEditorial are not directly saved in db
        em.detach(updatedEditorial)
        updatedEditorial.name = UPDATED_NAME
        updatedEditorial.url = UPDATED_URL

        restEditorialMockMvc.perform(
            put("/api/management/editorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedEditorial))
        ).andExpect(status().isOk)

        // Validate the Editorial in the database
        val editorialList = editorialRepository.findAll()
        assertThat(editorialList).hasSize(databaseSizeBeforeUpdate)
        val testEditorial = editorialList[editorialList.size - 1]
        assertThat(testEditorial.name).isEqualTo(UPDATED_NAME)
        assertThat(testEditorial.url).isEqualTo(UPDATED_URL)
    }

    @Test
    @Transactional
    fun updateNonExistingEditorial() {
        val databaseSizeBeforeUpdate = editorialRepository.findAll().size

        // Create the Editorial

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEditorialMockMvc.perform(
            put("/api/management/editorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(editorial))
        ).andExpect(status().isBadRequest)

        // Validate the Editorial in the database
        val editorialList = editorialRepository.findAll()
        assertThat(editorialList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteEditorial() {
        // Initialize the database
        editorialRepository.saveAndFlush(editorial)

        val databaseSizeBeforeDelete = editorialRepository.findAll().size

        val id = editorial.id
        assertNotNull(id)

        // Delete the editorial
        restEditorialMockMvc.perform(
            delete("/api/management/editorials/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val editorialList = editorialRepository.findAll()
        assertThat(editorialList).hasSize(databaseSizeBeforeDelete - 1)
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
        fun createEntity(): Editorial {
            val editorial = Editorial(
                name = DEFAULT_NAME,
                url = DEFAULT_URL
            )

            return editorial
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Editorial {
            val editorial = Editorial(
                name = UPDATED_NAME,
                url = UPDATED_URL
            )

            return editorial
        }
    }
}
