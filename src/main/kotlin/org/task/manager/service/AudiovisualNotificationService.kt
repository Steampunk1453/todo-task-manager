package org.task.manager.service

import java.time.Instant
import java.time.temporal.ChronoUnit
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.task.manager.domain.Audiovisual
import org.task.manager.repository.AudiovisualRepository
import org.task.manager.service.dto.toDto

private const val IS_CHECK = 0

@Service
@Transactional
class AudiovisualNotificationService(
    private val audiovisualRepository: AudiovisualRepository,
    private val mailService: MailService
) : NotificationService {

    /**
     * Send notification when is one day left to the deadline
     *
     * This is scheduled to get fired everyday, at 08:00 (am) in the system.
     */
    @Scheduled(cron = "0 00 08 * * ?")
    override fun send() {
        sendAudiovisualsStartingToday()
        sendAudiovisualsEndingToday()
    }

    private fun sendAudiovisualsStartingToday() {
        val audiovisualsStartingToday = audiovisualRepository.findAllByStartDateBetweenAndCheck(Instant.now(),
            Instant.now().plus(1, ChronoUnit.DAYS), IS_CHECK)
        val isStartDate = true
        sendToMail(audiovisualsStartingToday, isStartDate)
    }

    private fun sendAudiovisualsEndingToday() {
        val audiovisualsEndingToday = audiovisualRepository.findAllByDeadlineBetweenAndCheck(Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS), IS_CHECK)
        val isStartDate = false
        sendToMail(audiovisualsEndingToday, isStartDate)
    }

    private fun sendToMail(audiovisuals: List<Audiovisual>, isStartDate: Boolean) {
        for (audiovisual in audiovisuals) {
            mailService.sendAudiovisualNotificationMail(audiovisual.toDto(isStartDate))
        }
    }
}
