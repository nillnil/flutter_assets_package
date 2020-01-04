package com.nillnil.assets;

/**
 * @author WuHongcheng
 * 2020/1/4.
 */
public enum AssetsRatio {
    ONE("@1x", ""),
    ONE_HELF("@1.5x", "1.5x"),
    TWO("@2x", "2.0x"),
    TWO_HELF("@2.5x", "2.5x"),
    THREE("@3x", "3.0x"),
    THREE_HELF("@3.5x", "3.5x");

    AssetsRatio(String sign, String value) {
        this.sign = sign;
        this.value = value;
    }
    private String sign;
    private String value;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
