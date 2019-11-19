package com.knasardinov.idea.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TestCase {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("section_id")
    @Expose
    private Integer sectionId;

    @SerializedName("template_id")
    @Expose
    private Integer templateId;

    @SerializedName("type_id")
    @Expose
    private Integer typeId;

    @SerializedName("priority_id")
    @Expose
    private Integer priorityId;

    @SerializedName("milestone_id")
    @Expose
    private Object milestoneId;

    @SerializedName("refs")
    @Expose
    private Object refs;

    @SerializedName("created_by")
    @Expose
    private Integer createdBy;

    @SerializedName("created_on")
    @Expose
    private Integer createdOn;

    @SerializedName("updated_by")
    @Expose
    private Integer updatedBy;

    @SerializedName("updated_on")
    @Expose
    private Integer updatedOn;

    @SerializedName("suite_id")
    @Expose
    private Integer suiteId;

    @SerializedName("custom_automation_status")
    @Expose
    private Integer customAutomationStatus;
}