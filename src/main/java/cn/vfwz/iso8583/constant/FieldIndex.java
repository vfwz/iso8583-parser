package cn.vfwz.iso8583.constant;

public class FieldIndex {

    /**
     * 报文长度域
     */
    public static final int TOTAL_MESSAGE_LENGTH = -99;

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
     * <p>
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
    public static final int HEADER_LENGTH = -128;
    public static final int HEADER_FLAG_AND_VERSION = -127;
    /**
     * public static final int TOTAL_MESSAGE_LENGTH = -99; 银联整个报文长度不在报文开头
     **/
    public static final int DESTINATION_ID = -29;
    public static final int SOURCE_ID = -28;
    public static final int RESERVERD = -27;
    public static final int BATCH_NUMBER = -26;
    public static final int TRANS_INFO = -25;
    public static final int USER_INFO = -24;
    public static final int REJECT_CODE = -23;

    /**
     * 64域报文头
     */
    public static final int TPDU = -3;
    public static final int HEAD = -2;

    /**
     * 通用报文域
     */

    public static final int MTI = -1;

    public static final int BITMAP = 0;

    public static final int F2 = 2;

    public static final int F3 = 3;

    public static final int F4 = 4;

    public static final int F5 = 5;

    public static final int F6 = 6;

    public static final int F7 = 7;

    public static final int F8 = 8;

    public static final int F9 = 9;

    public static final int F10 = 10;

    public static final int F11 = 11;

    public static final int F12 = 12;

    public static final int F13 = 13;

    public static final int F14 = 14;

    public static final int F15 = 15;

    public static final int F16 = 16;

    public static final int F17 = 17;

    public static final int F18 = 18;

    public static final int F19 = 19;

    public static final int F20 = 20;

    public static final int F21 = 21;

    public static final int F22 = 22;

    public static final int F23 = 23;

    public static final int F24 = 24;

    public static final int F25 = 25;

    public static final int F26 = 26;

    public static final int F27 = 27;

    public static final int F28 = 28;

    public static final int F29 = 29;

    public static final int F30 = 30;

    public static final int F31 = 31;

    public static final int F32 = 32;

    public static final int F33 = 33;

    public static final int F34 = 34;

    public static final int F35 = 35;

    public static final int F36 = 36;

    public static final int F37 = 37;

    public static final int F38 = 38;

    public static final int F39 = 39;

    public static final int F40 = 40;

    public static final int F41 = 41;

    public static final int F42 = 42;

    public static final int F43 = 43;

    public static final int F44 = 44;

    public static final int F45 = 45;

    public static final int F46 = 46;

    public static final int F47 = 47;

    public static final int F48 = 48;

    public static final int F49 = 49;

    public static final int F50 = 50;

    public static final int F51 = 51;

    public static final int F52 = 52;

    public static final int F53 = 53;

    public static final int F54 = 54;

    public static final int F55 = 55;

    public static final int F56 = 56;

    public static final int F57 = 57;

    public static final int F58 = 58;

    public static final int F59 = 59;

    public static final int F60 = 60;

    public static final int F61 = 61;

    public static final int F62 = 62;

    public static final int F63 = 63;

    public static final int F64 = 64;

    public static final int F65 = 65;

    public static final int F66 = 66;

    public static final int F67 = 67;

    public static final int F68 = 68;

    public static final int F69 = 69;

    public static final int F70 = 70;

    public static final int F71 = 71;

    public static final int F72 = 72;

    public static final int F73 = 73;

    public static final int F74 = 74;

    public static final int F75 = 75;

    public static final int F76 = 76;

    public static final int F77 = 77;

    public static final int F78 = 78;

    public static final int F79 = 79;

    public static final int F80 = 80;

    public static final int F81 = 81;

    public static final int F82 = 82;

    public static final int F83 = 83;

    public static final int F84 = 84;

    public static final int F85 = 85;

    public static final int F86 = 86;

    public static final int F87 = 87;

    public static final int F88 = 88;

    public static final int F89 = 89;

    public static final int F90 = 90;

    public static final int F91 = 91;

    public static final int F92 = 92;

    public static final int F93 = 93;

    public static final int F94 = 94;

    public static final int F95 = 95;

    public static final int F96 = 96;

    public static final int F97 = 97;

    public static final int F98 = 98;

    public static final int F99 = 99;

    public static final int F100 = 100;

    public static final int F101 = 101;

    public static final int F102 = 102;

    public static final int F103 = 103;

    public static final int F104 = 104;

    public static final int F105 = 105;

    public static final int F106 = 106;

    public static final int F107 = 107;

    public static final int F108 = 108;

    public static final int F109 = 109;

    public static final int F110 = 110;

    public static final int F111 = 111;

    public static final int F112 = 112;

    public static final int F113 = 113;

    public static final int F114 = 114;

    public static final int F115 = 115;

    public static final int F116 = 116;

    public static final int F117 = 117;

    public static final int F118 = 118;

    public static final int F119 = 119;

    public static final int F120 = 120;

    public static final int F121 = 121;

    public static final int F122 = 122;

    public static final int F123 = 123;

    public static final int F124 = 124;

    public static final int F125 = 125;

    public static final int F126 = 126;

    public static final int F127 = 127;

    public static final int F128 = 128;


}
