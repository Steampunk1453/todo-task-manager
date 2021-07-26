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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
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
import org.task.manager.domain.Book
import org.task.manager.repository.BookRepository
import org.task.manager.service.BookService
import org.task.manager.service.UserService
import org.task.manager.web.rest.errors.ExceptionTranslator

/**
 * Integration tests for the [BookResource] REST controller.
 *
 * @see BookResource
 */
@SpringBootTest(classes = [ToDoTaskManagerApp::class])
class BookResourceIT {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var bookService: BookService

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

    private lateinit var restBookMockMvc: MockMvc

    private lateinit var book: Book

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val bookResource = BookResource(bookService, userService)
        this.restBookMockMvc = MockMvcBuilders.standaloneSetup(bookResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        book = createEntity()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createBook() {
        val databaseSizeBeforeCreate = bookRepository.findAll().size

        // Create the Book
        restBookMockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(book))
        ).andExpect(status().isCreated)

        // Validate the Book in the database
        val bookList = bookRepository.findAll()
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1)
        val testBook = bookList[bookList.size - 1]
        assertThat(testBook.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testBook.author).isEqualTo(DEFAULT_AUTHOR)
        assertThat(testBook.genre).isEqualTo(DEFAULT_GENRE)
        assertThat(testBook.editorial).isEqualTo(DEFAULT_EDITORIAL)
        assertThat(testBook.bookshop).isEqualTo(DEFAULT_BOOKSHOP)
        assertThat(testBook.bookshopUrl).isEqualTo(DEFAULT_BOOKSHOP_URL)
        assertThat(testBook.startDate).isEqualTo(DEFAULT_START_DATE)
        assertThat(testBook.deadline).isEqualTo(DEFAULT_DEADLINE)
        assertThat(testBook.check).isEqualTo(DEFAULT_CHECK)
        assertThat(testBook.editorialUrl).isEqualTo(DEFAULT_EDITORIAL_URL)
    }

    @Test
    @Transactional
    fun createBookWithExistingId() {
        val databaseSizeBeforeCreate = bookRepository.findAll().size

        // Create the Book with an existing ID
        book.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookMockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(book))
        ).andExpect(status().isBadRequest)

        // Validate the Book in the database
        val bookList = bookRepository.findAll()
        assertThat(bookList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = bookRepository.findAll().size
        // set the field null
        book.title = null

        // Create the Book, which fails.

        restBookMockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(book))
        ).andExpect(status().isBadRequest)

        val bookList = bookRepository.findAll()
        assertThat(bookList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkStartDateIsRequired() {
        val databaseSizeBeforeTest = bookRepository.findAll().size
        // set the field null
        book.startDate = null

        // Create the Book, which fails.

        restBookMockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(book))
        ).andExpect(status().isBadRequest)

        val bookList = bookRepository.findAll()
        assertThat(bookList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkDeadlineIsRequired() {
        val databaseSizeBeforeTest = bookRepository.findAll().size
        // set the field null
        book.deadline = null

        // Create the Book, which fails.

        restBookMockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(book))
        ).andExpect(status().isBadRequest)

        val bookList = bookRepository.findAll()
        assertThat(bookList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllBooks() {
        // Initialize the database
        bookRepository.saveAndFlush(book)

        // Create security-aware mockMvc
        restBookMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()

        // Get all the bookList
        restBookMockMvc.perform(get("/api/books?sort=id,desc")
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.id?.toInt())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].genre").value(hasItem(DEFAULT_GENRE)))
            .andExpect(jsonPath("$.[*].editorial").value(hasItem(DEFAULT_EDITORIAL)))
            .andExpect(jsonPath("$.[*].bookshop").value(hasItem(DEFAULT_BOOKSHOP)))
            .andExpect(jsonPath("$.[*].bookshopUrl").value(hasItem(DEFAULT_BOOKSHOP_URL)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].deadline").value(hasItem(DEFAULT_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].check").value(hasItem(DEFAULT_CHECK)))
            .andExpect(jsonPath("$.[*].editorialUrl").value(hasItem(DEFAULT_EDITORIAL_URL)))
    }

    @Test
    @Transactional
    fun getBook() {
        // Initialize the database
        bookRepository.saveAndFlush(book)

        val id = book.id
        assertNotNull(id)

        // Get the book
        restBookMockMvc.perform(get("/api/books/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.genre").value(DEFAULT_GENRE))
            .andExpect(jsonPath("$.editorial").value(DEFAULT_EDITORIAL))
            .andExpect(jsonPath("$.bookshop").value(DEFAULT_BOOKSHOP))
            .andExpect(jsonPath("$.bookshopUrl").value(DEFAULT_BOOKSHOP_URL))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.deadline").value(DEFAULT_DEADLINE.toString()))
            .andExpect(jsonPath("$.check").value(DEFAULT_CHECK))
            .andExpect(jsonPath("$.editorialUrl").value(DEFAULT_EDITORIAL_URL))
    }

    @Test
    @Transactional
    fun getNonExistingBook() {
        // Get the book
        restBookMockMvc.perform(get("/api/books/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateBook() {
        // Initialize the database
        bookService.save(book)

        val databaseSizeBeforeUpdate = bookRepository.findAll().size

        // Update the book
        val id = book.id
        assertNotNull(id)
        val updatedBook = bookRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedBook are not directly saved in db
        em.detach(updatedBook)
        updatedBook.title = UPDATED_TITLE
        updatedBook.author = UPDATED_AUTHOR
        updatedBook.genre = UPDATED_GENRE
        updatedBook.editorial = UPDATED_EDITORIAL
        updatedBook.bookshop = UPDATED_BOOKSHOP
        updatedBook.bookshopUrl = UPDATED_BOOKSHOP_URL
        updatedBook.startDate = UPDATED_START_DATE
        updatedBook.deadline = UPDATED_DEADLINE
        updatedBook.check = UPDATED_CHECK
        updatedBook.editorialUrl = UPDATED_EDITORIAL_URL

        restBookMockMvc.perform(
            put("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedBook))
        ).andExpect(status().isOk)

        // Validate the Book in the database
        val bookList = bookRepository.findAll()
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate)
        val testBook = bookList[bookList.size - 1]
        assertThat(testBook.title).isEqualTo(UPDATED_TITLE)
        assertThat(testBook.author).isEqualTo(UPDATED_AUTHOR)
        assertThat(testBook.genre).isEqualTo(UPDATED_GENRE)
        assertThat(testBook.editorial).isEqualTo(UPDATED_EDITORIAL)
        assertThat(testBook.bookshop).isEqualTo(UPDATED_BOOKSHOP)
        assertThat(testBook.bookshopUrl).isEqualTo(UPDATED_BOOKSHOP_URL)
        assertThat(testBook.startDate).isEqualTo(UPDATED_START_DATE)
        assertThat(testBook.deadline).isEqualTo(UPDATED_DEADLINE)
        assertThat(testBook.check).isEqualTo(UPDATED_CHECK)
        assertThat(testBook.editorialUrl).isEqualTo(UPDATED_EDITORIAL_URL)
    }

    @Test
    @Transactional
    fun updateNonExistingBook() {
        val databaseSizeBeforeUpdate = bookRepository.findAll().size

        // Create the Book

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc.perform(
            put("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(book))
        ).andExpect(status().isBadRequest)

        // Validate the Book in the database
        val bookList = bookRepository.findAll()
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteBook() {
        // Initialize the database
        bookService.save(book)

        val databaseSizeBeforeDelete = bookRepository.findAll().size

        val id = book.id
        assertNotNull(id)

        // Delete the book
        restBookMockMvc.perform(
            delete("/api/books/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val bookList = bookRepository.findAll()
        assertThat(bookList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_AUTHOR = "AAAAAAAAAA"
        private const val UPDATED_AUTHOR = "BBBBBBBBBB"

        private const val DEFAULT_GENRE = "AAAAAAAAAA"
        private const val UPDATED_GENRE = "BBBBBBBBBB"

        private const val DEFAULT_EDITORIAL = "AAAAAAAAAA"
        private const val UPDATED_EDITORIAL = "BBBBBBBBBB"

        private const val DEFAULT_BOOKSHOP = "AAAAAAAAAA"
        private const val UPDATED_BOOKSHOP = "BBBBBBBBBB"

        private const val DEFAULT_BOOKSHOP_URL = "AAAAAAAAAA"
        private const val UPDATED_BOOKSHOP_URL = "BBBBBBBBBB"

        private val DEFAULT_START_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_START_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_DEADLINE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DEADLINE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_CHECK: Int = 1
        private const val UPDATED_CHECK: Int = 2

        private const val DEFAULT_EDITORIAL_URL = "AAAAAAAAAA"
        private const val UPDATED_EDITORIAL_URL = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Book {
            val book = Book(
                title = DEFAULT_TITLE,
                author = DEFAULT_AUTHOR,
                genre = DEFAULT_GENRE,
                editorial = DEFAULT_EDITORIAL,
                bookshop = DEFAULT_BOOKSHOP,
                bookshopUrl = DEFAULT_BOOKSHOP_URL,
                startDate = DEFAULT_START_DATE,
                deadline = DEFAULT_DEADLINE,
                check = DEFAULT_CHECK,
                editorialUrl = DEFAULT_EDITORIAL_URL
            )

            return book
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Book {
            val book = Book(
                title = UPDATED_TITLE,
                author = UPDATED_AUTHOR,
                genre = UPDATED_GENRE,
                editorial = UPDATED_EDITORIAL,
                bookshop = UPDATED_BOOKSHOP,
                bookshopUrl = UPDATED_BOOKSHOP_URL,
                startDate = UPDATED_START_DATE,
                deadline = UPDATED_DEADLINE,
                check = UPDATED_CHECK,
                editorialUrl = UPDATED_EDITORIAL_URL
            )

            return book
        }
    }
}
