package com.wetrack.test;

public class QueryParam {
    private String name;
    private String value;

    public static QueryParam of(String name, String value) {
        return new QueryParam(name, value);
    }

    public QueryParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
