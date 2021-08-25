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
     * This is scheduled to get fired every day, at random time between 8:17 & 20:17 h in the system
     */
    @Scheduled(cron = "0 17 #{new java.util.Random().nextInt(20 - 8) + 8}  * * ?")
    override fun send() {
        sendAudiovisualsEndingToday()
    }

    private fun sendAudiovisualsEndingToday() {
        val booksEndingToday = bookRepository.findAllByDeadlineBetweenAndCheck(Instant.now(),
            Instant.now().plus(1, ChronoUnit.DAYS), IS_CHECK)
        sendToMail(booksEndingToday)
    }

    private fun sendToMail(books: List<Book>) {
        for (book in books) {
            mailService.sendBookNotificationMail(book.toDto(false))
        }
    }
}
