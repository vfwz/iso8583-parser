package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.constant.FieldIndex;
import cn.vfwz.iso8583.message.field.Iso8583Field;
import cn.vfwz.iso8583.message.field.Iso8583FieldType;
import cn.vfwz.iso8583.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.TreeMap;


@Slf4j
public class Iso8583Message {

    /**
     * <p>自身所拥有的一个报文格式工厂</p>
     */
    private Iso8583MessageFactory factory;
    /**
     * <p>当前报文所对应的一个bitmap 64/128 域规范由本身持有的factory.isBit128()方法决定</p>
     */
    private byte[] bitmap;
    private Map<Integer, Iso8583Field> fields = new TreeMap<>();

    /**
     * <p>构造函数，需要一个Iso8583MessageFactory来约束报文解析规范</p>
     * 默认只能通过Builder创建Message，或者通过Factory解析得到
     */
    protected Iso8583Message(Iso8583MessageFactory factory) {
        if (null == factory) {
            throw new NullPointerException("required param factory is null");
        }
        this.factory = factory;
        bitmap = new byte[factory.getFieldsCount()];
    }

    protected void setFields(Map<Integer, Iso8583Field> fields) {
        // 根据新的fields 更新bitmap，msgLength等域
        this.fields = fields;
        // 先刷新bitmap
        refreshBitMap();
        refreshMsgLength();
    }

    /**
     * Message的生成使用MessageBuilder
     * 更新指定域的值
     */
    public void updateValue(int index, String value) {
        Iso8583FieldType type = factory.getFieldType(index);
        putField(type.encodeField(value));
        refreshMsgLength();
        refreshBitMap();
    }

    public void removeField(int index) {
        //将数据填入map，已处理填充位数据
        if (fields.containsKey(index)) {
            fields.remove(index);
            //位图标记
            refreshBitMap();
            refreshMsgLength();
        } else {
            log.info("当前报文中包含域[{}]", index);
        }
    }

    private void putField(Iso8583Field field) {
        //将数据填入map，已处理填充位数据
        fields.put(field.getIndex(), field);
    }


    /**
     * <p>获取报文中的某个域的值</p>
     * <p>不关注填充内容，获取到的结果值中不包含填充内容</p>
     */
    public String getValue(int index) {
        Iso8583Field field = fields.get(index);
        return field == null ? null : field.getValue();
    }

    /**
     * <p>返回bitmap的位图内容</p>
     * <p>返回值示例：0110000000111100000000001000000100001010110100001000110000010001</p>
     */
    public String getBitmapBitString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bitmap.length; i++) {
            sb.append(bitmap[i]);
        }
        return sb.toString();
    }

    /**
     * <p>获取bitmap的字节数组内容</p>
     */
    public byte[] getBitmapBytes() {
        return EncodeUtil.binary(getBitmapBitString());
    }

    /**
     * 当报文域发生变动时，需要刷新报文长度信息
     */
    private void refreshMsgLength() {
        int msgLength = 0;
        for (Iso8583Field field : fields.values()) {
            if (this.factory.getFieldsCount() != 128 && field.getIndex() == FieldIndex.TOTAL_MESSAGE_LENGTH) {
                continue;
            }
            msgLength += (field.getLengthHex().length() / 2 + field.getValueHex().length() / 2);
        }
        Iso8583Field msgLengthField = this.factory
                .getFieldType(FieldIndex.TOTAL_MESSAGE_LENGTH)
                .encodeField(Integer.toHexString(msgLength));
        this.putField(msgLengthField);
    }

    /**
     * 当报文域发生变动时，需要刷新BitMap信息
     */
    private void refreshBitMap() {
        for (Iso8583Field field : fields.values()) {
            if (field.getIndex() < 1) {
                continue;
            }
            bitmap[field.getIndex() - 1] = 1;
        }
        Iso8583Field bitMapField = this.factory
                .getFieldType(FieldIndex.BITMAP)
                .encodeField(EncodeUtil.bytes2Hex(getBitmapBytes()));
        this.putField(bitMapField);
    }

    /**
     * <p>获取bitmap字节数组的字符串表现形式</p>
     * <p>返回示例：603C00810AD08C11</p>
     */
    public String getBitmapString() {
        return EncodeUtil.bytes2Hex(getBitmapBytes());
    }

    /**
     * <p>格式化消息输出</p>
     * <p>建议用于开发阶段调试，因为其打印内容未做掩码，为纯明文内容，不安全</p>
     *
     * @return String 格式化输出
     * @Title: toFormatString
     * @Description: 格式化消息输出
     */
    public String toFormatString() {
        StringBuilder sb = new StringBuilder();
        String format = "F%s:[%s]";
        for (Map.Entry<Integer, Iso8583Field> entry : fields.entrySet()) {
            Iso8583Field field = entry.getValue();
            String index = (field.getIndex() >= 0 && field.getIndex() < 10) ? "0" + field.getIndex() : Integer.toString(field.getIndex());
            sb.append(String.format(format, index, field.getValue()));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * <p>解析报文协议，返回字节数组，用于应用间的消息的传输</p>
     */
    public byte[] getBytes() {
        return EncodeUtil.hex2Bytes(getHexString());
    }

    /**
     * 获取当前报文的完整字符串表示形式
     */
    public String getHexString() {
        StringBuilder res = new StringBuilder();
        // 循环写入所有字段信息
        for (Iso8583Field field : fields.values()) {
            res.append(field.getLengthHex());
            res.append(field.getValueHex());
        }
        return res.toString();
    }

    /**
     * <p>获取用于计算mac的macBlock的字符串表示</p>
     * <p>macBlock : mti+bitmap+data(除去校验位的8583报文数据)</p>
     */
    public String getMacBlockString() {
        StringBuilder macBlockBuilder = new StringBuilder();
        //循环写入字段信息
        for (Iso8583Field field : fields.values()) {
            if (field.getIndex() >= FieldIndex.MTI && field.getIndex() <= FieldIndex.F63) {
                macBlockBuilder.append(field.getLengthHex());
                macBlockBuilder.append(field.getValueHex());
            }
        }
        return macBlockBuilder.toString();
    }

    /**
     * <p>获取用于计算mac的macBlock的字节数组表示</p>
     * <p>macBlock : mti+bitmap+data(出去校验位的8583报文数据)</p>
     */
    public byte[] getMacBlock() {
        return EncodeUtil.hex2Bytes(getMacBlockString());
    }

    /**
     * <p>获取校验域的字符串形式值</p>
     */
    public String getMacString() {
        Iso8583Field field = fields.get(FieldIndex.F64);
        return field.getValueHex();
    }

    /**
     * <p>获取校验域的字节数组格式</p>
     */
    public byte[] getMac() {
        return EncodeUtil.hex2Bytes(getMacString());
    }


    /**
     * 比较两个Iso8583Message对象是否一样
     */
    public boolean compareWith(Iso8583Message message) {
        if (null == message) {
            return false;
        }
        if (this == message) {
            return true;
        }
        return this.getHexString().equals(message.getHexString());
    }

}
