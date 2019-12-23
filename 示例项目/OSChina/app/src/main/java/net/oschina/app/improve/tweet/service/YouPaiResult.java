package net.oschina.app.improve.tweet.service;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public final class YouPaiResult implements Serializable {
    @SerializedName("image-type")
    private String imageType;

    @SerializedName("image-frames")
    private int imageFrames;

    @SerializedName("image-height")
    private int imageHeight;

    @SerializedName("image-width")
    private int imageWidth;

    private int code;

    private String url;

    private String message;

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public int getImageFrames() {
        return imageFrames;
    }

    public void setImageFrames(int imageFrames) {
        this.imageFrames = imageFrames;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }
}
