package cn.vfwz.iso8583.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PinUtil {

    private static final int PIN_BYTE_LENGTH = 8;
    private static final String FILLED_IN = "FFFFFFFFFFFFFFFF";

    /**
     * ANSI X9.8 Format（带主账号信息）
     *
     * @param pin 明文pin值
     * @param pin cardNo 卡号信息  2域，35域原始数据（等号不做转换），36域原始数据均可（等号不做转换）
     * @return String pinBlock的字符串形式
     * @Title: pinBlockStr
     * @Description: ANSI X9.8 Format（带主账号信息）
     * @author Ajsgn@foxmail.com
     * @date 2017年4月10日 下午1:29:24
     */
    public static final String pinBlockStr(String pin, String cardNo) {
        return EncodeUtil.bytes2Hex(pinBlockByte(pin, cardNo));
    }

    /**
     * ANSI X9.8 Format（带主账号信息）
     *
     * @param pin 明文pin值
     * @param pin cardNo 卡号信息  2域，35域原始数据（等号不做转换），36域原始数据均可（等号不做转换）
     * @return byte[] pinBlock的byte[]形式
     * @Title: pinBlockByte
     * @Description: ANSI X9.8 Format（带主账号信息）
     * @author Ajsgn@foxmail.com
     * @date 2017年4月10日 下午1:27:38
     */
    public static final byte[] pinBlockByte(String pin, String cardNo) {
        //获取卡号pan值
        byte[] pan = panByte(cardNo);
        //获取明文pinBlock
        byte[] pinBlock = pinBlockByte(pin);
        //长度匹配
        if (pan.length != pinBlock.length) {
            throw new IllegalArgumentException(String.format("计算pin长度与pan长度不等：pin.length = %d  pan.length = %d", pinBlock.length, pan.length));
        }
        byte[] result = new byte[pinBlock.length];
        //异或计算
        for (int i = 0; i < pan.length; i++) {
            result[i] = (byte) (pan[i] ^ pinBlock[i]);
        }
        return result;
    }

    /**
     * ANSI X9.8 Format（不带主账号信息）
     *
     * @param pin 明文pin码
     * @return String pinBlock的字符串形式
     * @Title: pinBlockStr
     * @Description: ANSI X9.8 Format（不带主账号信息）
     * @author Ajsgn@foxmail.com
     * @date 2017年4月10日 下午1:27:01
     */
    public static final String pinBlockStr(String pin) {
        return EncodeUtil.bytes2Hex(pinBlockByte(pin));
    }

    /**
     * ANSI X9.8 Format（不带主账号信息）
     *
     * @param pin 明文pin码
     * @return byte[] pinBlock
     * @Title: pinBlock
     * @Description: ANSI X9.8 Format（不带主账号信息）
     * @author Ajsgn@foxmail.com
     * @date 2017年4月10日 下午1:06:53
     */
    public static final byte[] pinBlockByte(String pin) {
        // 填充pinBlock
        StringBuilder sb = new StringBuilder(pin).append(FILLED_IN);
        // 删除多余长度
        sb.delete((PIN_BYTE_LENGTH - 1) * 2, sb.length());
        // 创建byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
        byte[] msgLength = EncodeUtil.hex2Bytes(String.valueOf(pin.length()));
        byte[] content = EncodeUtil.hex2Bytes(sb.toString());
        try {
            baos.write(msgLength);
            baos.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    private static final byte[] panByte(String cardNo) {
        return EncodeUtil.hex2Bytes(panStr(cardNo));
    }

    /**
     * 取主账号的pan值
     * （2域，二磁道，三磁道）均可
     * 如果传2域值，则从右起第二位向左数12位；
     * 如果传二磁道信息，从磁道2（35域）分隔符'＝'左边第二位开始，向左取12个字符，作为参与PIN加、密的PAN
     * 如只有磁道3（36域），则从磁道3分隔符'＝'左边第二位开始，向左取12个字符，作为参与PIN加、解密的PAN
     *
     * @param cardNo 卡号信息（2域，二磁道，三磁道）均可
     * @return String pan的字符串表示形式
     * @Title: panStr
     * @Description: 取主账号的pan值
     * @author Ajsgn@foxmail.com
     * @date 2017年4月10日 下午1:08:49
     */
    private static final String panStr(String cardNo) {
        checkCardNo(cardNo);
        String pan = "";
        if (cardNo.contains("=")) {
            int index = cardNo.indexOf("=");
            pan = cardNo.substring(index - 12 - 1, index - 1);
        } else {
            int length = cardNo.length();
            pan = cardNo.substring(length - 12 - 1, length - 1);
        }
        return String.format("0000%s", pan);
    }

    /**
     * 简单的检查卡号的有效性，不能为空，不能小于16位长度
     *
     * @param cardNo 卡号信息
     * @throws IllegalArgumentException 如果卡号信息不符合简单的判断要去
     * @Title: checkCardNo
     * @Description: 简单的检查卡号的有效性，不能为空，不能小于16位长度
     * @author Ajsgn@foxmail.com
     * @date 2017年4月10日 下午1:12:47
     */
    private static void checkCardNo(String cardNo) {
        if (StringUtil.isBlank(cardNo) || cardNo.length() < 16) {
            throw new IllegalArgumentException("无效的卡号信息...");
        }
    }

    public static void main(String[] args) {
        //0x06 0x12 0x53 0xDF 0xFE 0xDC 0xBA 0x98
        System.out.println(pinBlockStr("123456", "123456789012345678"));//061253DFFEDCBA98
        //0x06 0x12 0x71 0x31 0x76 0xFE 0xDC 0xBA
        System.out.println(pinBlockStr("123456", "1234567890123456"));//0612713176FEDCBA
    }

}
