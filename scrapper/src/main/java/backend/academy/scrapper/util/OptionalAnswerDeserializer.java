package backend.academy.scrapper.util;

import backend.academy.scrapper.dto.ApiErrorResponse;
import backend.academy.scrapper.dto.OptionalAnswer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;

public class OptionalAnswerDeserializer extends JsonDeserializer<OptionalAnswer<?>> implements ContextualDeserializer {
    private JavaType valueType;

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        OptionalAnswerDeserializer deserializer = new OptionalAnswerDeserializer();
        deserializer.valueType = context.getContextualType().containedType(0);
        return deserializer;
    }

    @Override
    public OptionalAnswer<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode node = mapper.readTree(parser);

        if (node.has("exceptionName")) {
            return OptionalAnswer.error(mapper.treeToValue(node, ApiErrorResponse.class));
        }

        return valueType.hasRawClass(Void.class)
                ? OptionalAnswer.of(null)
                : OptionalAnswer.of(mapper.treeToValue(node, valueType.getRawClass()));
    }
}
