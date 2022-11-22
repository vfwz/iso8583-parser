package cn.vfwz;

import cn.vfwz.iso8583.constant.FieldIndex;
import cn.vfwz.iso8583.message.*;
import cn.vfwz.iso8583.message.field.Field;
import cn.vfwz.iso8583.message.field.FixedFieldType;
import cn.vfwz.iso8583.message.field.VariableFieldType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

import static cn.vfwz.iso8583.constant.FieldIndex.*;
import static cn.vfwz.iso8583.enumeration.FieldLengthType.LLLVAR;
import static cn.vfwz.iso8583.enumeration.FieldValueType.ASCII;
import static cn.vfwz.iso8583.enumeration.FieldValueType.BCD;

public class PosMessageTest {

    public static final String DOWNLOAD_REQUEST = "600003000060310031010008000000000000C408103130303136393139383433323930303435383230303042004750493034323034303230323035313430303030323430323130363837303030303030313038303856302E302E332E3004000000000000000011000000014200";
    public static final String DOWNLOAD_RESPONSE = "60000300006031003101000810001800000AE00114163829082330303030303030303030303030303130303136393139383433323930303435383230303042C4ABCEC4D5DCD3D0CFDEB9ABCBBE20202020202020202020202020202020202020202020202020200004B3C9B9A60011000000014200006439354638454530363337444143453935423232383235443533323630453744424238373633383230363334303343343736393843304131363334463446463231";
    public static final String SIGNIN_REQUEST = "600003000060310031010008000020000000C408120000783130303136393139383433323930303435383230303042004750493034323034303230323035313430303030323430323130363837303030303030313038303856302E302E332E30040000000000000000110000072600500003303120";
    public static final String SIGNIN_RESPONSE = "60000300006031003101000810003800010AC401140000781415440824080306290030303030303030303030303030303130303136393139383433323930303435383230303042004750493034323034303230323035313430303030323430323130363837303030303030313038303856302E302E332E300004B3C9B9A6001100000727011000571CD7E77C6ABCE8DE71C54E43103F3691A5827EE743CC37DD604094AF22746A4BD094F7D626E95138BC549EE4AA89E70E2CFA5B77D3F84D06F7";
    public static final String PAY_REQUEST = "60000300006031003101000200703C06C000C49A1716622424230000006900000000000000111100007914155408242903071000000012313030313639313938343332393030343538323030304200695049303634303430323032303531343030303032343032313036383730303030303031303630363030303036393037303844333930334633393038303856302E302E332E303135360000000000000000241000000000000001459F260846FD62985CAAE7589F2701809F101307011703A00000010A0100000500001EF41C469F37049536C9B89F36020C66950500000000009A032208249C01009F02060000000011115F2A02015682027C009F1A0201569F03060000000000009F330360E9C89F34030000009F3501229F1E0831323334353637388408A0000003330101029F090200309F4104000000010013220007270006000024FF02213436307C30307C32383638387C34333232323834390021534D303136CDC489E91786D0BE01F543D813611BCD3833353932373435";
    public static final String PAY_RESPONSE = "60000300006031003101000210703E02810EF08B31166224242300000069000000000000001111000079141556082400000824000000084843166558303030303430303237363136353038353230303130303136393139383433313636353538313230303041BAD3C4CFCAA1D2F8C2A1D0C5CFA2BCBCCAF5D3D0CFDEB9ABCBBE20202020202020202020202020202230333039202020202020203438343320202020202020313536240000000000000001459F260846FD62985CAAE7589F2701809F101307011703A00000010A0100000500001EF41C469F37049536C9B89F36020C66950500000000009A032208249C01009F02060000000011115F2A02015682027C009F1A0201569F03060000000000009F330360E9C89F34030000009F3501229F1E0831323334353637388408A0000003330101029F090200309F410400000001000730302BB3C9B9A6011445463031333336383734373437303733334132463246373036313739324536423634363232443734364132453633364636443246363833353644363537323633363836313645373445463032313843394138433245424338434644364134434345314339464442444531434245334236454500172200072700060007004246314136464335";
    public static final String SIGN_IMG_REQUEST = "600003000060310031010009205022000008C00A151662242423000000690000000011110000790824583030303034303032373631313030313639313938343332393030343538323030304204000000000000000050FF001ABAD3C4CFCAA1D2F8C2A1D0C5CFA2BCBCCAF5D3D0CFDEB9ABCBBEFF0104CFFBB7D1FF020101FF0607202208241415560008070007270126000001000000008000000040000000807F0000481CB93FCFF8E273061BDF301D76CB6869AEE9705A37872F81613C17DADB45AE6006D1C45C20399538F24115D37CA710009388652D49B88EA3FB843D29684CA53982D5AE3A1EA03191299C498B9F39DDA2825D982FD5C2312A115EFD6248CA1D9D8F140FB3AF676970FF023542334236433246";
    public static final String SIGN_IMG_RESPONSE = "60000300006031003101000930002000000AC00111000079583030303034303032373631303031303031363931393834333136363535383132303030410004B3C9B9A60008070007273839314333323442";

    private void checkMessageDecodeAndEncode(String messageHexOrigin) {
        // 解析源报文
        MessageConfig config = DefaultMessageConfig.produce();
        config.set(new VariableFieldType(F59, LLLVAR, ASCII)); // 59域是TLV应该直接存HEX的，多转了一道

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
    public void downloadRequestMessage() {
        checkMessageDecodeAndEncode(DOWNLOAD_REQUEST);
    }

    @Test
    public void downloadResponseMessage() {
        checkMessageDecodeAndEncode(DOWNLOAD_RESPONSE);
    }

    @Test
    public void signInRequestMessage() {
        checkMessageDecodeAndEncode(SIGNIN_REQUEST);
    }

    @Test
    public void signInResponseMessage() {
        checkMessageDecodeAndEncode(SIGNIN_RESPONSE);
    }

    @Test
    public void payRequestMessage() {
        checkMessageDecodeAndEncode(PAY_REQUEST);
    }

    @Test
    public void payResponseMessage() {
        checkMessageDecodeAndEncode(PAY_RESPONSE);
    }

    @Test
    public void subFieldDecode() {
        MessageConfig config = DefaultMessageConfig.produce();
        // 2200072700060
// ----------- 换一种实现思路
//        BcdSubMessageDecoder decoder = new BcdSubMessageDecoder();
//        decoder.set(new FixedFieldType(1, 2, BCD))
//                .set(new FixedFieldType(2, 1, BCD))
//                .set(new FixedFieldType(3, 1, BCD));
//        config.getFieldType(F22).setSubMessageFactory(decoder);
//
//        BcdSubMessageDecoder f60SubMessageFactory = new BcdSubMessageDecoder();
//        f60SubMessageFactory.set(new FixedFieldType(1, 1, BCD))
//                .set(new FixedFieldType(2, 2, BCD))
//                .set(new FixedFieldType(3, 3, BCD))
//                .set(new FixedFieldType(4, 7, BCD).setSubMessageFactory(decoder));
//        config.getFieldType(F60).setSubMessageFactory(f60SubMessageFactory);
//
//        Message message = config.decode(PAY_RESPONSE);
//        System.out.println(message.toFormatString());
    }

    @Test
    public void encodePayRequestMessage() {
        MessageConfig config = DefaultMessageConfig.produce();
        config.set(new VariableFieldType(F59, LLLVAR, ASCII)); // 59域是TLV应该直接存HEX的，多转了一道

//        Iso8583Message requestMessage = config.parseWithoutMsgLength(PAY_REQUEST);
        MessageEncoder builder = new MessageEncoder(config);
        builder.setField(FieldIndex.TPDU, "6000030000");
        builder.setField(FieldIndex.HEAD, "603100310100");
        builder.setField(FieldIndex.MTI, "0200");
        builder.setField(FieldIndex.F2, "6224242300000069");
        builder.setField(FieldIndex.F3, "000000");
        builder.setField(FieldIndex.F4, "12");
        builder.setField(FieldIndex.F11, "000079");
        builder.setField(FieldIndex.F12, "141556");
        builder.setField(FieldIndex.F13, "0824");
        builder.setField(FieldIndex.F14, "2903");
        builder.setField(F22, "071");
        builder.setField(FieldIndex.F23, "001");
        builder.setField(FieldIndex.F25, "00");
        builder.setField(FieldIndex.F26, "12");
        builder.setField(FieldIndex.F41, "10016919");
        builder.setField(FieldIndex.F42, "84329004582000B");
        builder.setField(FieldIndex.F46, "PI06404020205140000240210687000000106060000690708D3903F390808V0.0.3.0");
        builder.setField(FieldIndex.F49, "156");
        builder.setField(FieldIndex.F52, "0000000000000000");
        builder.setField(FieldIndex.F53, "2410000000000000");
        builder.setField(FieldIndex.F55, "9F260846FD62985CAAE7589F2701809F101307011703A00000010A0100000500001EF41C469F37049536C9B89F36020C66950500000000009A032208249C01009F02060000000011115F2A02015682027C009F1A0201569F03060000000000009F330360E9C89F34030000009F3501229F1E0831323334353637388408A0000003330101029F090200309F410400000001");
        builder.setField(FieldIndex.F60, "2200072700060");
        builder.setField(FieldIndex.F62, "FF02213436307C30307C32383638387C3433323232383439");
        builder.setField(FieldIndex.F63, "534D303136CDC489E91786D0BE01F543D813611BCD");
        builder.setField(FieldIndex.F64, "3833353932373435");
        Message requestMessage = builder.encode();

        System.out.println(requestMessage.toFormatString());
        String requestHex = requestMessage.getHexString();
        System.out.println(requestHex);

        MessageDecoder decoder = new MessageDecoder(config);
        Message requestMessage2 = decoder.decodeWithMsgLength(requestHex);
        System.out.println(requestMessage2.getHexString());
        System.out.println(requestMessage2.toFormatString());
    }


    @Test
    public void encodePayResponseMessage() {
        MessageConfig config = DefaultMessageConfig.produce();
        config.set(new VariableFieldType(F59, LLLVAR, ASCII)); // 59域是TLV应该直接存HEX的，多转了一道

//        Iso8583Message requestMessage = factory.parseWithoutMsgLength(PAY_REQUEST);
        MessageEncoder encoder = new MessageEncoder(config);
        encoder.setField(FieldIndex.TPDU, "6000030000");
        encoder.setField(FieldIndex.HEAD, "603100310100");
        encoder.setField(FieldIndex.MTI, "0210");
        encoder.setField(FieldIndex.F2, "6224242300000069");
        encoder.setField(FieldIndex.F3, "000000");
        encoder.setField(FieldIndex.F4, "1111");
        encoder.setField(FieldIndex.F11, "000079");
        encoder.setField(FieldIndex.F12, "141556");
        encoder.setField(FieldIndex.F13, "0824");
        encoder.setField(FieldIndex.F14, "2903");
        encoder.setField(FieldIndex.F23, "000");
        encoder.setField(FieldIndex.F25, "00");
        encoder.setField(FieldIndex.F26, "12");
        encoder.setField(FieldIndex.F32, "48431665");
        encoder.setField(FieldIndex.F37, "X00004002761");
        encoder.setField(FieldIndex.F38, "650852");
        encoder.setField(FieldIndex.F39, "00");
        encoder.setField(FieldIndex.F41, "10016919");
        encoder.setField(FieldIndex.F42, "84316655812000A");
        encoder.setField(FieldIndex.F43, "河南省银隆信息技术有限公司");
        encoder.setField(FieldIndex.F44, "0309       4843       ");
        encoder.setField(FieldIndex.F49, "156");
        encoder.setField(FieldIndex.F53, "2400000000000000");
        encoder.setField(FieldIndex.F55, "9F260846FD62985CAAE7589F2701809F101307011703A00000010A0100000500001EF41C469F37049536C9B89F36020C66950500000000009A032208249C01009F02060000000011115F2A02015682027C009F1A0201569F03060000000000009F330360E9C89F34030000009F3501229F1E0831323334353637388408A0000003330101029F090200309F410400000001");
        encoder.setField(FieldIndex.F56, "00+成功");
        encoder.setField(FieldIndex.F59, "EF013368747470733A2F2F7061792E6B64622D746A2E636F6D2F68356D65726368616E74EF0218C9A8C2EBC8CFD6A4CCE1C9FDBDE1CBE3B6EE");
        encoder.setField(FieldIndex.F60, "22000727000600070");
        encoder.setField(FieldIndex.F64, "4246314136464335");
        Message responseMessage = encoder.encode();

        System.out.println(responseMessage.toFormatString());
        String responseHex = responseMessage.getHexString();
        System.out.println(responseHex);

        MessageDecoder decoder = new MessageDecoder(config);
        Message responseMessage2 = decoder.decodeWithMsgLength(responseHex);

        String responseHex2 = responseMessage2.getHexString();
        System.out.println(responseHex2);
        System.out.println(responseMessage2.toFormatString());

        Assert.assertEquals(responseHex, responseHex2);
    }


    @Test
    public void encodeEmptyMessage() {
        MessageConfig config = DefaultMessageConfig.produce();
        config.set(new VariableFieldType(F59, LLLVAR, ASCII)); // 59域是TLV应该直接存HEX的，多转了一道

//        Iso8583Message requestMessage = factory.parseWithoutMsgLength(PAY_REQUEST);
        MessageEncoder builder = new MessageEncoder(config);
        builder.setField(FieldIndex.TPDU, "");
        builder.setField(FieldIndex.HEAD, "");
        builder.setField(FieldIndex.MTI, "");
        builder.setField(FieldIndex.F2, "");
        builder.setField(FieldIndex.F3, "");
        builder.setField(FieldIndex.F4, "");
        builder.setField(FieldIndex.F11, "");
        builder.setField(FieldIndex.F12, "");
        builder.setField(FieldIndex.F13, "");
        builder.setField(FieldIndex.F14, "");
        builder.setField(FieldIndex.F23, "");
        builder.setField(FieldIndex.F25, "");
        builder.setField(FieldIndex.F26, "");
        builder.setField(FieldIndex.F32, "");
        builder.setField(FieldIndex.F37, "");
        builder.setField(FieldIndex.F38, "");
        builder.setField(FieldIndex.F39, "");
        builder.setField(FieldIndex.F41, "");
        builder.setField(FieldIndex.F42, "");
        builder.setField(FieldIndex.F43, "");
        builder.setField(FieldIndex.F44, "");
        builder.setField(FieldIndex.F49, "");
        builder.setField(FieldIndex.F53, "");
        builder.setField(FieldIndex.F55, "");
        builder.setField(FieldIndex.F56, "");
        builder.setField(FieldIndex.F59, "");
        builder.setField(FieldIndex.F60, "");
        builder.setField(FieldIndex.F64, "");
        Message responseMessage = builder.encode();

        System.out.println(responseMessage.toFormatString());
        String responseHex = responseMessage.getHexString();
        System.out.println(responseHex);

        MessageDecoder decoder = new MessageDecoder(config);
        Message responseMessage2 = decoder.decodeWithMsgLength(responseHex);

        String responseHex2 = responseMessage2.getHexString();
        System.out.println(responseHex2);
        System.out.println(responseMessage2.toFormatString());

        Assert.assertEquals(responseHex, responseHex2);
    }


}
