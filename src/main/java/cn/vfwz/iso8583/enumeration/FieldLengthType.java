package cn.vfwz.iso8583.enumeration;

import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.util.EncodeUtil;

import java.nio.charset.StandardCharsets;

public enum FieldLengthType {

    FIXED,
    // BCD压缩的域长度部分
    LLVAR,
    LLLVAR,
    LLLLVAR,
    // ASCII表示的域长度部分
    LLVAR_ASCII,
    LLLVAR_ASCII;

    public int decodeLength(byte[] lengthBytes) {
        switch (this) {
            case LLVAR:
            case LLLVAR:
            case LLLLVAR:
                return Integer.parseInt(EncodeUtil.bytes2Hex(lengthBytes), 10);
            case LLVAR_ASCII:
            case LLLVAR_ASCII:
                return Integer.parseInt(new String(lengthBytes, StandardCharsets.UTF_8));
            default:
                throw new Iso8583Exception("暂不支持的域长类型[" + this + "]");
        }
    }


    /**
     * 获取当前域长度部分所占字节数量
     */
    public int getLengthBytesCount() {
        int lengthBytesCount;
        switch (this) {
            case LLVAR:
                lengthBytesCount = 1;
                break;
            case LLLVAR:
                lengthBytesCount = 2;
                break;
            case LLLLVAR:
                lengthBytesCount = 3;
                break;
            case LLVAR_ASCII:
                lengthBytesCount = 2;
                break;
            case LLLVAR_ASCII:
                lengthBytesCount = 3;
                break;
            default:
                throw new Iso8583Exception("暂不支持的域长类型[" + this + "]");
        }
        return lengthBytesCount;
    }
}
