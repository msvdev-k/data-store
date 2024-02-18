package ru.msvdev.ds.server.utils.type;

public enum ConstantValue {

    NULL(-1),
    FALSE(0),
    TRUE(1);


    public final long id;

    ConstantValue(long id) {
        this.id = id;
    }
}
