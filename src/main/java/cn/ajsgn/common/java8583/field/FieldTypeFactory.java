package cn.ajsgn.common.java8583.field;

import static cn.ajsgn.common.java8583.field.Iso8583FieldType.FieldTypeValue.NUMERIC;

public class FieldTypeFactory {

    public static Iso8583FieldType fixedLengthNumeric(int length) {
        return new Iso8583FieldType(NUMERIC, length);
    }

    public static Iso8583FieldType fixedLengthField(Iso8583FieldType.FieldTypeValue fieldType, int length) {
        return new Iso8583FieldType(fieldType, length);
    }

    public static Iso8583FieldType fixedLengthField(Iso8583FieldType.FieldTypeValue fieldType, int length, Iso8583FillBlankStrategy strategy) {
        return new Iso8583FieldType(fieldType, length).setFillBlankStrategy(strategy);
    }

    public static Iso8583FieldType variableLengthField(Iso8583FieldType.FieldTypeValue fieldType) {
        return new Iso8583FieldType(fieldType, 0);
    }

    public static Iso8583FieldType variableLengthField(Iso8583FieldType.FieldTypeValue fieldType, Iso8583FillBlankStrategy strategy) {
        return new Iso8583FieldType(fieldType, 0).setFillBlankStrategy(strategy);
    }
}
