package backend.academy.scrapper.dto;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record EventInformation(String message, String title, String user, OffsetDateTime createdAt, String body) {

    public String getFormattedInformation() {
        StringBuilder metaInformation = new StringBuilder();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());
        String formattedTime = createdAt() != null ? formatter.format(createdAt) : null;

        appendIfNotNull(metaInformation, "", message);
        appendIfNotNull(metaInformation, "  • User: ", user);
        appendIfNotNull(metaInformation, "  • Title: ", title);
        appendIfNotNull(metaInformation, "  • Created at: ", formattedTime);
        appendIfNotNull(metaInformation, "  • Body: ", body);

        return metaInformation.toString().formatted();
    }

    private void appendIfNotNull(StringBuilder sb, String prefix, String value) {
        if (value != null) {
            sb.append(prefix).append(value).append("%n");
        }
    }
}
