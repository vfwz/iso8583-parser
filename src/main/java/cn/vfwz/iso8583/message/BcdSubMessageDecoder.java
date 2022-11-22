package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.enumeration.FieldValueType;
import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.message.field.FieldType;
import cn.vfwz.iso8583.message.field.FixedFieldType;

import java.util.Iterator;

/**
 * BCD格式子域报文解析工厂
 */
public class BcdSubMessageDecoder extends MessageDecoder {

    public BcdSubMessageDecoder(MessageConfig messageConfig) {
        super(messageConfig);
    }

    @Override
    public Message decode(String data) {
        MessageEncoder subMessageEncoder = new MessageEncoder(this.getMessageConfig());
        Iterator<FieldType> fieldTypeIterator = this.getMessageConfig().getFieldTypeIterator();
        int pos = 0;
        while (fieldTypeIterator.hasNext()) {
            FixedFieldType fieldType = (FixedFieldType) fieldTypeIterator.next();
            int end = pos + fieldType.getDataLength();
            if (end > data.length()) { // 剩下的数据不足解析，后面的子域不存在
                break;
            }
            String valueHex = data.substring(pos, end);
            subMessageEncoder.setField(fieldType.decodeField(valueHex));
            pos = pos + fieldType.getDataLength();
        }
        return subMessageEncoder.encode();
    }
}
