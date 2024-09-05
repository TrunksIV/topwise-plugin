package com.topwise.plugin;

/**
 * @author caixh
 * @description
 * @date 2023/2/1 20:04
 */
public enum LedEnum {
    ALL(0),
    GREEN(1),
    YELLOW(2),
    RED(3),
    BLUE(4);

    private int value;

    LedEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static LedEnum light(int value) {
        LedEnum ledEnum = null;
        switch (value) {
            case 0:
                ledEnum = ALL;
                break;
            case 1:
                ledEnum = GREEN;
                break;
            case 2:
                ledEnum = YELLOW;
                break;
            case 3:
                ledEnum = RED;
                break;
            case 4:
                ledEnum = BLUE;
                break;
            default:
                throw new IllegalArgumentException("light " + value + " is not valid");
        }
        return ledEnum;
    }
}
