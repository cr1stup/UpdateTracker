package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkUpdate;

public interface DispatcherUpdatesService {
    void dispatchUpdates(LinkUpdate linkUpdate);
}
