package cn.vfwz.iso8583.message.field;

import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.enumeration.FieldValueType;
import cn.vfwz.iso8583.enumeration.FieldLengthType;
import cn.vfwz.iso8583.enumeration.TlvType;
import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 域类型
 */
@Slf4j
public abstract class FieldType {

    /**
     * 域长度类型枚举 定长，BCD压缩的变长，ASCII编码变长
     */
    protected FieldLengthType fieldLengthType;

    /**
     * 域值类型枚举，BCD、HEX、ASCII
     */
    protected FieldValueType fieldValueType;

    /**
     * 域索引位置
     */
    protected int fieldIndex;

    /**
     * 编码格式，ASCII格式的域需要，默认使用GBK编码
     */
    protected Charset charset = Charset.forName("GBK");

    /**
     * 数据对齐方向，默认左对齐
     */
    protected AlignType alignType = AlignType.LEFT;

    /**
     * 需要对齐时的字符，默认0
     */
    protected char padChar = '0';

    /**
     * TLV域的类型格式，默认不是TLV格式
     */
    protected TlvType tlvType = TlvType.NONE; // 默认不是TLV格式

    FieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType) {
        this.fieldIndex = fieldIndex;
        this.fieldLengthType = fieldLengthType;
        this.fieldValueType = fieldValueType;
    }

    FieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, TlvType tlvType) {
        this(fieldIndex, fieldLengthType, fieldValueType);
        this.tlvType = tlvType;
    }

    FieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, AlignType alignType) {
        this(fieldIndex, fieldLengthType, fieldValueType);
        this.alignType = alignType;
    }

    FieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, Charset charset) {
        this(fieldIndex, fieldLengthType, fieldValueType);
        this.charset = charset;
    }

    FieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, AlignType alignType, char padChar) {
        this(fieldIndex, fieldLengthType, fieldValueType, alignType);
        this.padChar = padChar;
    }

    FieldType(int fieldIndex, FieldLengthType fieldLengthType, FieldValueType fieldValueType, AlignType alignType, char padChar, Charset charset) {
        this(fieldIndex, fieldLengthType, fieldValueType, alignType, padChar);
        this.charset = charset;
    }

    /**
     * 使用当前域格式从流中解析域
     * @param is 输入的报文流
     * @return 解析后的域
     */
    public Field decodeField(InputStream is) {
        try {
            int dataLength;
            byte[] lengthBytes;
            if (this.fieldLengthType == FieldLengthType.FIXED) {
                dataLength = this.getValueLength(null, charset);
                lengthBytes = new byte[0];
            } else {
                lengthBytes = readBytes(is, this.fieldLengthType.getBytesCount());
                dataLength = this.fieldLengthType.decode(lengthBytes);
            }

//            int valueBytesCount = getValueBytesCount(dataLength);
            int valueBytesCount = this.fieldValueType.getBytesCount(dataLength);
            byte[] valueBytes = readBytes(is, valueBytesCount);

            String lengthHex = EncodeUtil.bytes2Hex(lengthBytes);
            String valueHex = EncodeUtil.bytes2Hex(valueBytes);
            String value = this.fieldValueType.decode(valueHex, dataLength, this.alignType, this.charset);

            return new Field(this.getFieldIndex(), dataLength, value, lengthHex, valueHex, this);
        } catch (Exception e) {
            log.error("解析域[{}]失败", this.fieldIndex, e);
            throw new Iso8583Exception(e);
        }
    }

    public Field encodeField(String data) {
        try {
            int valueLength = getValueLength(data, charset);
            String lengthHex = this.fieldLengthType.encode(valueLength);
            String valueHex = "";
            if (valueLength == 0) {
                if (this.fieldLengthType != FieldLengthType.FIXED) {
                    log.info("当前域[{}]为变长域[{}]，但是数据长度为0，请检查", this.fieldIndex, this.fieldValueType);
                }
            } else {
                valueHex = this.fieldValueType.encode(data, valueLength, this.alignType, this.padChar, this.charset);
            }
            return new Field(this.getFieldIndex(), valueLength, data, lengthHex, valueHex, this);
        } catch (Exception e) {
            log.error("组装域[{}]时发生异常, data[{}], lengthType[{}], valueType[{}]", data, this.fieldIndex, this.fieldLengthType, this.fieldValueType);
            throw new Iso8583Exception(e);
        }
    }

    /**
     * 根据域值的Hex值获取实际数据长度
     */
    protected abstract int getValueLength(String value, Charset charset);

    public int getFieldIndex() {
        return fieldIndex;
    }

    protected byte[] readBytes(InputStream is, int bytesLen) throws IOException {
        byte[] bytes = new byte[bytesLen];
        is.read(bytes);
        return bytes;
    }


}
