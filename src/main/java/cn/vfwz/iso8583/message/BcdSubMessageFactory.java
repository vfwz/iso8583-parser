package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.enumeration.FieldValueType;
import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.message.field.FieldType;
import cn.vfwz.iso8583.message.field.FixedFieldType;

import java.util.Iterator;

/**
 * BCD格式子域报文解析工厂
 */
public class BcdSubMessageFactory extends MessageFactory {
    public BcdSubMessageFactory() {
    }

    @Override
    public MessageFactory set(FieldType fieldType) {
        if (fieldType.getFieldValueType() != FieldValueType.BCD) {
            throw new Iso8583Exception("BcdSubMessageFactory的子域格式类型必须为" + FieldValueType.BCD);
        }
        return super.set(fieldType);
    }

    @Override
    public Message parse(String data) {
        MessageBuilder subMessageBuilder = new MessageBuilder(this);
        Iterator<FieldType> fieldTypeIterator = super.getFieldTypeIterator();
        int pos = 0;
        while (fieldTypeIterator.hasNext()) {
            FixedFieldType fieldType = (FixedFieldType) fieldTypeIterator.next();
            int end = pos + fieldType.getDataLength();
            if (end > data.length()) { // 剩下的数据不足解析，后面的子域不存在
                break;
            }
            String valueHex = data.substring(pos, end);
            subMessageBuilder.setField(fieldType.decodeField(valueHex));
            pos = pos + fieldType.getDataLength();
        }
        return subMessageBuilder.build();
    }
}
