package org.task.manager.service
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.task.manager.domain.Audiovisual
import org.task.manager.repository.AudiovisualRepository

/**
 * Service Implementation for managing [Audiovisual].
 */
@Service
@Transactional
class AudiovisualService(
    private val audiovisualRepository: AudiovisualRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a audiovisual.
     *
     * @param audiovisual the entity to save.
     * @return the persisted entity.
     */
    fun save(audiovisual: Audiovisual): Audiovisual {
        log.debug("Request to save Audiovisual : {}", audiovisual)
        return audiovisualRepository.save(audiovisual)
    }

    /**
     * Get all the audiovisuals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Audiovisual> {
        log.debug("Request to get all Audiovisuals")
        return audiovisualRepository.findAll(pageable)
    }

    @Transactional(readOnly = true)
    fun findAllByUser(pageable: Pageable): Page<Audiovisual> {
        log.debug("Request to get all Audiovisuals")
        return audiovisualRepository.findByUserIsCurrentUser(pageable)
    }

    /**
     * Get one audiovisual by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Audiovisual> {
        log.debug("Request to get Audiovisual : {}", id)
        return audiovisualRepository.findById(id)
    }

    /**
     * Delete the audiovisual by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Audiovisual : {}", id)
        audiovisualRepository.deleteById(id)
    }
}
