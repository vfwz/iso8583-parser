package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.message.field.Field;
import cn.vfwz.iso8583.message.field.FieldType;

import java.util.Map;
import java.util.TreeMap;

public class MessageBuilder {

    private Map<Integer, Field> fields = new TreeMap<>();

    private final MessageFactory factory;

    public MessageBuilder(MessageFactory factory) {
        this.factory = factory;
    }

    public MessageBuilder setField(int index, String value) {
        FieldType fieldType = this.factory.getFieldType(index);
        setField(fieldType.encodeField(value));
        return this;
    }

    public MessageBuilder setField(Field field) {
        fields.put(field.getIndex(), field);
        return this;
    }

    public Message build() {
        Message message = new Message(factory);
        message.setFields(this.fields);
        return message;
    }

    public Message build(String hexMessage) {
        Message message = factory.parse(hexMessage);
        return message;
    }
}
