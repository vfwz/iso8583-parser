package cn.vfwz.iso8583.message.field;

import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.enumeration.FieldLengthType;
import cn.vfwz.iso8583.enumeration.FieldValueType;
import cn.vfwz.iso8583.enumeration.TlvType;
import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.message.Message;
import cn.vfwz.iso8583.message.MessageConfig;
import cn.vfwz.iso8583.message.MessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;
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

    protected MessageConfig fieldMessageConfig = null;

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
     *
     * @param reader 输入的报文流
     * @return 解析后的域
     */
    public Field decodeField(Reader reader) {
        try {
            int dataLength = getValueLength(reader);
            String lengthHex = getLengthHex(dataLength);
            String valueHex = getValueHex(reader, dataLength, this.alignType);
            String value = this.fieldValueType.decode(valueHex, dataLength, this.alignType, this.charset);

            Message fieldMessage = null;
            if (this.getFieldMessageConfig() != null) {
                log.debug("该域[{}]包含子域，根据配置进行解析", fieldIndex);
                MessageDecoder fieldMessagDeocoder = new MessageDecoder(this.getFieldMessageConfig());
                fieldMessage = fieldMessagDeocoder.decode(valueHex);
            }

            return new Field(this.getFieldIndex(), dataLength, value, lengthHex, valueHex, this, fieldMessage);
        } catch (Exception e) {
            log.error("解析域[{}]失败", this.fieldIndex, e);
            throw new Iso8583Exception(e);
        }
    }

    /**
     * 使用当前域格式从hex格式的域值解析域
     *
     * @param valueHex hex格式报文值
     * @return 解析后的域
     */
    public Field decodeField(String valueHex) {
        try {
            int valueLength = this.getValueLengthFromValueHex(valueHex);
            String lengthHex = this.fieldLengthType.encode(valueLength);
            String value = this.fieldValueType.decode(valueHex, valueLength, this.alignType, this.charset);

            return new Field(this.getFieldIndex(), valueLength, value, lengthHex, valueHex, this);
        } catch (Exception e) {
            log.error("解析域[{}]失败", this.fieldIndex, e);
            throw new Iso8583Exception(e);
        }
    }

    public Field encodeField(String value) {
        try {
            int valueLength = getValueLength(value, charset);
            String lengthHex = this.fieldLengthType.encode(valueLength);
            String valueHex = "";
            if (valueLength == 0) {
                if (this.fieldLengthType != FieldLengthType.FIXED) {
                    log.warn("当前域[{}]为变长域，但是数据长度为0，请检查", this);
                }
            } else {
                valueHex = this.fieldValueType.encode(value, valueLength, this.alignType, this.padChar, this.charset);
            }
            return new Field(this.getFieldIndex(), valueLength, value, lengthHex, valueHex, this);
        } catch (Exception e) {
            log.error("组装域[{}]时发生异常, value[{}], lengthType[{}], valueType[{}]", value, this.fieldIndex, this.fieldLengthType, this.fieldValueType);
            throw new Iso8583Exception(e);
        }
    }

    /**
     * 使用子域报文获取域值
     *
     * @param subMessage 子域报文信息
     * @return 解析后的域
     */
    public Field encodeField(Message subMessage) {
        if (this.fieldMessageConfig == null) {
            throw new Iso8583Exception("当前域值类型[" + this + "]没有配置子域格式");
        }
        if (subMessage == null) {
            throw new Iso8583Exception("subMessage is null, 无法解析域, 域值类型[" + this + "]");
        }

        try {
            String subMessageHexString = subMessage.getHexString();
            int valueLength = this.fieldValueType.getValueLengthFromValueHex(subMessageHexString);
            String lengthHex = this.fieldLengthType.encode(valueLength);

            String valueHex = this.fieldValueType.pad(subMessageHexString, 0, this.alignType, this.padChar, this.charset);
            String value = this.fieldValueType.decode(valueHex, valueLength, this.alignType, this.charset);

            return new Field(this.getFieldIndex(), valueLength, value, lengthHex, valueHex, this);
        } catch (Exception e) {
            log.error("通过子域组装域[{}]时发生异常", this);
            throw new Iso8583Exception(e);
        }
    }

    /**
     *
     */
    protected abstract int getValueLength(String value, Charset charset);


    /**
     * 从流中获得当前域的长度
     *
     * @param reader 报文流
     * @return 当前域值长度
     */
    protected abstract int getValueLength(Reader reader);

    protected abstract String getLengthHex(int valueLength);

    /**
     * 从数据流中读取当前域值的Hex形式数据
     *
     * @param reader      报文hex字符流
     * @param valueLength 域值的长度
     * @param alignType
     * @return hex形式的域值
     */
    protected String getValueHex(Reader reader, int valueLength, AlignType alignType) {
        int hexCount = this.fieldValueType.getHexCount(valueLength, alignType);
        return readHexChar(reader, hexCount);
    }

    /**
     * 根据域值的Hex值获取实际数据长度
     *
     * @param valueHex hex形式的域值
     * @return 域的实际长度
     */
    protected abstract int getValueLengthFromValueHex(String valueHex);

    public int getFieldIndex() {
        return fieldIndex;
    }

    /**
     * 读取指定数量的hex字符
     *
     * @param reader 报文hex字符流
     * @param count  读取数量
     * @return hex串
     */
    protected String readHexChar(Reader reader, int count) {
        char[] chars = new char[count];
        try {
            int read = reader.read(chars);
        } catch (IOException e) {
            throw new Iso8583Exception("读取报文流失败", e);
        }
        return new String(chars);
    }

    public MessageConfig getFieldMessageConfig() {
        return fieldMessageConfig;
    }

    public FieldType setFieldMessageConfig(MessageConfig fieldMessageConfig) {
        this.fieldMessageConfig = fieldMessageConfig;
        return this;
    }


    public FieldLengthType getFieldLengthType() {
        return fieldLengthType;
    }

    public FieldValueType getFieldValueType() {
        return fieldValueType;
    }

    @Override
    public String toString() {
        return "FieldType{" +
                "fieldLengthType=" + fieldLengthType +
                ", fieldValueType=" + fieldValueType +
                ", fieldIndex=" + fieldIndex +
                ", charset=" + charset +
                ", alignType=" + alignType +
                ", padChar=" + padChar +
                ", subMessageFactory=" + fieldMessageConfig +
                ", tlvType=" + tlvType +
                '}';
    }
}
