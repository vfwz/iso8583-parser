package cn.vfwz.iso8583.message.field;

import cn.ajsgn.common.java8583.exception.Iso8583Exception;
import cn.ajsgn.common.java8583.util.EncodeUtil;
import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.enumeration.FieldDataType;
import cn.vfwz.iso8583.enumeration.FieldLengthType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 可变长类型域
 */
public class VariableFieldType extends Iso8583FieldType {

    private FieldLengthType fieldLengthType;

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldDataType fieldDataType) {
        super(fieldIndex, fieldDataType);
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldDataType fieldDataType, Charset charset) {
        super(fieldIndex, fieldDataType, charset);
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldDataType fieldDataType, AlignType alignType) {
        super(fieldIndex, fieldDataType, alignType);
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldDataType fieldDataType, AlignType alignType, char padChar) {
        super(fieldIndex, fieldDataType, alignType, padChar);
        this.fieldLengthType = fieldLengthType;
    }

    public VariableFieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldDataType fieldDataType, AlignType alignType, char padChar, Charset charset) {
        super(fieldIndex, fieldDataType, alignType, padChar, charset);
        this.fieldLengthType = fieldLengthType;
    }

    @Override
    public Iso8583Field decodeField(InputStream is) throws IOException {
        int lengthBytesCount;
        switch (this.fieldLengthType) {
            case LLVAR:
                lengthBytesCount = 1;
                break;
            case LLLVAR:
                lengthBytesCount = 2;
                break;
            case LLLLVAR:
                lengthBytesCount = 3;
                break;
            default:
                throw new Iso8583Exception("暂不支持的域长类型[" + this.fieldLengthType + "]");
        }

        byte[] lengthBytes = new byte[lengthBytesCount];
        is.read(lengthBytes);
        int dataLength =  Integer.parseInt(EncodeUtil.bytes2Hex(lengthBytes), 10);

        int valueBytesCount = getValueBytesCount(dataLength);
        byte[] valueBytes = new byte[valueBytesCount];
        is.read(valueBytes);

        String value;
        switch (this.fieldDataType) {
            case BCD:
                value = EncodeUtil.bytes2Hex(valueBytes);
                value = removePad(value, dataLength);
                break;
            case HEX:
                value = EncodeUtil.bytes2Hex(valueBytes);
                break;
            case ASCII:
                value = new String(valueBytes, this.charset);
                break;
            default:
                throw new Iso8583Exception("暂不支持的域类型[" + this.fieldDataType + "]");
        }

        return new Iso8583Field(this.getFieldIndex(), dataLength, value, "", EncodeUtil.bytes2Hex(valueBytes), this);
    }

    @Override
    public Iso8583Field encodeField(String data) {
        String hexData = data;
        switch (this.fieldDataType) {
            case BCD:
                hexData = pad(hexData);
                break;
            case HEX:
                hexData = pad(hexData);
                break;
            case ASCII:
                hexData = EncodeUtil.bytes2Hex(data.getBytes(this.charset));
                hexData = pad(hexData);
                break;
            default:
                throw new Iso8583Exception("暂不支持的域类型[" + this.fieldDataType + "]");
        }

        int dataLength = hexData.length() / 2;
        return new Iso8583Field(this.getFieldIndex(), dataLength, data, "", hexData, this);
    }

    @Override
    protected int getValueBytesCount(int valueLength) {
        switch (fieldDataType) {
            case BCD:
                return (valueLength + 1) / 2;
            case HEX:
            case ASCII:
                return valueLength;
        }
        return valueLength;
    }
}
