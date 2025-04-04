package backend.academy.scrapper.client.link.stackoverflow.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class UnixTimestampDeserializer extends JsonDeserializer<OffsetDateTime> {

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
        long timestamp = p.getLongValue();
        return Instant.ofEpochSecond(timestamp).atOffset(ZoneOffset.UTC);
    }
}
