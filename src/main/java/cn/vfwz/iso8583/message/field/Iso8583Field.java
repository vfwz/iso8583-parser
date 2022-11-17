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

import cn.ajsgn.common.java8583.util.EncodeUtil;

/**
 * <p>8583报文字段抽象，不可变类<p>
 */
public final class Iso8583Field implements Comparable<Iso8583Field> {

    /**
     * 报文字段索引
     */
    private int index;
    private int length;
    private String value;
    private String valueHex;
    private String lengthHex;

    /**
     * 报文格式类型
     */
    private Iso8583FieldType fieldType = null;

    /**
     * <p>构造函数</p>
     *
     * @param index     当前字段索引
     * @param value     当前字段值
     * @param fieldType 当前字段类型
     */
    public Iso8583Field(int index, int length, String value, String lengthHex, String valueHex, Iso8583FieldType fieldType) {
        this.index = index;
        this.length = length;
        this.value = value;
        this.lengthHex = lengthHex;
        this.valueHex = valueHex;
        this.fieldType = fieldType;
    }

    /**
     * <p>获取当前字段索引</p>
     */
    public int getIndex() {
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
    public Iso8583FieldType getFieldType() {
        return fieldType;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueHex() {
        return valueHex;
    }

    public void setValueHex(String valueHex) {
        this.valueHex = valueHex;
    }

    public String getLengthHex() {
        return lengthHex;
    }

    public void setLengthHex(String lengthHex) {
        this.lengthHex = lengthHex;
    }

    public void setFieldType(Iso8583FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public byte[] getValueBytes() {
        return EncodeUtil.hex2Bytes(this.valueHex);
    }

    @Override
    public int compareTo(Iso8583Field field) {
        return this.index - field.index;
    }

    @Override
    public String toString() {
        return "Iso8583IsoField [index=" + index + ", value=" + value + ", fieldType=" + fieldType + "]";
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Iso8583Field other = (Iso8583Field) obj;
        if (fieldType == null) {
            if (other.fieldType != null)
                return false;
        } else if (!fieldType.equals(other.fieldType))
            return false;
        if (index != other.index)
            return false;
        if (value == null) {
            return other.value == null;
        } else return value.equals(other.value);
    }

}
