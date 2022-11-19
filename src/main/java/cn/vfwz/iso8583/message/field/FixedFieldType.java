package cn.vfwz.iso8583.message.field;

import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.enumeration.FieldValueType;
import cn.vfwz.iso8583.enumeration.FieldLengthType;

import java.nio.charset.Charset;

/**
 * 定长域
 */
public class FixedFieldType extends FieldType {

    private int dataLength;

    public FixedFieldType(int fieldIndex, int dataLength, FieldValueType fieldValueType) {
        super(fieldIndex, FieldLengthType.FIXED, fieldValueType);
        this.dataLength = dataLength;
    }

    public FixedFieldType(int fieldIndex, int dataLength, FieldValueType fieldValueType, Charset charset) {
        super(fieldIndex, FieldLengthType.FIXED, fieldValueType, charset);
        this.dataLength = dataLength;
    }

    public FixedFieldType(int fieldIndex, int dataLength, FieldValueType fieldValueType, AlignType alignType) {
        super(fieldIndex, FieldLengthType.FIXED, fieldValueType, alignType);
        this.dataLength = dataLength;
    }

    public FixedFieldType(int fieldIndex, int dataLength, FieldValueType fieldValueType, AlignType alignType, char padChar) {
        super(fieldIndex, FieldLengthType.FIXED, fieldValueType, alignType, padChar);
        this.dataLength = dataLength;
    }

    public FixedFieldType(int fieldIndex, int dataLength, FieldValueType fieldValueType, AlignType alignType, char padChar, Charset charset) {
        super(fieldIndex, FieldLengthType.FIXED, fieldValueType, alignType, padChar, charset);
        this.dataLength = dataLength;
    }


    public int getDataLength() {
        return dataLength;
    }

    @Override
    protected int getValueLength(String value, Charset charset) {
        return this.dataLength;
    }



}
