package com.topwise.plugin;

/**
 * @author caixh
 * @description
 * @date 2023/4/13 17:44
 */
public enum ImageType {
    ASSET(0),
    SDCARD(1),
    QRCODE(2),
    BARCODE(3),
    NET(4),;
    private int value;

    ImageType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static ImageType type(int value) {
        ImageType type = null;
        switch (value) {
            case 0:
                type = ASSET;
                break;
            case 1:
                type = SDCARD;
                break;
            case 2:
                type = QRCODE;
                break;
            case 3:
                type = BARCODE;
                break;
            case 4:
                type = NET;
                break;
            default:
                throw new IllegalArgumentException("light " + value + " is not valid");
        }
        return type;
    }
}
