package cn.vfwz.iso8583.message.tlv;

public class TLVObject {

    // Tag名称
    private final String tag;
    // Length, 数据长度
    private final int length;
    // Value, Hex格式的value数据
    private final String value;

    public TLVObject(String tag, int length, String value) {
        this.tag = tag;
        this.length = length;
        this.value = value;
    }

    public TLVObject(String tag, String value) {
        this.tag = tag;
        this.length = value.length()/2;
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public int getLength() {
        return length;
    }

    public String getValue() {
        return value;
    }
}
