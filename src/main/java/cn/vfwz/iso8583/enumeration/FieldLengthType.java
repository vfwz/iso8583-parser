package cn.vfwz.iso8583.enumeration;

import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.util.EncodeUtil;
import cn.vfwz.iso8583.util.StringUtil;

import java.nio.charset.StandardCharsets;

/**
 * 域长部分类型
 */
public enum FieldLengthType {


    // 定长
    FIXED,

    // BCD压缩的域长度部分
    LLVAR,
    LLLVAR,
    LLLLVAR,

    // ASCII表示的域长度部分
    LLVAR_ASCII,
    LLLVAR_ASCII;

    /**
     * 根据域值长度计算域长部分的hex值
     *
     * @param valueLength 域值长度
     * @return 域长部分的Hex值
     */
    public String encode(int valueLength) {
        String lengthStr;
        int hexCount = getHexCount();
        switch (this) {
            // 固定长度域无需长度部分
            case FIXED:
                return "";
            // BCD编码
            case LLVAR:
            case LLLVAR:
            case LLLLVAR:
                lengthStr = Integer.toString(valueLength);
                if (lengthStr.length() > hexCount) {
                    throw new Iso8583Exception("当前值长度[" + lengthStr + "]超过当前长度类型[" + this + "]范围");
                }
                return StringUtil.leftPad(lengthStr, hexCount, '0');
            // ASCII编码
            case LLVAR_ASCII:
            case LLLVAR_ASCII:
                lengthStr = Integer.toString(valueLength);
                if (lengthStr.length() * 2 > hexCount) {
                    throw new Iso8583Exception("当前值长度[" + lengthStr + "]超过当前长度类型[" + this + "]范围");
                }
                lengthStr = StringUtil.leftPad(lengthStr, hexCount / 2, '0');
                return EncodeUtil.bytes2Hex(lengthStr.getBytes(StandardCharsets.UTF_8));
            default:
                throw new Iso8583Exception("暂不支持的长度类型[" + this + "]");
        }
    }

    /**
     * 将hex类型的域长度部分解析为实际长度值
     */
    public int decode(String lengthHex) {
        return decode(EncodeUtil.hex2Bytes(lengthHex));
    }

    public int decode(byte[] lengthBytes) {
        switch (this) {
            case LLVAR:
            case LLLVAR:
            case LLLLVAR:
                return Integer.parseInt(EncodeUtil.bytes2Hex(lengthBytes));
            case LLVAR_ASCII:
            case LLLVAR_ASCII:
                return Integer.parseInt(new String(lengthBytes));
            default:
                throw new Iso8583Exception("暂不支持的域长类型[" + this + "]");
        }
    }


    /**
     * 获取当前长度类型占字节数量
     */
    public int getHexCount() {
        int lengthBytesCount;
        switch (this) {
            case FIXED:
                lengthBytesCount = 0;
                break;
            case LLVAR:
                lengthBytesCount = 2;
                break;
            case LLLVAR:
                lengthBytesCount = 4;
                break;
            case LLLLVAR:
                lengthBytesCount = 6;
                break;
            case LLVAR_ASCII:
                lengthBytesCount = 4;
                break;
            case LLLVAR_ASCII:
                lengthBytesCount = 6;
                break;
            default:
                throw new Iso8583Exception("暂不支持的域长类型[" + this + "]");
        }
        return lengthBytesCount;
    }
}
