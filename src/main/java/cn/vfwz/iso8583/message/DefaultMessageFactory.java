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
package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.message.field.FixedFieldType;
import cn.vfwz.iso8583.message.field.VariableFieldType;

import static cn.vfwz.iso8583.constant.FieldIndex.*;
import static cn.vfwz.iso8583.enumeration.FieldDataType.*;
import static cn.vfwz.iso8583.enumeration.FieldLengthType.LLLVAR;
import static cn.vfwz.iso8583.enumeration.FieldLengthType.LLVAR;


/**
 * 默认消息工厂
 * 实际使用时，在项目中只需要生成一个，不用每次都生成
 */
public class DefaultMessageFactory {

    public static Iso8583MessageFactory generate() {
        Iso8583MessageFactory factory = new Iso8583MessageFactory();

        factory.set(new FixedFieldType(MSG_LENGTH, 2, HEX))
                .set(new FixedFieldType(TPDU, 10, BCD))
                .set(new FixedFieldType(HEAD, 12, BCD))
                .set(new FixedFieldType(MTI, 4, BCD))
                .set(new FixedFieldType(BITMAP, 16, BCD))
                .set(new FixedFieldType(F3, 6, BCD))
                .set(new FixedFieldType(F4, 6, BCD, AlignType.RIGHT))
                .set(new FixedFieldType(F11, 6, BCD))
                .set(new FixedFieldType(F12, 6, BCD))
                .set(new FixedFieldType(F13, 4, BCD))
                .set(new FixedFieldType(F14, 4, BCD))
                .set(new FixedFieldType(F15, 4, BCD))
                .set(new FixedFieldType(F22, 3, BCD, AlignType.LEFT))
                .set(new FixedFieldType(F23, 3, BCD, AlignType.RIGHT))
                .set(new FixedFieldType(F25, 2, BCD))
                .set(new FixedFieldType(F26, 2, BCD))
                .set(new VariableFieldType(F32, LLVAR, BCD, AlignType.LEFT))
                .set(new VariableFieldType(F34, LLLVAR, HEX))
                .set(new VariableFieldType(F35, LLLVAR, HEX))
                .set(new VariableFieldType(F36, LLLVAR, HEX))
                .set(new FixedFieldType(F37, 12, ASCII))
                .set(new FixedFieldType(F38, 6, ASCII))
                .set(new FixedFieldType(F39, 2, ASCII))
                .set(new FixedFieldType(F41, 8, ASCII))
                .set(new FixedFieldType(F42, 15, ASCII))
                .set(new FixedFieldType(F43, 40, ASCII))
                .set(new VariableFieldType(F44, LLVAR, ASCII))
                .set(new VariableFieldType(F46, LLLVAR, ASCII))
                .set(new VariableFieldType(F47, LLLVAR, HEX))
                .set(new VariableFieldType(F48, LLLVAR, HEX))
                .set(new FixedFieldType(F49, 3, ASCII))
                .set(new FixedFieldType(F52, 8, HEX))
                .set(new FixedFieldType(F53, 16, BCD))
                .set(new VariableFieldType(F54, LLLVAR, ASCII))
                .set(new VariableFieldType(F55, LLLVAR, HEX))
                .set(new VariableFieldType(F56, LLLVAR, ASCII))
                .set(new VariableFieldType(F58, LLLVAR, ASCII))
                .set(new VariableFieldType(F59, LLLVAR, HEX))
                .set(new VariableFieldType(F60, LLLVAR, BCD, AlignType.LEFT))
                .set(new VariableFieldType(F61, LLLVAR, BCD))
                .set(new VariableFieldType(F62, LLLVAR, HEX))
                .set(new VariableFieldType(F63, LLLVAR, HEX))
                .set(new FixedFieldType(F64, 8, HEX));
        return factory;
    }


}
