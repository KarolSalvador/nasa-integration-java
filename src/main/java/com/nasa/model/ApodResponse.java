package com.nasa.model;

import com.google.gson.annotations.SerializedName;

public class ApodResponse {
    private String title;
    private String explanation;
    private String date;
    private String url;
    
    @SerializedName("media_type")
    private String mediaType;

    public String getTitle() { return title; }
    public String getExplanation() { return explanation; }
    public String getDate() { return date; }
    public String getUrl() { return url; }
    public String getMediaType() { return mediaType; }
}