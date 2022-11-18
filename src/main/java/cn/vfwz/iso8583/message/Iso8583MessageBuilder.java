package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.message.field.Iso8583Field;
import cn.vfwz.iso8583.message.field.Iso8583FieldType;

import java.util.Map;
import java.util.TreeMap;

public class Iso8583MessageBuilder {

    private final Map<Integer, Iso8583Field> fields = new TreeMap<>();

    private Iso8583MessageFactory factory;

    public Iso8583MessageBuilder(Iso8583MessageFactory factory) {
        this.factory = factory;
    }

    public Iso8583MessageBuilder setField(int index, String value) {
        Iso8583FieldType fieldType = this.factory.getFieldType(index);
        setField(fieldType.encodeField(value));
        return this;
    }

    public Iso8583MessageBuilder setField(Iso8583Field field) {
        fields.put(field.getIndex(), field);
        return this;
    }

    /**
     *
     */
    public Iso8583Message build() {
        Iso8583Message message = new Iso8583Message(factory);
        message.refresh(this.fields);
        return message;
    }
}
