package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.message.field.Field;
import cn.vfwz.iso8583.message.field.FieldType;

import java.util.Map;
import java.util.TreeMap;

public class MessageEncoder {

    private final MessageConfig messageConfig;
    private final Map<Integer, Field> fields = new TreeMap<>();

    public MessageEncoder(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    public MessageEncoder setField(int index, String value) {
        FieldType fieldType = this.messageConfig.getFieldType(index);
        setField(fieldType.encodeField(value));
        return this;
    }

    public MessageEncoder setField(Field field) {
        fields.put(field.getIndex(), field);
        return this;
    }

    public Message encode() {
        Message message = new Message(messageConfig);
        message.setFields(this.fields);
        return message;
    }
}
