package cn.vfwz.iso8583.message.field;

import cn.vfwz.iso8583.message.tlv.TlvObject;

import java.util.List;

public class TlvField extends Field {

    private List<TlvObject> tlvObjects;

    /**
     * <p>构造函数</p>
     * 不可变数据，只能通过构造函数生成
     */
    public TlvField(int index, int length, String value, String lengthHex, String valueHex, FieldType fieldType) {
        super(index, length, value, lengthHex, valueHex, fieldType);
    }


}
