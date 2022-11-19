package cn.vfwz.iso8583.exception;

/**
 * 该工具类统一业务报错类型
 */
public class Iso8583Exception extends RuntimeException {

    public Iso8583Exception() {
        super();
    }

    public Iso8583Exception(String message) {
        super(message);
    }

    public Iso8583Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public Iso8583Exception(Throwable cause) {
        super(cause);
    }
}
