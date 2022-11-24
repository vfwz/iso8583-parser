/*
 * Copyright (c) 2017, Ajsgn 杨光 (Ajsgn@foxmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.vfwz.iso8583.message.field;

import cn.vfwz.iso8583.message.Message;
import cn.vfwz.iso8583.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

/**
 * 8583报文域抽象，不可变类
 */
@Slf4j
public class Field implements Comparable<Field> {

    /**
     * 报文字段索引
     */
    private final int index;
    /**
     * 数据长度
     */
    private final int length;
    /**
     * 数据值
     */
    private final String value;
    /**
     * hex格式的数据值
     */
    private final String valueHex;
    /**
     * hex格式的长度值
     */
    private final String lengthHex;
    /**
     * 报文格式类型
     */
    private final FieldType fieldType;

    protected Field parentField;

    /**
     * 子域
     */
    protected Message fieldMessage;

//    private final String tagName; // TLV格式域 tag信息
//
////    private final Message subMessage;
//    private final Map<Integer, Field> subFields = new TreeMap<>(); // 子域信息

    public Field(int index, int length, String value, String lengthHex, String valueHex, FieldType fieldType) {
        this.parentField = null;
        this.index = index;
        this.length = length;
        this.value = value;
        this.lengthHex = lengthHex;
        this.valueHex = valueHex;
        this.fieldType = fieldType;
    }

    public Field(int index, int length, String value, String lengthHex, String valueHex, FieldType fieldType, Message fieldMessage) {
        this.parentField = null;
        this.index = index;
        this.length = length;
        this.value = value;
        this.lengthHex = lengthHex;
        this.valueHex = valueHex;
        this.fieldType = fieldType;
        this.fieldMessage = fieldMessage;
        if (fieldMessage != null) {
            fieldMessage.getFieldIterator().forEachRemaining(field -> field.setParentField(this));
        }
    }

    /**
     * <p>获取当前字段索引</p>
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * <p>获取当前字段值</p>
     */
    public String getValue() {
        return value;
    }

    /**
     * <p>获取当前字段类型</p>
     */
    public FieldType getFieldType() {
        return fieldType;
    }

    public int getLength() {
        return length;
    }

    public String getValueHex() {
        return valueHex;
    }

    public String getLengthHex() {
        return lengthHex;
    }

    public byte[] getValueBytes() {
        return EncodeUtil.hex2Bytes(this.valueHex);
    }

    public void setParentField(Field parentField) {
        this.parentField = parentField;
    }

    /**
     * 获取当前域的索引，带父域
     */
    public String getIndexString() {
        StringBuilder isb = new StringBuilder();
        if (this.parentField != null) {
            isb.append(this.parentField.getIndexString());
            isb.append(".");
        }

        isb.append(this.index);
        return isb.toString();
    }

    @Override
    public int compareTo(Field field) {
        return this.index - field.index;
    }

    @Override
    public String toString() {
        return "Field{" +
                "parentField=" + parentField +
                ", index=" + index +
                ", length=" + length +
                ", value='" + value + '\'' +
                ", valueHex='" + valueHex + '\'' +
                ", lengthHex='" + lengthHex + '\'' +
                ", fieldType=" + fieldType +
                '}';
    }

    public String toFormatString() {
        StringBuilder sb = new StringBuilder();
        String format = "[F%s][%s][%s][%s][%s]\n";
        sb.append(String.format(format, this.getIndexString(), fieldType.getFieldLengthType(), fieldType.getFieldValueType(), this.getLength(), this.getValue()));
        if (this.fieldMessage != null) {
            Iterator<Field> subField = this.fieldMessage.getFieldIterator();
            while (subField.hasNext()) {
                sb.append(" ");
                sb.append(subField.next().toFormatString());
            }
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldType == null) ? 0 : fieldType.hashCode());
        result = prime * result + index;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Field other = (Field) obj;
        if (fieldType == null) {
            if (other.fieldType != null) {
                return false;
            }
        } else if (!fieldType.equals(other.fieldType)) {
            return false;
        }
        if (index != other.index) {
            return false;
        }
        if (value == null) {
            return other.value == null;
        } else {
            return value.equals(other.value);
        }
    }

}
