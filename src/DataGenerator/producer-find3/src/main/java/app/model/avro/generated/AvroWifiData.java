/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package app.model.avro.generated;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class AvroWifiData extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -408919393993364807L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroWifiData\",\"namespace\":\"app.model.avro.generated\",\"fields\":[{\"name\":\"wifiData\",\"type\":{\"type\":\"map\",\"values\":\"int\",\"avro.java.string\":\"String\"},\"default\":{}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<AvroWifiData> ENCODER =
      new BinaryMessageEncoder<AvroWifiData>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<AvroWifiData> DECODER =
      new BinaryMessageDecoder<AvroWifiData>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<AvroWifiData> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<AvroWifiData> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<AvroWifiData> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<AvroWifiData>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this AvroWifiData to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a AvroWifiData from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a AvroWifiData instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static AvroWifiData fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.util.Map<java.lang.String,java.lang.Integer> wifiData;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AvroWifiData() {}

  /**
   * All-args constructor.
   * @param wifiData The new value for wifiData
   */
  public AvroWifiData(java.util.Map<java.lang.String,java.lang.Integer> wifiData) {
    this.wifiData = wifiData;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return wifiData;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: wifiData = (java.util.Map<java.lang.String,java.lang.Integer>)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'wifiData' field.
   * @return The value of the 'wifiData' field.
   */
  public java.util.Map<java.lang.String,java.lang.Integer> getWifiData() {
    return wifiData;
  }


  /**
   * Sets the value of the 'wifiData' field.
   * @param value the value to set.
   */
  public void setWifiData(java.util.Map<java.lang.String,java.lang.Integer> value) {
    this.wifiData = value;
  }

  /**
   * Creates a new AvroWifiData RecordBuilder.
   * @return A new AvroWifiData RecordBuilder
   */
  public static app.model.avro.generated.AvroWifiData.Builder newBuilder() {
    return new app.model.avro.generated.AvroWifiData.Builder();
  }

  /**
   * Creates a new AvroWifiData RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AvroWifiData RecordBuilder
   */
  public static app.model.avro.generated.AvroWifiData.Builder newBuilder(app.model.avro.generated.AvroWifiData.Builder other) {
    if (other == null) {
      return new app.model.avro.generated.AvroWifiData.Builder();
    } else {
      return new app.model.avro.generated.AvroWifiData.Builder(other);
    }
  }

  /**
   * Creates a new AvroWifiData RecordBuilder by copying an existing AvroWifiData instance.
   * @param other The existing instance to copy.
   * @return A new AvroWifiData RecordBuilder
   */
  public static app.model.avro.generated.AvroWifiData.Builder newBuilder(app.model.avro.generated.AvroWifiData other) {
    if (other == null) {
      return new app.model.avro.generated.AvroWifiData.Builder();
    } else {
      return new app.model.avro.generated.AvroWifiData.Builder(other);
    }
  }

  /**
   * RecordBuilder for AvroWifiData instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AvroWifiData>
    implements org.apache.avro.data.RecordBuilder<AvroWifiData> {

    private java.util.Map<java.lang.String,java.lang.Integer> wifiData;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(app.model.avro.generated.AvroWifiData.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.wifiData)) {
        this.wifiData = data().deepCopy(fields()[0].schema(), other.wifiData);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
    }

    /**
     * Creates a Builder by copying an existing AvroWifiData instance
     * @param other The existing instance to copy.
     */
    private Builder(app.model.avro.generated.AvroWifiData other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.wifiData)) {
        this.wifiData = data().deepCopy(fields()[0].schema(), other.wifiData);
        fieldSetFlags()[0] = true;
      }
    }

    /**
      * Gets the value of the 'wifiData' field.
      * @return The value.
      */
    public java.util.Map<java.lang.String,java.lang.Integer> getWifiData() {
      return wifiData;
    }


    /**
      * Sets the value of the 'wifiData' field.
      * @param value The value of 'wifiData'.
      * @return This builder.
      */
    public app.model.avro.generated.AvroWifiData.Builder setWifiData(java.util.Map<java.lang.String,java.lang.Integer> value) {
      validate(fields()[0], value);
      this.wifiData = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'wifiData' field has been set.
      * @return True if the 'wifiData' field has been set, false otherwise.
      */
    public boolean hasWifiData() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'wifiData' field.
      * @return This builder.
      */
    public app.model.avro.generated.AvroWifiData.Builder clearWifiData() {
      wifiData = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AvroWifiData build() {
      try {
        AvroWifiData record = new AvroWifiData();
        record.wifiData = fieldSetFlags()[0] ? this.wifiData : (java.util.Map<java.lang.String,java.lang.Integer>) defaultValue(fields()[0]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<AvroWifiData>
    WRITER$ = (org.apache.avro.io.DatumWriter<AvroWifiData>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<AvroWifiData>
    READER$ = (org.apache.avro.io.DatumReader<AvroWifiData>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    long size0 = this.wifiData.size();
    out.writeMapStart();
    out.setItemCount(size0);
    long actualSize0 = 0;
    for (java.util.Map.Entry<java.lang.String, java.lang.Integer> e0: this.wifiData.entrySet()) {
      actualSize0++;
      out.startItem();
      out.writeString(e0.getKey());
      java.lang.Integer v0 = e0.getValue();
      out.writeInt(v0);
    }
    out.writeMapEnd();
    if (actualSize0 != size0)
      throw new java.util.ConcurrentModificationException("Map-size written was " + size0 + ", but element count was " + actualSize0 + ".");

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      long size0 = in.readMapStart();
      java.util.Map<java.lang.String,java.lang.Integer> m0 = this.wifiData; // Need fresh name due to limitation of macro system
      if (m0 == null) {
        m0 = new java.util.HashMap<java.lang.String,java.lang.Integer>((int)size0);
        this.wifiData = m0;
      } else m0.clear();
      for ( ; 0 < size0; size0 = in.mapNext()) {
        for ( ; size0 != 0; size0--) {
          java.lang.String k0 = null;
          k0 = in.readString();
          java.lang.Integer v0 = null;
          v0 = in.readInt();
          m0.put(k0, v0);
        }
      }

    } else {
      for (int i = 0; i < 1; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          long size0 = in.readMapStart();
          java.util.Map<java.lang.String,java.lang.Integer> m0 = this.wifiData; // Need fresh name due to limitation of macro system
          if (m0 == null) {
            m0 = new java.util.HashMap<java.lang.String,java.lang.Integer>((int)size0);
            this.wifiData = m0;
          } else m0.clear();
          for ( ; 0 < size0; size0 = in.mapNext()) {
            for ( ; size0 != 0; size0--) {
              java.lang.String k0 = null;
              k0 = in.readString();
              java.lang.Integer v0 = null;
              v0 = in.readInt();
              m0.put(k0, v0);
            }
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










