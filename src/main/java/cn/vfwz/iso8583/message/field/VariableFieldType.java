package cn.vfwz.iso8583.message.field;

import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.enumeration.FieldLengthType;
import cn.vfwz.iso8583.enumeration.FieldValueType;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 可变长类型域
 */
@Slf4j
public class VariableFieldType extends FieldType {

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType) {
        super(fieldIndex, fieldLengthType, fieldValueType);
        this.fieldLengthType = fieldLengthType;
        if (FieldLengthType.FIXED == fieldLengthType) {
            log.error("不允许使用FIXED域值类型初始化构建可变长度域, " +
                    "fieldIndex[" + fieldIndex + "], fieldLengthType[" + fieldLengthType + "], fieldValueType[" + fieldValueType + "]");
        }
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, Charset charset) {
        this(fieldIndex, fieldLengthType, fieldValueType);
        this.charset = charset;
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, AlignType alignType) {
        this(fieldIndex, fieldLengthType, fieldValueType);
        this.alignType = alignType;
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, AlignType alignType, char padChar) {
        this(fieldIndex, fieldLengthType, fieldValueType, alignType);
        this.padChar = padChar;
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, AlignType alignType, char padChar, Charset charset) {
        this(fieldIndex, fieldLengthType, fieldValueType, alignType, padChar);
        this.charset = charset;
        this.fieldLengthType = fieldLengthType;
    }

    /**
     * 根据域值的实际Hex长度获得域值的实际长度
     */
    @Override
    protected int getValueLength(String data, Charset charset) {
        return this.fieldValueType.getValueLength(data, charset);
    }

    @Override
    protected int getValueLength(InputStream inputStream) {
        byte[] lengthBytes = readBytes(inputStream, this.fieldLengthType.getBytesCount());
        return this.fieldLengthType.decode(lengthBytes);
    }

    @Override
    protected String getLengthHex(int valueLength) {
        return this.fieldLengthType.encode(valueLength);
    }

    @Override
    protected int getValueLengthFromValueHex(String valueHex) {
        return this.fieldValueType.getValueLengthFromValueHex(valueHex);
    }
}
