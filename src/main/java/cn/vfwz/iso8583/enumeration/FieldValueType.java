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
     * @param data 域值
     * @param dataLength 域长，定长域需要对齐填充
     * @param alignType 对齐方式
     * @param padChar 补充字符
     * @param charset 编码方式
     * @return hex格式的域值部分
     */
    public String encode(String data, int dataLength, AlignType alignType, char padChar, Charset charset) {
        if (data == null) {
            return data;
        }
        int targetHexLength = getBytesCount(dataLength) * 2;
        String hexData = getHexData(data, charset);

        if (hexData.length() > targetHexLength) {
            log.error("注入的值[{}]长度超过目标长度[{}]", hexData, targetHexLength);
            throw new Iso8583Exception("注入的值[" + hexData + "]长度超过目标长度[" + targetHexLength + "]");
        }

        if (hexData.length() < targetHexLength) {
            log.debug("域数据类型[{}], 当前值hex形式长度[{}]与目标长度[{}]不一致，根据填充方案alignType[{}], padChar[{}]进行填充",
                    this, hexData.length(), targetHexLength,
                    alignType, padChar);
            String c = String.valueOf(padChar);
            // 字符类型域转换时直接拼接字面字符
            if (FieldValueType.ASCII == this) {
                c = EncodeUtil.bytes2Hex(c.getBytes(charset));
            }
            String padHexData = hexData;
            while (padHexData.length() < targetHexLength) {
                if (AlignType.RIGHT == alignType) {
                    padHexData = c + padHexData;
                } else {
                    padHexData = padHexData + c;
                }
            }
            if (padHexData.length() != targetHexLength) {
                throw new Iso8583Exception("Hex域值[" + hexData + "]经过填充方案alignType[" + alignType + "], padChar[" + padChar + "]填充后，" +
                        "结果[" + hexData + "长度超出预估值[" + targetHexLength + "]");
            }
            hexData = padHexData;
        }
        return hexData;
    }

    /**
     * 解析hex格式的域值到实际域值
     * @param valueHex hex形式的域值
     * @param dataLength 域长，BCD格式可能有对齐填充
     * @param alignType 对齐方式
     * @param charset 编码方式
     * @return 实际域值
     */
    public String decode(String valueHex, int dataLength, AlignType alignType, Charset charset) {
        String value;
        switch (this) {
            case BCD:
                value = valueHex;
                value = removePad(value, dataLength, alignType);
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
     * 去除对齐补位的字符
     * @param value 域值
     * @param targetLength 目标长度
     * @param alignType 对齐方式
     * @return 去除对齐字符后的实际值
     */
    private String removePad(String value, int targetLength, AlignType alignType) {
        if (value == null || value.isEmpty() || value.length() <= targetLength) {
            return value;
        }
        int valLen = value.length();
        if (AlignType.LEFT == alignType) { // 左对齐，右边截取掉
            return value.substring(0, targetLength);
        } else { // 右对齐，左边截取掉
            return value.substring(valLen - targetLength);
        }
    }

    /**
     * 获取域值在该类型下的长度
     * @param value 域值
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
     * 根据长度获得当前值类型所占字节数量
     * @param dataLength 域长度
     * @return 字节数量
     */
    public int getBytesCount(int dataLength) {
        int bytesCount;
        switch (this) {
            case BCD:
                bytesCount = (dataLength + 1) / 2;
                break;
            case HEX:
            case ASCII:
                bytesCount = dataLength;
                break;
            default:
                throw new Iso8583Exception("暂不支持的域值类型[" + this + "]");
        }
        return bytesCount;
    }

    /**
     * 获取域值的Hex形式
     * @param data 域值
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

}
