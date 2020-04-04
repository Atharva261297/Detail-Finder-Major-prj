package com.destro.linkcalculator.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "visualModelBuilder")
public class VisualModel {

    @NonNull private String trainNo;
    @NonNull private String deptTime;
    @NonNull private String link;
    @NonNull private String lpmName;
    @NonNull private String colpmName;
    private String returnTrainNo;
}
