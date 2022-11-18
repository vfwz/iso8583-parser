package cn.vfwz;

import cn.vfwz.iso8583.message.DefaultMessageFactory;
import cn.vfwz.iso8583.message.Iso8583Message;
import cn.vfwz.iso8583.message.Iso8583MessageFactory;
import org.junit.Test;

public class UnionMessageTest {

    public static String PAY_REQUEST = "";
    public static String PAY_RESPONSE = "";

    @Test
    public void parsePayMessage() {
        Iso8583MessageFactory factory = DefaultMessageFactory.generateUnion();

        Iso8583Message requestMessage = factory.parse(PAY_REQUEST);
        System.out.println(requestMessage.toFormatString());
        Iso8583Message responseMessage = factory.parse(PAY_RESPONSE);
        System.out.println(responseMessage.toFormatString());
    }

}
