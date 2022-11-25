package cn.vfwz;

import cn.vfwz.iso8583.message.tlv.TLV;
import cn.vfwz.iso8583.message.tlv.TLVObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@Slf4j
public class TLVTest {

    public static final String TLV_MESSAGE = "9F260846FD62985CAAE7589F2701809F101307011703A00000010A0100000500001EF41C469F37049536C9B89F36020C66950500000000009A032208249C01009F02060000000011115F2A02015682027C009F1A0201569F03060000000000009F330360E9C89F34030000009F3501229F1E0831323334353637388408A0000003330101029F090200309F410400000001";


    @Test
    public void decodeTest() {
        List<TLVObject> parsedList = TLV.parse(TLV_MESSAGE);
        parsedList.forEach(obj -> log.info("[{}][{}][{}]", obj.getTag(), obj.getLength(), obj.getValue()));
    }

    @Test
    public void encodeTest() {
        List<TLVObject> parsedList = TLV.parse(TLV_MESSAGE);
        parsedList.forEach(obj -> log.info("[{}][{}][{}]", obj.getTag(), obj.getLength(), obj.getValue()));

        String encodeTlv = TLV.toHexString(parsedList);

        log.info("重新encode tlv: {}", encodeTlv);

        Assert.assertEquals(encodeTlv, TLV_MESSAGE);

        List<TLVObject> parsedList2 = TLV.parse(encodeTlv);
        parsedList2.forEach(obj -> log.info("[{}][{}][{}]", obj.getTag(), obj.getLength(), obj.getValue()));
//        Assert.assertEquals(encodeTlv, TLV_MESSAGE);
    }

    @Test
    public void decode2() {
        String s = "9F2608AAA4D321391F16849F2701809F10160706A203206E00010DA11111111100001509071900809F3704B983E6289F36020004950500000000009A031605189C01009F02060000000000015F2A020156820200409F1A0201569F03060000000000009F3303E0E9C89F34030000009F3501229F1E0831323320202020208408F0000006660101019F090202009F631031343239333330300020040000000000";

        List<TLVObject> parsedList = TLV.parse(s);
        parsedList.forEach(obj -> log.info("[{}][{}][{}]", obj.getTag(), obj.getLength(), obj.getValue()));
    }
}
