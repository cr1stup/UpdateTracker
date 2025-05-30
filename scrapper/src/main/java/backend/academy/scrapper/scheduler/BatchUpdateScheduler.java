package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.service.chat.ChatModeService;
import backend.academy.scrapper.service.model.Mode;
import backend.academy.scrapper.service.update.BatchUpdateService;
import java.time.LocalTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchUpdateScheduler {

    private final ChatModeService chatModeService;
    private final BatchUpdateService batchUpdateService;

    @Scheduled(cron = "0 * * * * *")
    public void checkDailyBatches() {
        LocalTime now = LocalTime.now(ZoneId.systemDefault()).withSecond(0).withNano(0);

        for (Long chatId : chatModeService.findAllChatIdsByMode(Mode.DAILY.modeName())) {
            LocalTime userBatchTime = chatModeService.findTimeByChatId(chatId);

            if (userBatchTime.equals(now)) {
                batchUpdateService.sendBatchUpdateToUser(chatId);
            }
        }
    }
}
