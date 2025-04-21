package backend.academy.scrapper.service.update;

import backend.academy.scrapper.dto.LinkUpdate;

public interface ImmediateUpdateService {
    void sendUpdatesToUsers(LinkUpdate linkUpdate);
}
