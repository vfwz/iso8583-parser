package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.message.field.FieldType;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class MessageConfig {


    /**
     * <p>用于保存字段类型的集合</p>
     * <p>相当于是一个报文模板集合</p>
     */
    private final Map<Integer, FieldType> fieldTypeMap = new TreeMap<>();
    // 报文域长度
    private int fieldsCount = 64; // 默认64域报文


    public MessageConfig() {

    }

    public MessageConfig(int fieldsCount) {
        this.fieldsCount = fieldsCount;
    }

    /**
     * <p>分别设置8583报文中各个字段域的消息数据格式</p>
     */
    public MessageConfig set(FieldType fieldType) {
        if (fieldType.getFieldIndex() > fieldsCount) {
            log.error("当前工厂最大报文域数量为:[{}], 该域索引[{}]超出了设置范围", fieldsCount, fieldType.getFieldIndex());
            throw new Iso8583Exception("当前工厂最大报文域数量为:[" + fieldsCount + "], 该域索引[" + fieldType.getFieldIndex() + "]超出了设置范围");
        }
        fieldTypeMap.put(fieldType.getFieldIndex(), fieldType);
        return this;
    }


    /**
     * <p>获取指定索引的消息类型格式</p>
     * <p>index为int类型是为了保证避免由Factory维护的类型暴露</p>
     */
    public FieldType getFieldType(int index) {
        FieldType fieldType = fieldTypeMap.get(index);
        if (null == fieldType) {
            throw new Iso8583Exception(String.format("没有找到当前索引的配置信息 ： %s", index));
        }
        return fieldType;
    }

    /**
     * <p>获取指定索引的消息类型格式</p>
     * 不存在的fieldType不会报错
     */
    public FieldType getFieldTypeMute(int index) {
        return fieldTypeMap.get(index);
    }


    public int getFieldsCount() {
        return fieldsCount;
    }

    public Iterator<FieldType> getFieldTypeIterator() {
        return this.fieldTypeMap.values().iterator();
    }

}
