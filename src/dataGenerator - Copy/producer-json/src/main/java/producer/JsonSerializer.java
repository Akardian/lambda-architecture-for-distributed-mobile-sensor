package producer;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerializer implements Serializer<Object> {
    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(final Map<String, ?> config, final boolean isKey) {
        // Nothing to Configure
    }

    @Override
    public byte[] serialize(String topic, Object data) { // Overwrite default Serialzer methode
        if (data == null) { // Check for data
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (final JsonProcessingException e) {
            throw new SerializationException("Error serializing JSON message", e);
        } catch (IOException e) {
            throw new SerializationException("Error IOExceotion", e);
        }
    }

    @Override
    public void close() {
        //Nothing to do
    }    
}
