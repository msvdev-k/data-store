package ru.msvdev.ds.server.utils.type;

public enum ValueType {

    NULL(1, ValueServiceBeanNames.NULL),
    INTEGER(2, ValueServiceBeanNames.INTEGER),
    DOUBLE(3, ValueServiceBeanNames.DOUBLE),
    BIG_DECIMAL(4, ValueServiceBeanNames.BIG_DECIMAL),
    STRING(5, ValueServiceBeanNames.STRING),
    TEXT(6, ValueServiceBeanNames.TEXT),
    DATE(7, ValueServiceBeanNames.DATE),
    DATETIME(8, ValueServiceBeanNames.DATETIME),
    BOOLEAN(9, ValueServiceBeanNames.BOOLEAN),
    BYTES(10, ValueServiceBeanNames.BYTES),
    UUID(11, ValueServiceBeanNames.UUID),
    JSON(12, ValueServiceBeanNames.JSON);


    public final int id;
    public final String serviceBeanName;

    ValueType(int id, String serviceBeanName) {
        this.id = id;
        this.serviceBeanName = serviceBeanName;
    }
}
