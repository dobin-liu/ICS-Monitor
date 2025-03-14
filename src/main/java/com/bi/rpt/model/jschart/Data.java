/*
 * @(#)Data.java Jun 15, 2016
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist., 
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.rpt.model.jschart;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author bLake
 * @version
 * 
 */

public class Data {

    private String label;
    private String value;
    private String link;
    private String text;
    private String showLabel;
    private String color;
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getShowLabel() {
        return showLabel;
    }

    public void setShowLabel(String showLabel) {
        this.showLabel = showLabel;
    }

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
