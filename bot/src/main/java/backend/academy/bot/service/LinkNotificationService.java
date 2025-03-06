package backend.academy.bot.service;

import backend.academy.bot.dto.LinkUpdate;

public interface LinkNotificationService {

    void notifyLinkUpdate(LinkUpdate link);

}
