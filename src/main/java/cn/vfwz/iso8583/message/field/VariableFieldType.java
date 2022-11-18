package cn.vfwz.iso8583.message.field;

import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.enumeration.FieldDataType;
import cn.vfwz.iso8583.enumeration.FieldLengthType;
import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 可变长类型域
 */
@Slf4j
public class VariableFieldType extends Iso8583FieldType {

    private final FieldLengthType fieldLengthType;

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
    public Iso8583Field decodeField(InputStream is) {
        try {
            byte[] lengthBytes = new byte[this.fieldLengthType.getLengthBytesCount()];
            is.read(lengthBytes);
            int dataLength = this.fieldLengthType.decodeLength(lengthBytes);

            int valueBytesCount = getValueBytesCount(dataLength);
            byte[] valueBytes = new byte[valueBytesCount];
            is.read(valueBytes);

            String value;
            String dataHex = EncodeUtil.bytes2Hex(valueBytes);
            switch (this.fieldDataType) {
                case BCD:
                    value = dataHex;
                    value = removePad(value, dataLength);
                    break;
                case HEX:
                    value = dataHex;
                    break;
                case ASCII:
                    value = new String(valueBytes, this.charset);
                    break;
                default:
                    throw new Iso8583Exception("暂不支持的域类型[" + this.fieldDataType + "]");
            }
            return new Iso8583Field(this.getFieldIndex(), dataLength, value, getLengthHex(dataHex.length()), dataHex, this);
        } catch (Exception e) {
            log.error("解析域[{}]失败", this.fieldIndex, e);
            throw new Iso8583Exception(e);
        }
    }


    @Override
    protected int getValueBytesCount(int dataLength) {
        switch (fieldDataType) {
            case BCD:
                return (dataLength + 1) / 2;
            case HEX:
            case ASCII:
                return dataLength;
        }
        return dataLength;
    }

    @Override
    protected String getLengthHex(int valueHexLength) {
        int lengthBytesCount = this.fieldLengthType.getLengthBytesCount();

        // bcd压缩的，hex所见即所得
        String lengthHex = Integer.toString(getValueLength(valueHexLength));
        if (lengthHex.length() > lengthBytesCount * 2) {
            throw new Iso8583Exception("当前域[" + this.fieldIndex + "]设置的值长度超过设定范围[" + this.fieldDataType + "]");
        }
        // 变长域的长度部分，补0到指定的字节数目
        while (lengthHex.length() != lengthBytesCount * 2) {
            lengthHex = '0' + lengthHex;
        }
        return lengthHex;
    }

    /**
     * 根据域值的实际Hex长度获得域值的实际长度
     */
    @Override
    protected int getValueLength(int valueHexLength) {
        switch (fieldDataType) {
            case BCD:
                return valueHexLength;
            case HEX:
            case ASCII:
                return valueHexLength / 2;
        }
        return valueHexLength / 2;
    }
}
