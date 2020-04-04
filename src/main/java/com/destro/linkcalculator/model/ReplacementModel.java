package com.destro.linkcalculator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplacementModel {
    private String reporterName;
    private Map<String, String> replaceMap;
}
