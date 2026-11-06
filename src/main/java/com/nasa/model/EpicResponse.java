package com.nasa.model;

import com.google.gson.annotations.SerializedName;

public class EpicResponse {
    private String identifier;
    private String caption;
    private String date;
    
    @SerializedName("image")
    private String imageName;

    public String getIdentifier() { return identifier; }
    public String getCaption() { return caption; }
    public String getDate() { return date; }
    public String getImageName() { return imageName; }

    // A API do EPIC exige montar a URL dinamicamente com base na data (YYYY/MM/DD) e nome da imagem
    public String getImageUrl(String rawDate) {
        if (rawDate == null || rawDate.length() < 10) return "";
        String[] parts = rawDate.substring(0, 10).split("-");
        String year = parts[0];
        String month = parts[1];
        String day = parts[2];
        return "https://epic.gsfc.nasa.gov/archive/natural/" + year + "/" + month + "/" + day + "/png/" + imageName + ".png";
    }
}