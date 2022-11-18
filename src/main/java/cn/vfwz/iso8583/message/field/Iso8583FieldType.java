package cn.vfwz.iso8583.message.field;

import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.enumeration.FieldDataType;
import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * <p>字段类型抽象</p>
 * <p>如果字段类型为 NUMERIC，LLVAR_NUMERIC，LLLVAR_NUMERIC，LLLLVAR_NUMERIC类型，因为使用的是BCD编码。所以，当数据长度为奇数时，会触发使用补位策略进行填充。</p>
 * <p>默认使用策略：左对齐，右补‘0’，长度计算不包含填充位。</p>
 */
@Slf4j
public abstract class Iso8583FieldType {

    protected FieldDataType fieldDataType;

    protected int fieldIndex;

    // 默认使用GBK编码
    protected Charset charset = Charset.forName("GBK");

    // 默认左对齐
    protected AlignType alignType = AlignType.LEFT;

    // 默认补字符0
    protected char padChar = '0';

    Iso8583FieldType(int fieldIndex, FieldDataType fieldDataType) {
        this.fieldIndex = fieldIndex;
        this.fieldDataType = fieldDataType;
    }

    Iso8583FieldType(int fieldIndex, FieldDataType fieldDataType, AlignType alignType) {
        this.fieldIndex = fieldIndex;
        this.fieldDataType = fieldDataType;
        this.alignType = alignType;
    }

    Iso8583FieldType(int fieldIndex, FieldDataType fieldDataType, Charset charset) {
        this.fieldIndex = fieldIndex;
        this.fieldDataType = fieldDataType;
        this.charset = charset;
    }

    Iso8583FieldType(int fieldIndex, FieldDataType fieldDataType, AlignType alignType, char padChar) {
        this.fieldIndex = fieldIndex;
        this.fieldDataType = fieldDataType;
        this.alignType = alignType;
        this.padChar = padChar;
    }

    Iso8583FieldType(int fieldIndex, FieldDataType fieldDataType, AlignType alignType, char padChar, Charset charset) {
        this.fieldIndex = fieldIndex;
        this.fieldDataType = fieldDataType;
        this.charset = charset;
        this.alignType = alignType;
        this.padChar = padChar;
    }

    public abstract Iso8583Field decodeField(InputStream is) throws IOException;

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

        int valueHexLength = hexData.length();
        return new Iso8583Field(this.getFieldIndex(), getValueLength(valueHexLength), data, getLengthHex(valueHexLength), hexData, this);
    }

    /**
     * 根据域值的Hex值获取实际数据长度
     */
    protected abstract int getValueLength(int valueHexLength);

    public int getFieldIndex() {
        return fieldIndex;
    }

    /**
     * 根据当前设置的域值的Hex字符计算该域实际应占字节数量
     */
    protected abstract int getValueBytesCount(int hexLength);

    /**
     * 根据当前设置的域值的Hex字符计算该域的长度部分Hex值
     */
    protected abstract String getLengthHex(int valueHexLength);

    /**
     * 去除对齐补位的数
     */
    protected String removePad(String value, int length) {
        if (value == null || value.isEmpty() || value.length() <= length) {
            return value;
        }
        int valLen = value.length();
        if (AlignType.LEFT == this.alignType) { // 左对齐，右边截取掉
            return value.substring(0, length);
        } else { // 右对齐，左边截取掉
            return value.substring(valLen - length);
        }
    }

    /**
     * 将16进制的Hex数据，对齐到指定长度
     *
     * @param hexData
     * @return
     */
    protected String pad(String hexData) {
        if (hexData == null) {
            return hexData;
        }
        int targetHexLength;
        int valueLength = getValueLength(hexData.length());
        if (FieldDataType.BCD == this.fieldDataType) {
            targetHexLength = ((valueLength + 1) / 2) * 2; // 奇数变偶数
        } else {
            targetHexLength = valueLength * 2;
        }
        if (hexData.length() > targetHexLength) {
            log.error("域[{}]中注入的值[{}]长度超过目标长度[{}]", this.getFieldIndex(), hexData, targetHexLength);
            throw new Iso8583Exception("域[" + this.getFieldIndex() + "]中注入的值[" + hexData + "]长度超过目标长度[" + targetHexLength + "]");
        }

        if (hexData.length() < targetHexLength) {
            log.debug("域[{}]类型为[{}], 当前值[{}]的长度与目标值[{}]不一致，根据填充方案AlignType[{}],filledChar[{}]进行填充"
                    , this.getFieldIndex(), this.fieldDataType.toString(), hexData.length(), targetHexLength
                    , this.alignType, this.padChar);
            String c = String.valueOf(this.padChar);
            // 字符类型域转换时直接拼接字面字符
            if (FieldDataType.ASCII == this.fieldDataType) {
                c = EncodeUtil.bytes2Hex(c.getBytes(this.charset));
            }
            while (hexData.length() != targetHexLength) {
                if (AlignType.RIGHT == this.alignType) {
                    hexData = c + hexData;
                } else {
                    hexData = hexData + c;
                }
            }
        }
        return hexData;
    }


}
