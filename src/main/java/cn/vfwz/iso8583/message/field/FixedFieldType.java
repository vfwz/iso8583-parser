package cn.vfwz.iso8583.message.field;

import cn.ajsgn.common.java8583.exception.Iso8583Exception;
import cn.ajsgn.common.java8583.util.EncodeUtil;
import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.enumeration.FieldDataType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 定长域
 */
public class FixedFieldType extends Iso8583FieldType {

    private int dataLength;

    public FixedFieldType(int fieldIndex, int dataLength, FieldDataType fieldDataType) {
        super(fieldIndex, fieldDataType);
        this.dataLength = dataLength;
    }

    public FixedFieldType(int fieldIndex, int dataLength, FieldDataType fieldDataType, Charset charset) {
        super(fieldIndex, fieldDataType, charset);
        this.dataLength = dataLength;
    }

    public FixedFieldType(int fieldIndex, int dataLength, FieldDataType fieldDataType, AlignType alignType) {
        super(fieldIndex, fieldDataType, alignType);
        this.dataLength = dataLength;
    }

    public FixedFieldType(int fieldIndex, int dataLength, FieldDataType fieldDataType, AlignType alignType, char padChar) {
        super(fieldIndex, fieldDataType, alignType, padChar);
        this.dataLength = dataLength;
    }

    public FixedFieldType(int fieldIndex, int dataLength, FieldDataType fieldDataType, AlignType alignType, char padChar, Charset charset) {
        super(fieldIndex, fieldDataType, alignType, padChar, charset);
        this.dataLength = dataLength;
    }

    /**
     * 从字节流中解析当前类型的Field
     */
    @Override
    public Iso8583Field decodeField(InputStream is) throws IOException {
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

    public int getDataLength() {
        return dataLength;
    }

    /**
     * 将内容解析为当前类型的Field
     */
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

        return new Iso8583Field(this.getFieldIndex(), dataLength, data, "", hexData, this);
    }

    @Override
    protected int getValueBytesCount(int valueLength) {
        switch (fieldDataType) {
            case BCD:
                return (dataLength + 1) / 2;
            case HEX:
            case ASCII:
                return dataLength;
        }
        return valueLength;
    }


}
