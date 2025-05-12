package backend.academy.scrapper.service.update;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.LinkUpdate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FallbackUpdateService implements ImmediateUpdateService {

    private final List<ImmediateUpdateService> allTransports;
    private final ScrapperConfig config;
    private Map<String, ImmediateUpdateService> transportMap;

    @PostConstruct
    public void init() {
        transportMap = allTransports.stream()
            .collect(Collectors.toMap(
                ImmediateUpdateService::transportName,
                Function.identity()
            ));
    }

    @Override
    public void sendUpdatesToUsers(LinkUpdate linkUpdate) {
        List<String> preferredOrder = config.transportOrder();

        for (String transport : preferredOrder) {
            ImmediateUpdateService service = transportMap.get(transport);
            if (service == null) {
                continue;
            }

            try {
                service.sendUpdatesToUsers(linkUpdate);
                return;
            } catch (Exception e) {
                log.error("Error while sending updates to users for {}", transport);
            }
        }

        throw new RuntimeException("All transports failed");
    }

    @Override
    public String transportName() {
        return "fallback";
    }
}
