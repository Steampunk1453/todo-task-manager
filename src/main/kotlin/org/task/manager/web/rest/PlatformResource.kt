package org.task.manager.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
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
import org.task.manager.domain.Platform
import org.task.manager.repository.PlatformRepository
import org.task.manager.web.rest.errors.BadRequestAlertException
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid

private const val ENTITY_NAME = "platform"
/**
 * REST controller for managing [org.task.manager.domain.Platform].
 */
@RestController
@RequestMapping("/api")
@Transactional
class PlatformResource(
    private val platformRepository: PlatformRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /platforms` : Create a new platform.
     *
     * @param platform the platform to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new platform, or with status `400 (Bad Request)` if the platform has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/management/platforms")
    fun createPlatform(@Valid @RequestBody platform: Platform): ResponseEntity<Platform> {
        log.debug("REST request to save Platform : {}", platform)
        if (platform.id != null) {
            throw BadRequestAlertException(
                "A new platform cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = platformRepository.save(platform)
        return ResponseEntity.created(URI("/management/platforms/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /platforms` : Updates an existing platform.
     *
     * @param platform the platform to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated platform,
     * or with status `400 (Bad Request)` if the platform is not valid,
     * or with status `500 (Internal Server Error)` if the platform couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/management/platforms")
    fun updatePlatform(@Valid @RequestBody platform: Platform): ResponseEntity<Platform> {
        log.debug("REST request to update Platform : {}", platform)
        if (platform.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = platformRepository.save(platform)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    platform.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /platforms` : get all the platforms.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of platforms in body.
     */
    @GetMapping("/platforms")
    fun getAllPlatforms(): MutableList<Platform> {
        log.debug("REST request to get all Platforms")
        return platformRepository.findAll()
    }

    /**
     * `GET  /platforms/:id` : get the "id" platform.
     *
     * @param id the id of the platform to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the platform, or with status `404 (Not Found)`.
     */
    @GetMapping("/platforms/{id}")
    fun getPlatform(@PathVariable id: Long): ResponseEntity<Platform> {
        log.debug("REST request to get Platform : {}", id)
        val platform = platformRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(platform)
    }
    /**
     *  `DELETE  /platforms/:id` : delete the "id" platform.
     *
     * @param id the id of the platform to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/management/platforms/{id}")
    fun deletePlatform(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Platform : {}", id)

        platformRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
