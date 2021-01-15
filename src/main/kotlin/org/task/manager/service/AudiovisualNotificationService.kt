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
     * This is scheduled to get fired everyday, at random time between 8:03 & 20:03 h in the system.
     */
    @Scheduled(cron = "0 03 #{new java.util.Random().nextInt(20 - 8) + 8}  * * ?")
    override fun send() {
        sendAudiovisualsEndingToday()
    }

    private fun sendAudiovisualsEndingToday() {
        val audiovisualsEndingToday = audiovisualRepository.findAllByDeadlineBetweenAndCheck(Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS), IS_CHECK)
        sendToMail(audiovisualsEndingToday)
    }

    private fun sendToMail(audiovisuals: List<Audiovisual>) {
        for (audiovisual in audiovisuals) {
            mailService.sendAudiovisualNotificationMail(audiovisual.toDto(false))
        }
    }
}
