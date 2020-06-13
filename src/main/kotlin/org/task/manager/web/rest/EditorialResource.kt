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
import org.task.manager.domain.Editorial
import org.task.manager.repository.EditorialRepository
import org.task.manager.web.rest.errors.BadRequestAlertException

private const val ENTITY_NAME = "editorial"

/**
 * REST controller for managing [org.task.manager.domain.Editorial].
 */
@RestController
@RequestMapping("/api")
@Transactional
class EditorialResource(
    private val editorialRepository: EditorialRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /editorials` : Create a new editorial.
     *
     * @param editorial the editorial to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new editorial, or with status `400 (Bad Request)` if the editorial has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/management/editorials")
    fun createEditorial(@Valid @RequestBody editorial: Editorial): ResponseEntity<Editorial> {
        log.debug("REST request to save Editorial : {}", editorial)
        if (editorial.id != null) {
            throw BadRequestAlertException(
                "A new editorial cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = editorialRepository.save(editorial)
        return ResponseEntity.created(URI("/api/editorials/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /editorials` : Updates an existing editorial.
     *
     * @param editorial the editorial to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated editorial,
     * or with status `400 (Bad Request)` if the editorial is not valid,
     * or with status `500 (Internal Server Error)` if the editorial couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/management/editorials")
    fun updateEditorial(@Valid @RequestBody editorial: Editorial): ResponseEntity<Editorial> {
        log.debug("REST request to update Editorial : {}", editorial)
        if (editorial.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = editorialRepository.save(editorial)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    editorial.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /editorials` : get all the editorials.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of editorials in body.
     */
    @GetMapping("/editorials")
    fun getAllEditorials(): MutableList<Editorial> {
        log.debug("REST request to get all Editorials")
        return editorialRepository.findAll()
    }

    /**
     * `GET  /editorials/:id` : get the "id" editorial.
     *
     * @param id the id of the editorial to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the editorial, or with status `404 (Not Found)`.
     */
    @GetMapping("/editorials/{id}")
    fun getEditorial(@PathVariable id: Long): ResponseEntity<Editorial> {
        log.debug("REST request to get Editorial : {}", id)
        val editorial = editorialRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(editorial)
    }

    /**
     *  `DELETE  /editorials/:id` : delete the "id" editorial.
     *
     * @param id the id of the editorial to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/management/editorials/{id}")
    fun deleteEditorial(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Editorial : {}", id)

        editorialRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
