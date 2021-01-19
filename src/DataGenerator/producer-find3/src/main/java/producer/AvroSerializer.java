package producer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class AvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {
    //T is an Avro object witch extends SpecificRecordBase

    public AvroSerializer() {
    }

    @Override
    public void configure(final Map<String, ?> config, final boolean isKey) {
        // Nothing to Configure
    }

    @Override
    public byte[] serialize(String topic, T data) { // Overwrite default Serialzer methode
        try {
            byte[] result = null;
      
            if (data != null) {      
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); //Byte Array for the encoder
                BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);

                DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(data.getSchema());
                datumWriter.write(data, binaryEncoder);
      
                binaryEncoder.flush();
                byteArrayOutputStream.close();
      
                result = byteArrayOutputStream.toByteArray();
            }
            return result;
        } catch (IOException ex) {
            throw new SerializationException(
                "Can't serialize data='" + data + "' for topic='" + topic + "'", ex);
        }
    }

    @Override
    public void close() {
        //Nothing to do
    }    
}
