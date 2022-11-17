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
package cn.ajsgn.common.java8583.field;

import cn.ajsgn.common.java8583.exception.Iso8583Exception;
import cn.ajsgn.common.java8583.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * <p>字段类型抽象</p>
 * <p>如果字段类型为 NUMERIC，LLVAR_NUMERIC，LLLVAR_NUMERIC，LLLLVAR_NUMERIC类型，因为使用的是BCD编码。所以，当数据长度为奇数时，会触发使用补位策略进行填充。</p>
 * <p>默认使用策略：左对齐，右补‘0’，长度计算不包含填充位。</p>
 * <p>如果希望改变填充策略，则可以通过调用 setFillBlankStrategy({@link Iso8583FillBlankStrategy} fillBlankStrategy) 来完成策略的修改</p>
 *
 * @author Ajsgn@foxmail.com
 * @ClassName: Iso8583FieldType
 * @Description: 字段类型抽象
 * @date 2017年3月23日 下午12:44:53
 */
@Slf4j
public class Iso8583FieldType {

    /**
     * <p>字段类型描述</p>
     */
    private final FieldTypeValue fieldTypeValue;
    /**
     * <p>字段位索引</p>
     */
    private String fieldIndex = "";
    /**
     * <p>字段所占长度</p>
     * <p>对于变长字段，该字段没有特殊意义</p>
     */
    private final int fieldLength;
    /**
     * <p>字段填充策略</p>
     * <p>默认使用策略：左对齐，右补‘0’，长度计算不包含填充位。</p>
     */
    private Iso8583FillBlankStrategy fillBlankStrategy = null;

    /**
     * <p>构造函数</p>
     *
     * @param fieldTypeValue 字段类型值
     * @param fieldLength    字段所占用字节长度
     */
    public Iso8583FieldType(FieldTypeValue fieldTypeValue, int fieldLength) {
        this.fieldTypeValue = fieldTypeValue;
        this.fieldLength = fieldLength;
    }

    /**
     * <p>获取字段类型</p>
     *
     * @return Iso8583FieldTypeName
     * @Title: getFieldTypeValue
     * @Description: 获取字段类型
     * @author Ajsgn@foxmail.com
     * @date 2017年3月24日 上午10:49:05
     */
    public FieldTypeValue getFieldTypeValue() {
        return fieldTypeValue;
    }

    /**
     * <p>获取字段类型所占用字节长度</p>
     *
     * @return int 所占用的字节长度
     * @Title: getFieldLength
     * @Description: 获取字段类型所占用字节长度
     * @author Ajsgn@foxmail.com
     * @date 2017年3月24日 上午10:49:21
     */
    public int getFieldLength() {
        return fieldLength;
    }

    @Override
    public String toString() {
        return "IsoFieldType [fieldTypeValue=" + fieldTypeValue + ", fieldLength=" + fieldLength + "]";
    }

    /**
     * <p>获取字段类型所占用字节长度</p>
     * <p>ps，通常该功能只使用与字段类型为:LLVAR_NUMERIC,LLLVAR_NUMERIC，没有做非类型判断</p>
     *
     * @return int 所占用的字节长度
     * @Title: getFieldLength
     * @Description: 获取字段类型所占用字节长度
     * @author Ajsgn@foxmail.com
     * @date 2017年3月24日 上午10:49:21
     */
    public Iso8583FillBlankStrategy getFillBlankStrategy() {
        //当需要获取策略信息时，检查是否有做设置，如果没有相关策略，则使用一个默认策略
//        fillBlankStrategyCheck();
        return fillBlankStrategy;
    }

    /**
     * 设置补位策略，返回当前对象本身，方便链式调用</br>
     * 参数如果为null，并没有拥有自己的补位策略，则会使用一个默认补位策略；如果已经有补位策略，则放弃操作
     *
     * @param fillBlankStrategy 补位策略
     * @return Iso8583FieldType
     * @Title: setFillBlankStrategy
     * @Description: 设置补位策略
     * @author Ajsgn@foxmail.com
     * @date 2017年3月27日 上午10:02:57
     */
    public Iso8583FieldType setFillBlankStrategy(Iso8583FillBlankStrategy fillBlankStrategy) {
        // 如果设置的策略为null则判断当前是否已经有设置填补策略，没有的话则使用一个默认策略，如果有，则放弃操作
        if (null == fillBlankStrategy) {
//            fillBlankStrategyCheck();
        } else {
            this.fillBlankStrategy = fillBlankStrategy;    //策略不为空，则使用一个新的填补策略
        }
        return this;
    }

    /**
     * 检查是否有补位策略，如果没有，则使用一个默认补位策略
     *
     * @Title: fillBlankStrategyCheck
     * @Description: 检查是否有补位策略，如果没有，则使用一个默认补位策略
     * @author Ajsgn@foxmail.com
     * @date 2017年3月27日 上午10:02:50
     */
//    private void fillBlankStrategyCheck() {
//        if (null == this.fillBlankStrategy) {
//            this.fillBlankStrategy = Iso8583FillBlankStrategy.DEFAULT_RIGHT_APPEND_STRATETY;
//        }
//    }
    public String getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(String fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    /**
     * 当前类型域，长度所占字节数
     */
    public int lengthBytesCount() {
        int bytesCount;
        //根据字段类型，进行相应的解析动作
        switch (this.getFieldTypeValue()) {
            case NUMERIC:
            case HEX:
            case CHAR: {
                // 定长域
                bytesCount = 0;
                break;
            }
            case LLVAR_NUMERIC:
            case LLVAR_HEX:
            case LLVAR_CHAR: {
                // 定长域
                bytesCount = 1;
                break;
            }
            case LLLVAR_NUMERIC:
            case LLLVAR_HEX:
            case LLLVAR_CHAR: {
                // 定长域
                bytesCount = 2;
                break;
            }
            case LLLLVAR_NUMERIC:
            case LLLLVAR_HEX:
            case LLLLVAR_CHAR: {
                // 定长域
                bytesCount = 3;
                break;
            }
            default:
                throw new Iso8583Exception("错误的域类型[" + this.getFieldTypeValue().toString() + "]");
        }
        return bytesCount;
    }


    /**
     * 当前类型域，根据长度得到实际占用字节
     */
    public int valueBytesCount(int valueLength) {
        int bytesCount;
        //根据字段类型，进行相应的解析动作
        switch (this.getFieldTypeValue()) {
            case NUMERIC: {
                // BCD压缩，长度减半，定长
                bytesCount = (fieldLength + 1) / 2;
                break;
            }
            case LLVAR_NUMERIC:
            case LLLVAR_NUMERIC:
            case LLLLVAR_NUMERIC: {
                // BCD压缩，长度减半
                bytesCount = (valueLength + 1) / 2;
                break;
            }
            case HEX:
            case CHAR:{
                // 不压缩，定长
                bytesCount = fieldLength;
                break;
            }
            case LLVAR_HEX:
            case LLVAR_CHAR:
            case LLLVAR_HEX:
            case LLLVAR_CHAR:
            case LLLLVAR_HEX:
            case LLLLVAR_CHAR: {
                // 不压缩
                bytesCount = valueLength;
                break;
            }
            default:
                throw new Iso8583Exception("错误的域类型[" + this.getFieldTypeValue().toString() + "]");
        }
        return bytesCount;
    }

    /**
     * 从字节数组解码域的实际值
     */
    public String decodeValue(byte[] content, int valueLength, Charset charset) {
        String result;
        //根据字段类型，进行相应的解析动作
        switch (this.getFieldTypeValue()) {
            case HEX:
            case LLVAR_HEX:
            case LLLVAR_HEX:
            case LLLLVAR_HEX: {
                // Hex类型域，转HEX后直接输出
                result = EncodeUtil.bytes2Hex(content);
                break;
            }
            case CHAR:
            case LLVAR_CHAR:
            case LLLVAR_CHAR:
            case LLLLVAR_CHAR: {
                // 字符类型域
                result = new String(content, charset);
                break;
            }
            case NUMERIC:
            case LLVAR_NUMERIC:
            case LLLVAR_NUMERIC:
            case LLLLVAR_NUMERIC: {
                // BCD压缩类型域
                result = EncodeUtil.bytes2Hex(content);
                // 根据长度进行对齐
                result = removeAppend(result, valueLength);
                break;
            }
            default:
                throw new Iso8583Exception("错误的域类型[" + this.getFieldTypeValue().toString() + "]");
        }
        return result;
    }

    /**
     * 将域值解析成字节数组
     */
    public byte[] encodeValue(String value, Charset charset) {
        byte[] result;
        //根据字段类型，进行相应的解析动作
        switch (this.getFieldTypeValue()) {
            case HEX: {
                // Hex类型域，转HEX后直接输出
                // 根据长度进行对齐
                value = append(value);
                result = EncodeUtil.hex2Bytes(value);
                break;
            }
            case LLVAR_HEX:
            case LLLVAR_HEX:
            case LLLLVAR_HEX: {
                // Hex类型域，转HEX后直接输出
                result = EncodeUtil.hex2Bytes(value);
                break;
            }
            case CHAR: {
                // 字符类型域
                // 根据长度进行对齐
                value = append(EncodeUtil.bytes2Hex(value.getBytes(charset)));
                result = EncodeUtil.hex2Bytes(value);
                break;
            }
            case LLVAR_CHAR:
            case LLLVAR_CHAR:
            case LLLLVAR_CHAR: {
                // 字符类型域
                result = value.getBytes(charset);
                break;
            }
            case NUMERIC: {
                // BCD压缩类型域
                // 根据长度进行对齐
                value = append(value);
                result = EncodeUtil.hex2Bytes(value);
                break;
            }
            case LLVAR_NUMERIC:
            case LLLVAR_NUMERIC:
            case LLLLVAR_NUMERIC: {
                // BCD压缩类型域
                // 根据长度进行对齐
                value = append(value);
                result = EncodeUtil.hex2Bytes(value);
                break;
            }
            default:
                throw new Iso8583Exception("错误的域类型[" + this.getFieldTypeValue().toString() + "]");
        }
        return result;
    }

    /**
     * 去除对齐补位的数
     *
     * @param value 解析出来的域值
     * @return 返回根据对齐方案截取后的域值
     */
    private String removeAppend(String value, int length) {
        Iso8583FillBlankStrategy fillBlankStrategy = this.getFillBlankStrategy();
        if (value == null || value.isEmpty() || value.length() <= length) {
            return value;
        }
        if (fillBlankStrategy == null) {
            log.error("域[{}]类型为[{}], 当前值[{}]长度与目标不一致，需要设置填充方案",
                    this.getFieldIndex(), this.getFieldTypeValue().toString(), value);
            throw new Iso8583Exception("域[" + this.getFieldIndex() + "]类型为[" + this.getFieldTypeValue().toString() + "], " +
                    "当前值[" + value + "]的长度与目标[" + length + "]不一致，需要设置填充方案");
        }
        int valLen = value.length();
        if (fillBlankStrategy.isLeftAppend()) {
            return value.substring(valLen - length);
        } else {
            return value.substring(0, length - 1);
        }
    }


    private String append(String value) {
        Iso8583FillBlankStrategy fillBlankStrategy = this.getFillBlankStrategy();
        if (value == null) {
            return value;
        }
        int targetLength = valueBytesCount(value.length()) * 2;
        if (value.length() > targetLength) {
            log.error("域[{}]中注入的值[{}]长度超过设置[{}]", this.getFieldIndex(), value, fieldLength);
            throw new Iso8583Exception("域[" + this.getFieldIndex() + "]中注入的值[" + value + "]长度超过设置[" + fieldLength + "]");
        }

        if (value.length() < targetLength) {
            if (fillBlankStrategy == null) {
                log.error("域[{}]类型为[{}], 当前值[{}]长度与目标值不一致，需要设置填充方案",
                        this.getFieldIndex(), this.getFieldTypeValue().toString(), value);
                throw new Iso8583Exception("域[" + this.getFieldIndex() + "]类型为[" + this.getFieldTypeValue().toString() + "], " +
                        "当前值[" + value + "]的长度与目标[" + targetLength + "]不一致，需要设置填充方案");
            }
            log.debug("域[{}]类型为[{}], 当前值[{}]的长度与目标值[{}]不一致，根据填充方案isLeftAppend[{}],filledChar[{}]进行填充"
                    , this.getFieldIndex(), this.getFieldTypeValue().toString(), value.length(), targetLength
                    , fillBlankStrategy.isLeftAppend(), fillBlankStrategy.getValue());
            char c = fillBlankStrategy.getValue();
            while (value.length() != targetLength) {
                if (fillBlankStrategy.isLeftAppend()) {
                    value = c + value;
                } else {
                    value = value + c;
                }
            }
        }
        return value;
    }


    /**
     * <p>消息字段类型名称</p>
     * <p>用于约束字段名称的类型</p>
     *
     * @author Ajsgn@foxmail.com
     * @ClassName: Iso8583FieldTypeValue
     * @Description: 消息字段类型格式
     * @date 2017年3月23日 下午12:46:16
     */
    public enum FieldTypeValue {
        /**
         * <p>使用字符长度来描述BCD编码长度</p>
         * <p>使用BCD编码方式进行编码处理</p>
         */
        NUMERIC,
        /**
         * <p>字节长度来描述HEX编码，两个HEX字符表示一个字节</p>
         * <p>使用字节长度来描述HEX编码方式进行编码处理</p>
         * <p>暂未实现，用NUMERIC方式替代</p>
         */
        HEX,
        /**
         * <p>字符类型字段</p>
         * 使用ASCII编码方式编码
         */
        CHAR,
        /**
         * <p>1个字节长度表示的变长字段(表示字节长度)</p>
         * <p>使用ASCII编码方式编码</p>
         */
        LLVAR_CHAR,
        /**
         * <p>2个字节长度表示的变长字段(表示字节长度)</p>
         * <p>使用ASCII编码方式编码</p>
         */
        LLLVAR_CHAR,
        /**
         * <p>3个字节长度表示的变长字段(表示字节长度)</p>
         * <p>使用ASCII编码方式编码</p>
         */
        LLLLVAR_CHAR,
        /**
         * <p>1个字节长度表示的变长字段(表示字节长度)</p>
         * <p>使用HEX编码方式编码</p>
         */
        LLVAR_HEX,
        /**
         * <p>2个字节长度表示的变长字段(表示字节长度)</p>
         * <p>使用HEX编码方式编码</p>
         */
        LLLVAR_HEX,
        /**
         * <p>3个字节长度表示的变长字段(表示字节长度)</p>
         * <p>使用HEX编码方式编码</p>
         */
        LLLLVAR_HEX,
        /**
         * <p>3个字节长度表示的变长字段(表示字符长度)</p>
         * <p>使用BCD编码方式编码</p>
         */
        LLVAR_NUMERIC,
        /**
         * <p>2个字节长度表示的变长字段(表示字符长度)</p>
         * <p>使用BCD编码方式编码</p>
         */
        LLLVAR_NUMERIC,
        /**
         * <p>3个字节长度表示的变长字段(表示字符长度)</p>
         * <p>使用BCD编码方式编码</p>
         */
        LLLLVAR_NUMERIC
    }

}
