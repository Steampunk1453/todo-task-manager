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
import org.task.manager.domain.Audiovisual
import org.task.manager.security.ADMIN
import org.task.manager.security.getCurrentUserLogin
import org.task.manager.security.isCurrentUserInRole
import org.task.manager.service.AudiovisualService
import org.task.manager.service.UserService
import org.task.manager.web.rest.errors.BadRequestAlertException

private const val ENTITY_NAME = "audiovisual"

/**
 * REST controller for managing [org.task.manager.domain.Audiovisual].
 */
@RestController
@RequestMapping("/api")
class AudiovisualResource(
    private val audiovisualService: AudiovisualService,
    private val userService: UserService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /audiovisuals` : Create a new audiovisual.
     *
     * @param audiovisual the audiovisual to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new audiovisual, or with status `400 (Bad Request)` if the audiovisual has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/audiovisuals")
    fun createAudiovisual(@Valid @RequestBody audiovisual: Audiovisual): ResponseEntity<Audiovisual> {
        log.debug("REST request to save Audiovisual : {}", audiovisual)
        if (audiovisual.id != null) {
            throw BadRequestAlertException(
                "A new audiovisual cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        getCurrentUserLogin()
            .flatMap(userService::getUserWithAuthoritiesByLogin)
            .ifPresent { user -> audiovisual.user = user }
        val result = audiovisualService.save(audiovisual)

        return ResponseEntity.created(URI("/api/audiovisuals/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /audiovisuals` : Updates an existing audiovisual.
     *
     * @param audiovisual the audiovisual to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated audiovisual,
     * or with status `400 (Bad Request)` if the audiovisual is not valid,
     * or with status `500 (Internal Server Error)` if the audiovisual couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/audiovisuals")
    fun updateAudiovisual(@Valid @RequestBody audiovisual: Audiovisual): ResponseEntity<Audiovisual> {
        log.debug("REST request to update Audiovisual : {}", audiovisual)
        if (audiovisual.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = audiovisualService.save(audiovisual)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    audiovisual.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /audiovisuals` : get all the audiovisuals.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of audiovisuals in body.
     */
    @GetMapping("/audiovisuals")
    fun getAllAudiovisuals(pageable: Pageable): ResponseEntity<MutableList<Audiovisual>> {
        log.debug("REST request to get a page of Audiovisuals")
        val page =
            if (isCurrentUserInRole(ADMIN)) audiovisualService.findAll(pageable)
            else audiovisualService.findAllByUser(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /audiovisuals/:id` : get the "id" audiovisual.
     *
     * @param id the id of the audiovisual to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the audiovisual, or with status `404 (Not Found)`.
     */
    @GetMapping("/audiovisuals/{id}")
    fun getAudiovisual(@PathVariable id: Long): ResponseEntity<Audiovisual> {
        log.debug("REST request to get Audiovisual : {}", id)
        val audiovisual = audiovisualService.findOne(id)
        return ResponseUtil.wrapOrNotFound(audiovisual)
    }

    /**
     *  `DELETE  /audiovisuals/:id` : delete the "id" audiovisual.
     *
     * @param id the id of the audiovisual to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/audiovisuals/{id}")
    fun deleteAudiovisual(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Audiovisual : {}", id)
        audiovisualService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
