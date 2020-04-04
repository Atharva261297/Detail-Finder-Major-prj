package com.destro.linkcalculator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalUserModel {
    private int staffNo;
    private String batchName;
    private UserRole role;
    private String id;
    private String userName;
}
