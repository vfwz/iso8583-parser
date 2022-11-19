package cn.vfwz.iso8583.message.field;

import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.enumeration.FieldValueType;
import cn.vfwz.iso8583.enumeration.FieldLengthType;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 可变长类型域
 */
@Slf4j
public class VariableFieldType extends FieldType {

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType) {
        super(fieldIndex, fieldLengthType, fieldValueType);
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, Charset charset) {
        super(fieldIndex, fieldLengthType, fieldValueType, charset);
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, AlignType alignType) {
        super(fieldIndex, fieldLengthType, fieldValueType, alignType);
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, AlignType alignType, char padChar) {
        super(fieldIndex, fieldLengthType, fieldValueType, alignType, padChar);
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, AlignType alignType, char padChar, Charset charset) {
        super(fieldIndex, fieldLengthType, fieldValueType, alignType, padChar, charset);
        this.fieldLengthType = fieldLengthType;
    }

    /**
     * 根据域值的实际Hex长度获得域值的实际长度
     */
    @Override
    protected int getValueLength(String data, Charset charset) {
        return this.fieldValueType.getValueLength(data, charset);
    }
}
