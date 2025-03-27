package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.dto.OptionalAnswer;

public interface UpdateService {
    OptionalAnswer<Void> sendUpdatesToUsers(LinkUpdate linkUpdate);
}
