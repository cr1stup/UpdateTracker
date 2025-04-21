package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkUpdate;

public interface ImmediateUpdateService {
    void sendUpdatesToUsers(LinkUpdate linkUpdate);
}
