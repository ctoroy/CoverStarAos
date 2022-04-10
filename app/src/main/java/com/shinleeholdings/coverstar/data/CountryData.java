package com.shinleeholdings.coverstar.data;

public class CountryData {
    private String name = "";
    private String code = "";
    private String flagImageResId = "";

    public CountryData(String name, String code, String flagImageResId) {
        this.name = name;
        this.code = code;
        this.flagImageResId = flagImageResId;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getFlagImageResId() {
        return flagImageResId;
    }

}
