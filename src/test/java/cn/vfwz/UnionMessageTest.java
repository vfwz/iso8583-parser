package cn.vfwz;

import cn.vfwz.iso8583.message.DefaultMessageFactory;
import cn.vfwz.iso8583.message.Message;
import cn.vfwz.iso8583.message.MessageFactory;
import org.junit.Test;

public class UnionMessageTest {

    public static String PAY_REQUEST = "";
    public static String PAY_RESPONSE = "";

    @Test
    public void parsePayMessage() {
        MessageFactory factory = DefaultMessageFactory.produceUnion();

        Message requestMessage = factory.parse(PAY_REQUEST);
        System.out.println(requestMessage.toFormatString());
        Message responseMessage = factory.parse(PAY_RESPONSE);
        System.out.println(responseMessage.toFormatString());
    }

}
