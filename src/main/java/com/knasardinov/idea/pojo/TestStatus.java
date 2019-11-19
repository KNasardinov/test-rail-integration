package com.knasardinov.idea.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TestStatus {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("color_dark")
    @Expose
    private String colorDark;

    @SerializedName("color_medium")
    @Expose
    private String colorMedium;

    @SerializedName("color_bright")
    @Expose
    private String colorBright;

    @SerializedName("is_system")
    @Expose
    private String isSystem;

    @SerializedName("is_untested")
    @Expose
    private String isUntested;

    @SerializedName("is_final")
    @Expose
    private String isFinal;
}