package com.bi.line.model;

public class Message {
	private String type;
	private String originalContentUrl;
	private String previewImageUrl;
	private String text;
	private String packageId;
	private String stickerId;
    private int duration;
    private Sender sender;
    
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOriginalContentUrl() {
		return originalContentUrl;
	}

	public void setOriginalContentUrl(String originalContentUrl) {
		this.originalContentUrl = originalContentUrl;
	}

	public String getPreviewImageUrl() {
		return previewImageUrl;
	}

	public void setPreviewImageUrl(String previewImageUrl) {
		this.previewImageUrl = previewImageUrl;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getStickerId() {
		return stickerId;
	}

	public void setStickerId(String stickerId) {
		this.stickerId = stickerId;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

}
