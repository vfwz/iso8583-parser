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
package cn.ajsgn.common.java8583.core;

import cn.ajsgn.common.java8583.field.Iso8583DataHeader;

import java.nio.charset.Charset;

import static cn.ajsgn.common.java8583.field.FieldTypeFactory.fixedLengthField;
import static cn.ajsgn.common.java8583.field.FieldTypeFactory.variableLengthField;
import static cn.ajsgn.common.java8583.field.Iso8583FieldType.FieldTypeValue.*;
import static cn.ajsgn.common.java8583.field.Iso8583FillBlankStrategy.DEFAULT_LEFT_APPEND_STRATETY;
import static cn.ajsgn.common.java8583.field.Iso8583FillBlankStrategy.DEFAULT_RIGHT_APPEND_STRATETY;

/**
 * 默认消息工厂
 * 实际使用时，在项目中只需要生成一个，不用每次都生成
 */
public class DefaultMessageFactory {

    public static Iso8583MessageFactory generate() {
        Iso8583DataHeader dataHeaderType = new Iso8583DataHeader(
                fixedLengthField(NUMERIC, 10),
                fixedLengthField(NUMERIC, 12),
                fixedLengthField(NUMERIC, 4),
                fixedLengthField(NUMERIC, 16));
        Iso8583MessageFactory factory = new Iso8583MessageFactory(2, false, Charset.forName("GBK"), dataHeaderType);

        factory.set(2, variableLengthField(LLVAR_NUMERIC, DEFAULT_RIGHT_APPEND_STRATETY))
                .set(3, fixedLengthField(NUMERIC, 6))
                .set(4, fixedLengthField(NUMERIC, 12, DEFAULT_LEFT_APPEND_STRATETY))
                .set(11, fixedLengthField(NUMERIC, 6))
                .set(12, fixedLengthField(NUMERIC, 6))
                .set(13, fixedLengthField(NUMERIC, 4))
                .set(14, fixedLengthField(NUMERIC, 4))
                .set(15, fixedLengthField(NUMERIC, 4))
                .set(22, fixedLengthField(NUMERIC, 3, DEFAULT_RIGHT_APPEND_STRATETY))
                .set(23, fixedLengthField(NUMERIC, 3, DEFAULT_LEFT_APPEND_STRATETY))
                .set(25, fixedLengthField(NUMERIC, 2))
                .set(26, fixedLengthField(NUMERIC, 2))
                .set(32, variableLengthField(LLVAR_NUMERIC, DEFAULT_RIGHT_APPEND_STRATETY))
                .set(34, variableLengthField(LLLVAR_HEX))
                .set(35, variableLengthField(LLLVAR_HEX))
                .set(36, variableLengthField(LLLVAR_HEX))
                .set(37, fixedLengthField(CHAR, 12))
                .set(38, fixedLengthField(CHAR, 6))
                .set(39, fixedLengthField(CHAR, 2))
                .set(41, fixedLengthField(CHAR, 8))
                .set(42, fixedLengthField(CHAR, 15))
                .set(43, fixedLengthField(CHAR, 40))
                .set(44, variableLengthField(LLVAR_CHAR))
                .set(46, variableLengthField(LLLVAR_CHAR))
                .set(47, variableLengthField(LLLVAR_HEX))
                .set(48, variableLengthField(LLLVAR_HEX))
                .set(49, fixedLengthField(CHAR, 3))
                .set(52, fixedLengthField(HEX, 8))
                .set(53, fixedLengthField(NUMERIC, 16))
                .set(54, variableLengthField(LLLVAR_CHAR))
                .set(55, variableLengthField(LLLVAR_HEX))
                .set(56, variableLengthField(LLLVAR_CHAR))
                .set(58, variableLengthField(LLLVAR_CHAR))
                .set(59, variableLengthField(LLLVAR_HEX))
                .set(60, variableLengthField(LLLVAR_NUMERIC, DEFAULT_RIGHT_APPEND_STRATETY))
                .set(61, variableLengthField(LLLVAR_NUMERIC))
                .set(62, variableLengthField(LLLVAR_HEX))
                .set(63, variableLengthField(LLLVAR_HEX))
                .set(64, fixedLengthField(HEX, 8));
        return factory;
    }


}
