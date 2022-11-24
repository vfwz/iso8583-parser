package cn.vfwz.iso8583.enumeration;

import cn.vfwz.iso8583.exception.Iso8583Exception;

/**
 * 对齐方式，左右
 */
public enum AlignType {

    /**
     * 左对齐，右补字符
     */
    LEFT,

    /**
     * 右对齐，左补字符
     */
    RIGHT,

    /**
     * 不对齐，不补充字符
     * 比如BCD类型定长子域，一个BCD数字只占用半个字节
     */
    NONE;

    public String pad(String hexData, int targetHexLength, String padChar) {
        StringBuilder hexDataBuilder = new StringBuilder(hexData);
        while (hexDataBuilder.length() < targetHexLength) {
            switch (this) {
                case LEFT:
                    hexDataBuilder.append(padChar);
                    break;
                case RIGHT:
                    hexDataBuilder.insert(0, padChar);
                    break;
                case NONE:
                    break;
                default:
                    throw new Iso8583Exception("暂不支持的对齐类型[" + this + "]");

            }
        }
        hexData = hexDataBuilder.toString();
        return hexData;
    }

    public String removePad(String value, int targetLength) {
        if (value == null || value.isEmpty() || value.length() <= targetLength) {
            return value;
        }
        int valLen = value.length();
        String ret = value;
        switch (this) {
            case LEFT: // 左对齐，右边截取掉
                ret = value.substring(0, targetLength);
                break;
            case RIGHT: // 右对齐，左边截取掉
                ret = value.substring(valLen - targetLength);
                break;
            case NONE: // 不对齐，什么都不干
                break;
            default:
                throw new Iso8583Exception("暂不支持的对齐类型[" + this + "]");
        }
        return ret;
    }

}
