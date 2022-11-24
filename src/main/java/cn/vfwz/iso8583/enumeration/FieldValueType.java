package cn.vfwz.iso8583.enumeration;

import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 域值部分类型
 */
@Slf4j
public enum FieldValueType {

    /**
     * 8421 BCD类型，一个字节表示两个数字
     * 例：99 转成字节格式为 10011001b，0x99
     * 即转hex后字符就是实际数值
     */
    BCD,

    /**
     * HEX类型，不做数据转换，直接取HEX
     */
    HEX,

    /**
     * 根据设置的charset对域进行解码
     */
    ASCII;

    /**
     * 将域值按当前格式获得域值的Hex表示
     *
     * @param data       域值
     * @param dataLength 域长，定长域需要对齐填充
     * @param alignType  对齐方式
     * @param padChar    补充字符
     * @param charset    编码方式
     * @return hex格式的域值部分
     */
    public String encode(String data, int dataLength, AlignType alignType, char padChar, Charset charset) {
        if (data == null) {
            return data;
        }
        int targetHexLength = getHexCount(dataLength, alignType);
        String hexData = getHexData(data, charset);

        return pad(hexData, targetHexLength, alignType, padChar, charset);
    }

    public String pad(String hexData, int targetHexLength, AlignType alignType, char padChar, Charset charset) {
        String valueHex;
        if (hexData.length() > targetHexLength) {
            log.error("注入的值[{}]长度超过目标长度[{}]", hexData, targetHexLength);
            throw new Iso8583Exception("注入的值[" + hexData + "]长度超过目标长度[" + targetHexLength + "]");
        } else if (hexData.length() == targetHexLength) { // 长度符合，无需填充
            valueHex = hexData;
        } else { // (hexData.length() < targetHexLength) {
            log.debug("域数据类型[{}], 当前值hex形式长度[{}]与目标长度[{}]不一致，根据填充方案alignType[{}], padChar[{}]进行填充",
                    this, hexData.length(), targetHexLength,
                    alignType, padChar);
            String c = String.valueOf(padChar);
            // 字符类型域转换时直接拼接字面字符
            if (this == ASCII) {
                c = EncodeUtil.bytes2Hex(c.getBytes(charset));
            }
            valueHex = alignType.pad(hexData, targetHexLength, c);
            if (valueHex.length() != targetHexLength) {
                throw new Iso8583Exception("Hex域值[" + hexData + "]经过填充方案alignType[" + alignType + "], padChar[" + padChar + "]填充后，" +
                        "结果[" + hexData + "长度超出预估值[" + targetHexLength + "]");
            }
        }
        return valueHex;
    }

    /**
     * 解析hex格式的域值到实际域值
     *
     * @param valueHex    hex形式的域值
     * @param valueLength 域长，BCD格式可能有对齐填充
     * @param alignType   对齐方式
     * @param charset     编码方式
     * @return 实际域值
     */
    public String decode(String valueHex, int valueLength, AlignType alignType, Charset charset) {
        String value;
        switch (this) {
            case BCD:
                value = valueHex;
                value = alignType.removePad(value, valueLength);
                break;
            case HEX:
                value = valueHex;
                break;
            case ASCII:
                value = new String(EncodeUtil.hex2Bytes(valueHex), charset);
                break;
            default:
                throw new Iso8583Exception("暂不支持的域值类型[" + this + "]");
        }
        return value;
    }

    /**
     * 获取域值在该类型下的长度
     *
     * @param value   域值
     * @param charset 编码方式
     * @return 域长
     */
    public int getValueLength(String value, Charset charset) {
        switch (this) {
            case BCD:
                return value.length();
            case ASCII:
                return value.getBytes(charset).length;
            case HEX:
                return value.length() / 2;
            default:
                throw new Iso8583Exception("暂不支持的域值类型[" + this + "]");
        }
    }


    /**
     * 根据长度获得当前值类型所占hex字符数量
     *
     * @param valueLength 域值长度
     * @param alignType
     * @return 字节数量
     */
    public int getHexCount(int valueLength, AlignType alignType) {
        int hexCount;
        switch (this) {
            case BCD:
                if (AlignType.NONE == alignType) {
                    hexCount = valueLength;
                } else {
                    hexCount = (valueLength % 2 == 0) ? valueLength : (valueLength + 1);
                }
                break;
            case HEX:
            case ASCII:
                hexCount = valueLength * 2;
                break;
            default:
                throw new Iso8583Exception("暂不支持的域值类型[" + this + "]");
        }
        return hexCount;
    }

    /**
     * 获取域值的Hex形式
     *
     * @param data    域值
     * @param charset 编码格式
     * @return hex形式的域值
     */
    private String getHexData(String data, Charset charset) {
        String hexData;
        switch (this) {
            case BCD:
            case HEX:
                hexData = data;
                break;
            case ASCII:
                hexData = EncodeUtil.bytes2Hex(data.getBytes(charset));
                break;
            default:
                throw new Iso8583Exception("暂不支持的域值类型[" + this + "]");
        }
        return hexData;
    }

    /**
     * 从hex类型的值获得当前域的实际长度
     *
     * @param hexValue hex类型的域值
     * @return 域实际长度
     */
    public int getValueLengthFromValueHex(String hexValue) {
        int valueLength;
        switch (this) {
            // BCD编码所见即所得，一个BCD数占用半个字节，和hex字符数相同
            case BCD:
                valueLength = hexValue.length();
                break;
            // HEX和ASCII编码方式，一个字符占用一个字节
            case HEX:
            case ASCII:
                valueLength = hexValue.length() / 2;
                break;
            default:
                throw new Iso8583Exception("暂不支持的域值类型[" + this + "]");
        }
        return valueLength;
    }
}
