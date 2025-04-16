package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkUpdate;

public interface UpdateService {
    void sendUpdatesToUsers(LinkUpdate linkUpdate);
}
