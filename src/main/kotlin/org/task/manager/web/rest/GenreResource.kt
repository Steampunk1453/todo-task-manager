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
import org.springframework.web.bind.annotation.RestController
import org.task.manager.domain.Genre
import org.task.manager.repository.GenreRepository
import org.task.manager.web.rest.errors.BadRequestAlertException

private const val ENTITY_NAME = "genre"
/**
 * REST controller for managing [org.task.manager.domain.Genre].
 */
@RestController
@Transactional
class GenreResource(
    private val genreRepository: GenreRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /genres` : Create a new genre.
     *
     * @param genre the genre to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new genre, or with status `400 (Bad Request)` if the genre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/management/genres")
    fun createGenre(@Valid @RequestBody genre: Genre): ResponseEntity<Genre> {
        log.debug("REST request to save Genre : {}", genre)
        if (genre.id != null) {
            throw BadRequestAlertException(
                "A new genre cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = genreRepository.save(genre)
        return ResponseEntity.created(URI("/management/genres/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /genres` : Updates an existing genre.
     *
     * @param genre the genre to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated genre,
     * or with status `400 (Bad Request)` if the genre is not valid,
     * or with status `500 (Internal Server Error)` if the genre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/management/genres")
    fun updateGenre(@Valid @RequestBody genre: Genre): ResponseEntity<Genre> {
        log.debug("REST request to update Genre : {}", genre)
        if (genre.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = genreRepository.save(genre)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     genre.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /genres` : get all the genres.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of genres in body.
     */
    @GetMapping("api/genres")
    fun getAllGenres(): MutableList<Genre> {
        log.debug("REST request to get all Genres")
        return genreRepository.findAll()
    }

    /**
     * `GET  /genres/:id` : get the "id" genre.
     *
     * @param id the id of the genre to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the genre, or with status `404 (Not Found)`.
     */
    @GetMapping("api/genres/{id}")
    fun getGenre(@PathVariable id: Long): ResponseEntity<Genre> {
        log.debug("REST request to get Genre : {}", id)
        val genre = genreRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(genre)
    }
    /**
     *  `DELETE  /genres/:id` : delete the "id" genre.
     *
     * @param id the id of the genre to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/management/genres/{id}")
    fun deleteGenre(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Genre : {}", id)

        genreRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
