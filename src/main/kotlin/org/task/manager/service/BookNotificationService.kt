package org.task.manager.service

import java.time.Instant
import java.time.temporal.ChronoUnit
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.task.manager.domain.Book
import org.task.manager.repository.BookRepository
import org.task.manager.service.dto.toDto

private const val IS_CHECK = 0

@Service
@Transactional
class BookNotificationService(
    private val bookRepository: BookRepository,
    private val mailService: MailService
) : NotificationService {

    /**
     * Send notification when is one day left to the deadline
     *
     * This is scheduled to get fired everyday, at 08:10 (am) in the system.
     */
    @Scheduled(cron = "0 25 18 * * ?")
    override fun send() {
        sendAudiovisualsStartingToday()
        sendAudiovisualsEndingToday()
    }

    private fun sendAudiovisualsStartingToday() {
        val booksStartingToday = bookRepository.findAllByStartDateBetweenAndCheck(Instant.now(),
            Instant.now().plus(1, ChronoUnit.DAYS), IS_CHECK)
        val isStartDate = true
        sendToMail(booksStartingToday, isStartDate)
    }

    private fun sendAudiovisualsEndingToday() {
        val booksEndingToday = bookRepository.findAllByDeadlineBetweenAndCheck(Instant.now(),
            Instant.now().plus(1, ChronoUnit.DAYS), IS_CHECK)
        val isStartDate = false
        sendToMail(booksEndingToday, isStartDate)
    }

    private fun sendToMail(books: List<Book>, isStartDate: Boolean) {
        for (book in books) {
            mailService.sendBookNotificationMail(book.toDto(isStartDate))
        }
    }
}
