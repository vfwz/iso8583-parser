package cn.vfwz;

import cn.vfwz.iso8583.message.DefaultMessageConfig;
import cn.vfwz.iso8583.message.Message;
import cn.vfwz.iso8583.message.MessageDecoder;
import org.junit.Test;

public class UnionMessageTest {

    public static String PAY_REQUEST = "";
    public static String PAY_RESPONSE = "";

    @Test
    public void parsePayMessage() {
        MessageDecoder decoder = new MessageDecoder(DefaultMessageConfig.produceUnion());

        Message requestMessage = decoder.decode(PAY_REQUEST);
        System.out.println(requestMessage.toFormatString());
        Message responseMessage = decoder.decode(PAY_RESPONSE);
        System.out.println(responseMessage.toFormatString());
    }

}
