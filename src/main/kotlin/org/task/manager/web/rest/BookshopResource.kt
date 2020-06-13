package org.task.manager.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.task.manager.domain.Bookshop
import org.task.manager.repository.BookshopRepository
import org.task.manager.web.rest.errors.BadRequestAlertException

private const val ENTITY_NAME = "bookshop"

/**
 * REST controller for managing [org.task.manager.domain.Bookshop].
 */
@RestController
@RequestMapping("/api")
@Transactional
class BookshopResource(
    private val bookshopRepository: BookshopRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /bookshops` : Create a new bookshop.
     *
     * @param bookshop the bookshop to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new bookshop, or with status `400 (Bad Request)` if the bookshop has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/management/bookshops")
    fun createBookshop(@Valid @RequestBody bookshop: Bookshop): ResponseEntity<Bookshop> {
        log.debug("REST request to save Bookshop : {}", bookshop)
        if (bookshop.id != null) {
            throw BadRequestAlertException(
                "A new bookshop cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = bookshopRepository.save(bookshop)
        return ResponseEntity.created(URI("/api/bookshops/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /bookshops` : Updates an existing bookshop.
     *
     * @param bookshop the bookshop to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated bookshop,
     * or with status `400 (Bad Request)` if the bookshop is not valid,
     * or with status `500 (Internal Server Error)` if the bookshop couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/management/bookshops")
    fun updateBookshop(@Valid @RequestBody bookshop: Bookshop): ResponseEntity<Bookshop> {
        log.debug("REST request to update Bookshop : {}", bookshop)
        if (bookshop.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = bookshopRepository.save(bookshop)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    bookshop.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /bookshops` : get all the bookshops.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of bookshops in body.
     */
    @GetMapping("/bookshops")
    fun getAllBookshops(): MutableList<Bookshop> {
        log.debug("REST request to get all Bookshops")
        return bookshopRepository.findAll()
    }

    /**
     * `GET  /bookshops/:id` : get the "id" bookshop.
     *
     * @param id the id of the bookshop to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the bookshop, or with status `404 (Not Found)`.
     */
    @GetMapping("/bookshops/{id}")
    fun getBookshop(@PathVariable id: Long): ResponseEntity<Bookshop> {
        log.debug("REST request to get Bookshop : {}", id)
        val bookshop = bookshopRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(bookshop)
    }

    /**
     *  `DELETE  /bookshops/:id` : delete the "id" bookshop.
     *
     * @param id the id of the bookshop to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/management/bookshops/{id}")
    fun deleteBookshop(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Bookshop : {}", id)

        bookshopRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
