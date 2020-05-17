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
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.task.manager.domain.Title
import org.task.manager.repository.TitleRepository
import org.task.manager.web.rest.errors.BadRequestAlertException

private const val ENTITY_NAME = "title"
/**
 * REST controller for managing [org.task.manager.domain.Title].
 */
@RestController
@Transactional
class TitleResource(
    private val titleRepository: TitleRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /titles` : Create a new title.
     *
     * @param title the title to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new title, or with status `400 (Bad Request)` if the title has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/management/titles")
    fun createTitle(@Valid @RequestBody title: Title): ResponseEntity<Title> {
        log.debug("REST request to save Title : {}", title)
        if (title.id != null) {
            throw BadRequestAlertException(
                "A new title cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = titleRepository.save(title)
        return ResponseEntity.created(URI("/management/titles/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /titles` : Updates an existing title.
     *
     * @param title the title to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated title,
     * or with status `400 (Bad Request)` if the title is not valid,
     * or with status `500 (Internal Server Error)` if the title couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/management/titles")
    fun updateTitle(@Valid @RequestBody title: Title): ResponseEntity<Title> {
        log.debug("REST request to update Title : {}", title)
        if (title.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = titleRepository.save(title)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     title.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /titles` : get all the titles.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of titles in body.
     */
    @GetMapping("api/titles")
    fun getAllTitles(
        pageable: Pageable
    ): ResponseEntity<MutableList<Title>> {
        log.debug("REST request to get a page of Titles")
        val page = titleRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /titles/:id` : get the "id" title.
     *
     * @param id the id of the title to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the title, or with status `404 (Not Found)`.
     */
    @GetMapping("api/titles/{id}")
    fun getTitle(@PathVariable id: Long): ResponseEntity<Title> {
        log.debug("REST request to get Title : {}", id)
        val title = titleRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(title)
    }
    /**
     *  `DELETE  /titles/:id` : delete the "id" title.
     *
     * @param id the id of the title to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/management/titles/{id}")
    fun deleteTitle(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Title : {}", id)

        titleRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
