package org.task.manager.service
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.task.manager.domain.Book
import org.task.manager.repository.BookRepository

/**
 * Service Implementation for managing [Book].
 */
@Service
@Transactional
class BookService(
    private val bookRepository: BookRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a book.
     *
     * @param book the entity to save.
     * @return the persisted entity.
     */
    fun save(book: Book): Book {
        log.debug("Request to save Book : {}", book)
        return bookRepository.save(book)
    }

    /**
     * Get all the books.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Book> {
        log.debug("Request to get all Books")
        return bookRepository.findAll(pageable)
    }

    /**
     * Get one book by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Book> {
        log.debug("Request to get Book : {}", id)
        return bookRepository.findById(id)
    }

    /**
     * Delete the book by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Book : {}", id)

        bookRepository.deleteById(id)
    }
}
