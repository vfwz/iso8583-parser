package cn.vfwz.iso8583.message.tlv;

import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * TLV（tag-length-value）的表示方式，即每个子域由标签(Tag)，子域取值的长度(Length)和子域取值(Value)构成。
 * <p>
 * tag标签（T）的属性为bit，由16进制表示，占1～2个字节长度。
 * 例如，“9F33”为一个占用两个字节的tag标签。而“95”为一个占用一个字节的tag标签。
 * 若tag标签的第一个字节（注：字节排序方向为从左往右数，第一个字节即为最左边的字节。bit排序规则同理。）的后五个bit为“11111”，
 * 则说明该tag占两个字节，例如“9F33”；否则占一个字节，例如“95”。
 * <p>
 * 子域长度（L）的属性也为bit，占1～3个字节长度。具体编码规则如下：
 * a)  当L字段最左边字节的最左bit位（即bit8）为0，表示该L字段占一个字节，
 * 它的后续7个bit位（即bit7～bit1）表示子域取值的长度，采用二进制数表示子域取值长度的十进制数。
 * 例如，某个域取值占3个字节，那么其子域取值长度表示为“00000011”。所以，若子域取值的长度在1～127字节之间，那么该L字段本身仅占一个字节。
 * b)  当L字段最左边字节的最左bit位（即bit8）为1，
 * 表示该L字段不止占一个字节，那么它到底占几个字节由该最左字节的后续7个bit位（即bit7～bit1）的十进制取值表示。
 * 例如，若最左字节为10000010，表示L字段除该字节外，后面还有两个字节。其后续字节的十进制取值表示子域取值的长度。
 * 例如，若L字段为“1000 0001 1111 1111”，表示该子域取值占255个字节。所以，若子域取值的长度在127～255字节之间，那么该L字段本身需占两个字节。
 * <p>
 * 子域取值（V）子域取值根据不同的子域含义分别取不同的数值。
 */
@Slf4j
public class TLV {


    /**
     * 解析tlv标签
     *
     * @param hexData 待解析的hex格式tlv数据
     * @return 解析后的tlv map
     */
    public static List<TLVObject> parse(String hexData) {
        List<TLVObject> retList = new ArrayList<>();
        try {
            InputStream bis = new ByteArrayInputStream(EncodeUtil.hex2Bytes(hexData));
            while (bis.available() > 0) {
                String tag = readTag(bis);
                int length = readLength(bis);
                String hexValue = readValue(bis, length);
                retList.add(new TLVObject(tag, length, hexValue));
            }
        } catch (IOException e) {
            log.error("解析tlv失败", e);
            throw new Iso8583Exception("解析tlv失败:" + hexData);
        }
        return retList;
    }

    /**
     * 组装tlv标签
     *
     * @param tlvList tlv对象列表
     * @return hex格式的tlv数据
     */
    public static String toHexString(List<TLVObject> tlvList) {
        StringBuilder ret = new StringBuilder();
        for (TLVObject tlvObject : tlvList) {
            String tag = tlvObject.getTag();
            String valueHex = tlvObject.getValue();
            String lengthHex = getHexLength(valueHex);
            ret.append(tag).append(lengthHex).append(valueHex);
        }
        return ret.toString();
    }

    private static String getHexLength(String hexValue) {
        int valueLength = hexValue.length() / 2;
        if (valueLength < 128) {
            // 128长度以下占用1个字节
            byte[] b = new byte[1];
            b[0] = (byte) valueLength;
            return EncodeUtil.bytes2Hex(b);
        } else if (valueLength < 256) {
            // 128长度以上256以下占用2个字节
            byte[] b = new byte[2];
            b[0] = (byte) 129;
            b[1] = (byte) valueLength;
            return EncodeUtil.bytes2Hex(b);
        } else if (valueLength < 65536) {
            // 256长度以上65535以下占用3个字节
            byte[] b = new byte[3];
            b[0] = (byte) 130;
            b[1] = (byte) (valueLength >> 8);
            b[2] = (byte) valueLength;
            return EncodeUtil.bytes2Hex(b);
        } else {
            throw new Iso8583Exception("TLV中数据长度超限:" + valueLength);
        }
    }

    private static String readValue(InputStream bis, int length) throws IOException {
        byte[] valueBytes = readFixLength(bis, length);
        return EncodeUtil.bytes2Hex(valueBytes);
    }

    private static String readTag(InputStream is) throws IOException {
        byte[] b1 = readFixLength(is, 1);
        String tagFirstHex = EncodeUtil.bytes2Hex(b1);
        String tagFirstByteBits = EncodeUtil.binary(b1);
        String tag;
        if (tagFirstByteBits.endsWith("11111")) {
            // 若tag标签的第一个字节（注：字节排序方向为从左往右数，第一个字节即为最左边的字节。bit排序规则同理。）的后五个bit为“11111”，
            // 则说明该tag占两个字节
            byte[] b2 = readFixLength(is, 1);
            tag = tagFirstHex + EncodeUtil.bytes2Hex(b2);
        } else {
            tag = tagFirstHex;
        }
        return tag;
    }


    private static int readLength(InputStream is) throws IOException {
        byte[] b1 = readFixLength(is, 1);
        int firstByteLen = EncodeUtil.byte2Int(b1[0]);

        int length;
        // a)  当L字段最左边字节的最左bit位（即bit8）为0，表示该L字段占一个字节，
        // * 它的后续7个bit位（即bit7～bit1）表示子域取值的长度，采用二进制数表示子域取值长度的十进制数。
        // * 例如，某个域取值占3个字节，那么其子域取值长度表示为“00000011”。所以，若子域取值的长度在1～127字节之间，那么该L字段本身仅占一个字节。
        if (firstByteLen < 128) {
            length = firstByteLen;
        }
        // b)  当L字段最左边字节的最左bit位（即bit8）为1，
        // * 表示该L字段不止占一个字节，那么它到底占几个字节由该最左字节的后续7个bit位（即bit7～bit1）的十进制取值表示。
        // * 例如，若最左字节为10000010，表示L字段除该字节外，后面还有两个字节。其后续字节的十进制取值表示子域取值的长度。
        // * 例如，若L字段为“1000 0001 1111 1111”，表示该子域取值占255个字节。所以，若子域取值的长度在127～255字节之间，那么该L字段本身需占两个字节。
        else if (firstByteLen == 129) { // 长度占2个字节
            byte[] b2 = readFixLength(is, 1);
            length = EncodeUtil.byte2Int(b2[0]);
        } else if (firstByteLen == 130) { // 长度占3个字节
            byte[] b2 = readFixLength(is, 2);
            length = ((b2[0] & 0xFF) << 8 | b2[1] & 0xff);
        } else {
            throw new Iso8583Exception("不正确的tlv长度");
        }
        return length;
    }

    private static byte[] readFixLength(InputStream is, int length) throws IOException {
        byte[] r = new byte[length];
        int i = is.read(r);
        return r;
    }

}



