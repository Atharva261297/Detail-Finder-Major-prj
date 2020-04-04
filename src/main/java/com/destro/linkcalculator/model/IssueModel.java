package com.destro.linkcalculator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueModel {
    private IssueType issueType;
    private String issueDescription;
    private String contactInfo;
    private String reporterName;
    private String issueStatus;
    private Date creationDate;
}
