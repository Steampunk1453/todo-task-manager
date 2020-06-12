package org.task.manager.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.task.manager.domain.Book
import org.task.manager.security.ADMIN
import org.task.manager.security.getCurrentUserLogin
import org.task.manager.security.isCurrentUserInRole
import org.task.manager.service.BookService
import org.task.manager.service.UserService
import org.task.manager.web.rest.errors.BadRequestAlertException

private const val ENTITY_NAME = "book"
/**
 * REST controller for managing [org.task.manager.domain.Book].
 */
@RestController
@RequestMapping("/api")
class BookResource(
    private val bookService: BookService,
    private val userService: UserService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /books` : Create a new book.
     *
     * @param book the book to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new book, or with status `400 (Bad Request)` if the book has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/books")
    fun createBook(@Valid @RequestBody book: Book): ResponseEntity<Book> {
        log.debug("REST request to save Book : {}", book)
        if (book.id != null) {
            throw BadRequestAlertException(
                "A new book cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        getCurrentUserLogin()
            .flatMap(userService::getUserWithAuthoritiesByLogin)
            .ifPresent { user -> book.user = user }
        val result = bookService.save(book)

        return ResponseEntity.created(URI("/api/books/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /books` : Updates an existing book.
     *
     * @param book the book to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated book,
     * or with status `400 (Bad Request)` if the book is not valid,
     * or with status `500 (Internal Server Error)` if the book couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/books")
    fun updateBook(@Valid @RequestBody book: Book): ResponseEntity<Book> {
        log.debug("REST request to update Book : {}", book)
        if (book.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = bookService.save(book)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     book.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /books` : get all the books.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of books in body.
     */
    @GetMapping("/books")
    fun getAllBooks(
        pageable: Pageable
    ): ResponseEntity<MutableList<Book>> {
        log.debug("REST request to get a page of Books")
        val page =
            if (isCurrentUserInRole(ADMIN)) bookService.findAll(pageable)
            else bookService.findAllByUser(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /books/:id` : get the "id" book.
     *
     * @param id the id of the book to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the book, or with status `404 (Not Found)`.
     */
    @GetMapping("/books/{id}")
    fun getBook(@PathVariable id: Long): ResponseEntity<Book> {
        log.debug("REST request to get Book : {}", id)
        val book = bookService.findOne(id)
        return ResponseUtil.wrapOrNotFound(book)
    }
    /**
     *  `DELETE  /books/:id` : delete the "id" book.
     *
     * @param id the id of the book to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/books/{id}")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Book : {}", id)
        bookService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
