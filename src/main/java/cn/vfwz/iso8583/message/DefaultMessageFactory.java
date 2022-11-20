package cn.vfwz.iso8583.message;

import cn.vfwz.iso8583.enumeration.AlignType;
import cn.vfwz.iso8583.exception.Iso8583Exception;
import cn.vfwz.iso8583.message.field.FixedFieldType;
import cn.vfwz.iso8583.message.field.VariableFieldType;

import static cn.vfwz.iso8583.constant.FieldIndex.*;
import static cn.vfwz.iso8583.enumeration.FieldLengthType.*;
import static cn.vfwz.iso8583.enumeration.FieldValueType.*;


/**
 * 默认报文工厂
 */
public class DefaultMessageFactory {

    public static MessageFactory produce() {
        MessageFactory factory = new MessageFactory();
        factory.set(new FixedFieldType(TOTAL_MESSAGE_LENGTH, 2, HEX, AlignType.RIGHT))
                .set(new FixedFieldType(TPDU, 10, BCD))
                .set(new FixedFieldType(HEAD, 12, BCD))
                .set(new FixedFieldType(MTI, 4, BCD))
                .set(new FixedFieldType(BITMAP, 8, HEX))
                .set(new VariableFieldType(F2, LLVAR, BCD))
                .set(new FixedFieldType(F3, 6, BCD))
                .set(new FixedFieldType(F4, 12, BCD, AlignType.RIGHT))
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
                .set(new FixedFieldType(F43, 40, ASCII, AlignType.LEFT, ' '))
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

    public static MessageFactory produceUnion() {
        MessageFactory factory = new MessageFactory(128);
        /**
         * 银联报文头的基本组成
         * Field1 头长度（Header Length） 1
         * Field2 头标识和版本号（Header Flag and Version） 1
         * Field3 整个报文长度（Total Message Length） 4
         * Field4 目的 ID（Destination ID） 11
         * Field5 源 ID（Source ID） 11
         * Field6 保留使用（Reserved for Use） 3
         * Field7 批次号（Batch Number） 1
         * Field8 交易信息（Transaction Information） 8
         * Field9 用户信息（User Information） 1
         * Field10 拒绝码（Reject Code） 5
         * 2E
         * 02
         * 30333239
         * 3438343330303030202020
         * 3030303130303030202020
         * 303030
         * 01
         * 3030303030303030
         * 30
         * 3030303030
         */

        factory.set(new FixedFieldType(HEADER_LENGTH, 1, HEX))
                .set(new FixedFieldType(HEADER_FLAG_AND_VERSION, 1, BCD))
                .set(new FixedFieldType(TOTAL_MESSAGE_LENGTH, 4, ASCII))
                .set(new FixedFieldType(DESTINATION_ID, 11, ASCII))
                .set(new FixedFieldType(SOURCE_ID, 11, ASCII))
                .set(new FixedFieldType(RESERVERD, 3, ASCII))
                .set(new FixedFieldType(BATCH_NUMBER, 1, BCD))
                .set(new FixedFieldType(TRANS_INFO, 8, ASCII))
                .set(new FixedFieldType(USER_INFO, 1, ASCII))
                .set(new FixedFieldType(REJECT_CODE, 5, ASCII))

                .set(new FixedFieldType(MTI, 4, ASCII))
                .set(new FixedFieldType(BITMAP, 16, HEX))
                .set(new VariableFieldType(F2, LLVAR_ASCII, ASCII))
                .set(new FixedFieldType(F3, 6, ASCII))
                .set(new FixedFieldType(F4, 12, ASCII))
                .set(new FixedFieldType(F5, 12, ASCII))
                .set(new FixedFieldType(F6, 12, ASCII))
                .set(new FixedFieldType(F7, 10, ASCII))
                .set(new FixedFieldType(F9, 8, ASCII))
                .set(new FixedFieldType(F10, 8, ASCII))
                .set(new FixedFieldType(F11, 6, ASCII))
                .set(new FixedFieldType(F12, 6, ASCII))
                .set(new FixedFieldType(F13, 4, ASCII))
                .set(new FixedFieldType(F14, 4, ASCII))
                .set(new FixedFieldType(F15, 4, ASCII))
                .set(new FixedFieldType(F16, 4, ASCII))
                .set(new FixedFieldType(F18, 4, ASCII))
                .set(new FixedFieldType(F19, 3, ASCII))
                .set(new FixedFieldType(F22, 3, ASCII))
                .set(new FixedFieldType(F23, 3, ASCII))
                .set(new FixedFieldType(F25, 2, ASCII))
                .set(new FixedFieldType(F26, 2, ASCII))
                .set(new FixedFieldType(F28, 9, ASCII))
                .set(new VariableFieldType(F32, LLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F33, LLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F35, LLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F36, LLLVAR_ASCII, ASCII))
                .set(new FixedFieldType(F37, 12, ASCII))
                .set(new FixedFieldType(F38, 6, ASCII))
                .set(new FixedFieldType(F39, 2, ASCII))
                .set(new FixedFieldType(F41, 8, ASCII))
                .set(new FixedFieldType(F42, 15, ASCII))
                .set(new FixedFieldType(F43, 40, ASCII, AlignType.LEFT, ' '))
                .set(new VariableFieldType(F44, LLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F45, LLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F48, LLLVAR_ASCII, ASCII))
                .set(new FixedFieldType(F49, 3, ASCII))
                .set(new FixedFieldType(F50, 3, ASCII))
                .set(new FixedFieldType(F51, 3, ASCII))
                .set(new FixedFieldType(F52, 8, HEX))
                .set(new FixedFieldType(F53, 16, ASCII))
                .set(new VariableFieldType(F54, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F55, LLLVAR_ASCII, HEX))
                .set(new VariableFieldType(F56, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F57, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F58, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F59, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F60, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F61, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F62, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F63, LLLVAR_ASCII, ASCII))
                .set(new FixedFieldType(F66, 1, ASCII))
                .set(new FixedFieldType(F70, 3, ASCII))
                .set(new FixedFieldType(F74, 10, ASCII))
                .set(new FixedFieldType(F75, 10, ASCII))
                .set(new FixedFieldType(F76, 10, ASCII))
                .set(new FixedFieldType(F77, 10, ASCII))
                .set(new FixedFieldType(F78, 10, ASCII))
                .set(new FixedFieldType(F79, 10, ASCII))
                .set(new FixedFieldType(F80, 10, ASCII))
                .set(new FixedFieldType(F81, 10, ASCII))
                .set(new FixedFieldType(F82, 16, ASCII))
                .set(new FixedFieldType(F84, 16, ASCII))
                .set(new FixedFieldType(F86, 16, ASCII))
                .set(new FixedFieldType(F87, 16, ASCII))
                .set(new FixedFieldType(F88, 16, ASCII))
                .set(new FixedFieldType(F89, 16, ASCII))
                .set(new FixedFieldType(F90, 42, ASCII))
                .set(new FixedFieldType(F95, 42, ASCII))
                .set(new FixedFieldType(F96, 8, HEX))
                .set(new FixedFieldType(F97, 17, ASCII))
                .set(new VariableFieldType(F99, LLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F100, LLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F102, LLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F103, LLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F104, LLLVAR_ASCII, HEX))
                .set(new VariableFieldType(F113, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F116, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F117, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F121, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F122, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F123, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F125, LLLVAR_ASCII, ASCII))
                .set(new VariableFieldType(F126, LLLVAR_ASCII, ASCII))
                .set(new FixedFieldType(F128, 8, ASCII));
        return factory;
    }

    public static MessageFactory produce(int fieldsCount) {
        // 64域POS报文
        if (fieldsCount == 64) {
            return produce();
        }
        // 128域银联报文
        else if (fieldsCount == 128) {
            return produceUnion();
        } else {
            throw new Iso8583Exception("暂不支持的域长度报文:" + fieldsCount);
        }
    }


}
