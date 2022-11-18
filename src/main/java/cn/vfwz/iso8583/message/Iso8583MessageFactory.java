package cn.vfwz.iso8583.message;

import cn.ajsgn.common.java8583.util.EncodeUtil;
import cn.vfwz.iso8583.constant.FieldIndex;
import cn.vfwz.iso8583.message.field.FixedFieldType;
import cn.vfwz.iso8583.message.field.Iso8583Field;
import cn.vfwz.iso8583.message.field.Iso8583FieldType;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>8583报文格式工厂，通过该类来约束一个8583报文各个字段的类型以及处理方式</p>
 */
@Slf4j
public class Iso8583MessageFactory {

    /**
     * <p>用于保存字段类型的集合</p>
     * <p>相当于是一个报文模板集合</p>
     */
    private final Map<Integer, Iso8583FieldType> fieldTypeMap = new TreeMap<>();


    public Iso8583MessageFactory() {
    }

    /**
     * <p>分别设置8583报文中各个字段域的消息数据格式</p>
     */
    public Iso8583MessageFactory set(Iso8583FieldType fieldType) {
        fieldTypeMap.put(fieldType.getFieldIndex(), fieldType);
        return this;
    }

    /**
     * <p>获取指定索引的消息类型格式</p>
     * <p>index为int类型是为了保证避免由Factory维护的类型暴露</p>
     */
    public Iso8583FieldType getFieldType(int index) {
        return getFieldType(Integer.valueOf(index));
    }

    /**
     * <p>获取指定索引的消息类型格式</p>
     */
    private Iso8583FieldType getFieldType(Integer index) {
        Iso8583FieldType fieldType = fieldTypeMap.get(index);
        if (null == fieldType) {
            throw new NullPointerException(String.format("没有找到当前索引的配置信息 ： %s", index));
        }
        return fieldType;
    }

    /**
     * <p>将接受到的一个字符串格式的消息报文转换为一个Iso8583Message对象。</p>
     * <p>ps:data 包含消息长度信息</p>
     */
    public Iso8583Message parseWithMsgLength(String data) {
        //将接收到的String转换为byte[]
        byte[] srcData = EncodeUtil.hex2Bytes(data);
        return parseWithMsgLength(srcData);
    }

    /**
     * <p>将一个包括消息长度的byte[]格式的消息报文转换为一个Iso8583Message对象</p>
     */
    public Iso8583Message parseWithMsgLength(byte[] data) {
        return parse(getDestData(data));
    }


    /**
     * <p>解析原始数据，获得需要做解析的报文的byte[]数据</p>
     * <p>读取factory.msgLength个长度的字节作为整体报文的长度</p>\
     */
    private byte[] getDestData(byte[] srcData) {
        FixedFieldType fieldType = (FixedFieldType) fieldTypeMap.get(FieldIndex.MSG_LENGTH);
        //创建一个factory.msgLength个长度的byte[]用于去读取报文长度信息
        byte[] dataLength = new byte[fieldType.getDataLength()];
        //目标信息数据
        byte[] destData = new byte[0];
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
        }
        return destData;
    }

    /**
     * <p>将一个不包括消息长度的String格式的消息报文转换成为一个Iso8583Message对象</p>
     */
    public Iso8583Message parse(String data) {
        return parse(EncodeUtil.hex2Bytes(data));
    }

    /**
     * <p>将一个不包括消息长度的byte[]格式的消息报文转换成为一个Iso8583Message对象</p>
     */
    public Iso8583Message parse(byte[] data) {
        ByteArrayInputStream destIs = new ByteArrayInputStream(data);
        try {
            /*
             * 将srcData转换为ByteArrayInputStream对象，由ByteArrayInputStream来管理数组的pos
             * 用ByteArrayInputStream原因有2：
             * 1、ByteArrayInputStream 自身有pos属性，方便管理数组游标方便读取
             * 2、ByteArrayInputStream 内部持有buf，不需要做close()
             */
            //顺序解析：tpdu-head-mti-bitmap-data
            Iso8583MessageBuilder builder = new Iso8583MessageBuilder(this);
            builder.setField(getFieldType(FieldIndex.TPDU).decodeField(destIs))
                    .setField(getFieldType(FieldIndex.HEAD).decodeField(destIs))
                    .setField(getFieldType(FieldIndex.MTI).decodeField(destIs));
            Iso8583Field bitMapField = getFieldType(FieldIndex.BITMAP).decodeField(destIs);
            builder.setField(bitMapField);
            //解析bitmap，由此决定之后去解析哪些字段信息  0110000000111100000000001000000100001010110100001000110000010001
            String strByteBitmap = EncodeUtil.binary(bitMapField.getValueBytes());
            //判断索引是否为“1”来觉得是否要解析当前域
            //不做strByteBitmap.chatAt(0)做判断，因为在创建Iso8583Message对象时，已经通过参数Iso8583Factory对象的 boolean bit128 知道报文格式规范
            for (int bitIndex = 1; bitIndex < strByteBitmap.length(); bitIndex++) {
                //依次遍历下标，0表示当前位置的域不存在
                if ("0".equals(String.valueOf(strByteBitmap.charAt(bitIndex)))) {
                    continue;
                }
                builder.setField(getFieldType(bitIndex + 1).decodeField(destIs));
            }
            return builder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
