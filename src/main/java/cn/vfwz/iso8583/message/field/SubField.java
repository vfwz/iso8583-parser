package cn.vfwz.iso8583.message.field;

import cn.vfwz.iso8583.message.Message;
import cn.vfwz.iso8583.message.MessageConfig;
import cn.vfwz.iso8583.message.MessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 带子域的域
 */
@Slf4j
public class SubField extends Field {

    private final Map<Integer, Field> subFields = new TreeMap<>(); // 子域信息

    public SubField(int index, int length, String value, String lengthHex, String valueHex, FieldType fieldType) {
        super(index, length, value, lengthHex, valueHex, fieldType);
        MessageConfig messageConfig = fieldType.getFieldMessageConfig();
        if (messageConfig == null) {
            log.error("该域没有设置子域解析工厂，不能初始化为子域，转为普通域");
        } else {
            try {
                log.debug("域配置[{}]存在子域配置, 根据配置解析子域", this);
                MessageDecoder decoder = new MessageDecoder(messageConfig);
                Message subMessage = decoder.decode(value);
                subMessage.getFieldIterator().forEachRemaining(field -> {
                    field.setParentField(this);
                    this.subFields.put(field.getIndex(), field);
                });
            } catch (Exception e) {
                log.error("使用工厂[{}]解析子域失败，转为普通域", messageConfig);
            }
        }
    }

    public Iterator<Field> getSubFieldIterator() {
        return this.subFields.values().iterator();
    }

    public String getSubValue(int index) {
        return this.subFields.get(index).getValue();
    }

}
