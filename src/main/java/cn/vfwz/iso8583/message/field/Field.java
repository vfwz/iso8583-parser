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

import cn.vfwz.iso8583.util.EncodeUtil;

/**
 * 8583报文域抽象，不可变类
 */
public class Field implements Comparable<Field> {

    /**
     * 报文字段索引
     */
    private final Integer index;
    /**
     * 数据长度
     */
    private final Integer length;
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

    /**
     * <p>构造函数</p>
     * 不可变数据，只能通过构造函数生成
     */
    public Field(int index, int length, String value, String lengthHex, String valueHex, FieldType fieldType) {
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

    @Override
    public int compareTo(Field field) {
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
