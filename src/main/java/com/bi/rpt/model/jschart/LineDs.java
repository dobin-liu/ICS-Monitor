package com.bi.rpt.model.jschart;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class LineDs {
    private String seriesname;
    private String color;
    private List<Data> data;
    private List<Data> category;

    public String getSeriesname() {
        return seriesname;
    }

    public void setSeriesname(String seriesname) {
        this.seriesname = seriesname;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

	public List<Data> getData() {
		return data;
	}

	public void setData(List<Data> data) {
		this.data = data;
	}

    public List<Data> getCategory() {
        return category;
    }

    public void setCategory(List<Data> category) {
        this.category = category;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
