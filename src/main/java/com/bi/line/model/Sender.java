package com.bi.line.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sender {
    private String name;
    @JsonProperty(value = "iconUrl")
    private String iconUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
