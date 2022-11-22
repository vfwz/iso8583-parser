package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.constant.FieldIndex;
import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.message.field.Field;
import cn.vfwz.iso8583.message.field.FieldType;
import cn.vfwz.iso8583.message.field.FixedFieldType;
import cn.vfwz.iso8583.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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
        //将接收到的String转换为byte[]
        byte[] srcData = EncodeUtil.hex2Bytes(data);
        return decodeWithMsgLength(srcData);
    }

    /**
     * <p>将一个包括消息长度的byte[]格式的消息报文转换为一个Iso8583Message对象</p>
     */
    public Message decodeWithMsgLength(byte[] data) {
        if (this.messageConfig.getFieldsCount() == 128) { // 银联报文的域长度不在开头
            return decode(data);
        } else {
            return decode(getDestData(data));
        }
    }


    /**
     * <p>解析原始数据，获得需要做解析的报文的byte[]数据</p>
     * <p>读取factory.msgLength个长度的字节作为整体报文的长度</p>\
     */
    private byte[] getDestData(byte[] srcData) {
        FixedFieldType fieldType = (FixedFieldType) this.messageConfig.getFieldType(FieldIndex.TOTAL_MESSAGE_LENGTH);
        //创建一个factory.msgLength个长度的byte[]用于去读取报文长度信息
        byte[] dataLength = new byte[fieldType.getDataLength()];
        //目标信息数据
        byte[] destData;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(srcData)) {
            //读取长度信息
            bais.read(dataLength);
            //转换为10进制数据
            int destLength = Integer.parseInt(EncodeUtil.bytes2Hex(dataLength), 16);
            //创建等长的byte[]用于存放数据内容
            destData = new byte[destLength];
            bais.read(destData);
        } catch (IOException e) {
            log.error("解析报文失败", e);
            throw new Iso8583Exception("解析报文失败", e);
        }
        return destData;
    }

    /**
     * <p>将一个不包括消息长度的String格式的消息报文转换成为一个Iso8583Message对象</p>
     */
    public Message decode(String data) {
        return decode(EncodeUtil.hex2Bytes(data));
    }

    /**
     * <p>将一个不包括消息长度的byte[]格式的消息报文转换成为一个Iso8583Message对象</p>
     */
    public Message decode(byte[] data) {
        ByteArrayInputStream destIs = new ByteArrayInputStream(data);

        //顺序解析，按FieldIndex升序逐个解析
        MessageEncoder encoder = new MessageEncoder(this.getMessageConfig());

        // 解析头部
        for (int headerIndex = -128; headerIndex < 0; headerIndex++) { // 解析BitMap之前的头部信息，负数索引是
            if (this.messageConfig.getFieldsCount() != 128 && FieldIndex.TOTAL_MESSAGE_LENGTH == headerIndex) {
                continue; // 非128报文，不解析长度头
            }
            FieldType headerFieldType = this.messageConfig.getFieldTypeMute(headerIndex);
            if (headerFieldType == null) {
                continue;
            }
            encoder.setField(headerFieldType.decodeField(destIs));
        }

        // 解析域
        Field bitMapField = this.messageConfig.getFieldType(FieldIndex.BITMAP).decodeField(destIs);
        encoder.setField(bitMapField);
        //解析bitmap，由此决定之后去解析哪些字段信息  0110000000111100000000001000000100001010110100001000110000010001
        String strByteBitmap = EncodeUtil.binary(bitMapField.getValueBytes());
        //判断索引是否为“1”来觉得是否要解析当前域
        //不做strByteBitmap.chatAt(0)做判断，因为在创建Iso8583Message对象时，已经通过参数Iso8583Factory对象的 boolean bit128 知道报文格式规范
        for (int bitIndex = 1; bitIndex < strByteBitmap.length(); bitIndex++) {
            //依次遍历下标，0表示当前位置的域不存在
            if ("0".equals(String.valueOf(strByteBitmap.charAt(bitIndex)))) {
                continue;
            }
            encoder.setField(this.messageConfig.getFieldType(bitIndex + 1).decodeField(destIs));
        }
        return encoder.encode();
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }
}
