package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.constant.FieldIndex;
import cn.vfwz.iso8583.message.field.Field;
import cn.vfwz.iso8583.message.field.FieldType;
import cn.vfwz.iso8583.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.CharArrayReader;
import java.io.Reader;
import java.util.Iterator;

/**
 * <p>8583报文格式工厂，通过该类来约束一个8583报文各个字段的类型以及处理方式</p>
 */
@Slf4j
public class MessageDecoder {

    private final MessageConfig messageConfig;


    public MessageDecoder(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }


    /**
     * <p>将接受到的一个字符串格式的消息报文转换为一个Iso8583Message对象。</p>
     * <p>ps:data 包含消息长度信息</p>
     */
    public Message decodeWithMsgLength(String data) {
        return decode(data, true);
    }

    /**
     * <p>将一个包括消息长度的byte[]格式的消息报文转换为一个Iso8583Message对象</p>
     */
    public Message decodeWithMsgLength(byte[] data) {
        return decodeWithMsgLength(EncodeUtil.bytes2Hex(data));
    }

    /**
     * 解析hex格式的报文数据
     * 默认报文不带开头的长度部分
     *
     * @param hexData hex格式数据
     * @return 解析后的报文对象
     */
    public Message decode(String hexData) {
        return decode(hexData, false);
    }

    /**
     * 解析hex格式的报文数据
     *
     * @param hexData   hex格式报文
     * @param hasLength 是否有开头的长度部分
     * @return 解析后的报文对象
     */
    public Message decode(String hexData, boolean hasLength) {
        Reader reader = new CharArrayReader(hexData.toCharArray());

        //顺序解析，按FieldIndex升序逐个解析
        MessageEncoder encoder = new MessageEncoder(this.getMessageConfig());

        Iterator<FieldType> fieldTypeIterator = this.messageConfig.getFieldTypeIterator();
        String binaryByteBitmapStr = null;
        while (fieldTypeIterator.hasNext()) {
            FieldType fieldType = fieldTypeIterator.next();
            // 是否不需要解析报文开头的长度部分
            int fieldIndex = fieldType.getFieldIndex();
            if (this.messageConfig.getFieldsCount() != 128 // 128域报文长度不在开头
                    && FieldIndex.TOTAL_MESSAGE_LENGTH == fieldIndex
                    && !hasLength) {
                continue;
            }
            if (existInMessage(binaryByteBitmapStr, fieldIndex)) {
                Field field = this.messageConfig.getFieldType(fieldIndex).decodeField(reader);
                encoder.setField(field);
                if (fieldIndex == FieldIndex.BITMAP) { // 遇到bitmap, 后续的报文按照bitmap进行解析
                    binaryByteBitmapStr = EncodeUtil.binary(field.getValueBytes());
                }
            }
        }
        return encoder.encode();
    }

    /**
     * <p>将一个不包括消息长度的byte[]格式的消息报文转换成为一个Iso8583Message对象</p>
     */
    public Message decode(byte[] bytesData) {
        return this.decode(EncodeUtil.bytes2Hex(bytesData));
    }

    /**
     * 根据索引判断一个域是否存在于报文中
     *
     * @param binaryByteBitmapStr bitmap的二进制表示 0110000000111100000000001000000100001010110100001000110000010001
     * @param fieldIndex          域索引
     * @return 存在true，不存在false
     */
    private boolean existInMessage(String binaryByteBitmapStr, int fieldIndex) {
        if (binaryByteBitmapStr == null ||
                fieldIndex < 1 || fieldIndex > binaryByteBitmapStr.length()) {
            return true; // 没有配置bitmap, 或者索引超出bitmap范围的域都要进行解析
        }
        //判断索引是否为“1”来觉得是否要解析当前域
        return '1' == binaryByteBitmapStr.charAt(fieldIndex - 1);
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }
}
