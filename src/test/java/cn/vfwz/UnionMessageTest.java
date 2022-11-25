package cn.vfwz;

import cn.vfwz.iso8583.message.*;
import cn.vfwz.iso8583.message.field.Field;
import cn.vfwz.iso8583.message.field.VariableFieldType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

import static cn.vfwz.iso8583.constant.FieldIndex.F59;
import static cn.vfwz.iso8583.enumeration.FieldLengthType.LLLVAR;
import static cn.vfwz.iso8583.enumeration.FieldValueType.ASCII;

public class UnionMessageTest {

    public static String PAY_REQUEST = "";
    public static String PAY_RESPONSE = "";

    private void checkMessageDecodeAndEncode(String messageHexOrigin) {
        // 解析源报文
        MessageConfig config = DefaultMessageConfig.produceUnion();

        MessageDecoder decoder = new MessageDecoder(config);

        Message requestMessage = decoder.decode(messageHexOrigin);
        System.out.println(requestMessage.toFormatString());
        System.out.println("origin:" + messageHexOrigin);

        // 根据解析的报文再组装报文
        MessageEncoder encoder2 = new MessageEncoder(config);
        Iterator<Field> fieldIterator = requestMessage.getFieldIterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            encoder2.setField(field.getIndex(), field.getValue());
        }
        Message requestMessageAfter = encoder2.encode();
        String messageHexAfter = requestMessageAfter.getHexString();
        System.out.println(" after:" + messageHexAfter);
        System.out.println(requestMessageAfter.toFormatString());

        // 组装后报文与实际报文应该相同
        Assert.assertEquals(messageHexOrigin, messageHexAfter.substring(4));


        // 再解析一遍组装后的报文
        Message requestMessageThird = decoder.decodeWithMsgLength(messageHexAfter);
        String messageHexThird = requestMessageThird.getHexString();
        System.out.println(requestMessageThird.toFormatString());
        System.out.println(" third:" + messageHexThird);

        Assert.assertEquals(messageHexOrigin, messageHexThird.substring(4));


    }

    @Test
    public void checkRequest() {
        checkMessageDecodeAndEncode(PAY_REQUEST);
    }

    @Test
    public void checkResponse() {
        checkMessageDecodeAndEncode(PAY_REQUEST);
    }

    @Test
    public void parsePayMessage() {
        MessageDecoder decoder = new MessageDecoder(DefaultMessageConfig.produceUnion());

        Message requestMessage = decoder.decode(PAY_REQUEST);
        System.out.println(requestMessage.toFormatString());
        Message responseMessage = decoder.decode(PAY_RESPONSE);
        System.out.println(responseMessage.toFormatString());
    }

}
