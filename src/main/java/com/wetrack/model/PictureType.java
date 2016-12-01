package com.wetrack.model;

public enum PictureType {
    JPEG(".jpeg"), PNG(".png"), GIF(".gif");

    private final String suffix;

    PictureType(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() { return suffix; }
}
