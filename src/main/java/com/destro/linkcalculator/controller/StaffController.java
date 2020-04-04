package com.destro.linkcalculator.controller;

import com.destro.linkcalculator.model.ReplacementModel;
import com.destro.linkcalculator.model.ResponseModel;
import com.destro.linkcalculator.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.destro.linkcalculator.util.Constants.EMPTY_FIELD;
import static com.destro.linkcalculator.util.Constants.SEPARATOR;

@RestController
@RequestMapping(value = "/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping("/sign-up/{user-name}")
    public ResponseModel signUp(@PathVariable(name = "user-name") final String name) {
        return staffService.signUp(name);
    }

    @GetMapping(value = "/get-all-staff/{batch-name}")
    public ResponseModel getAllStaff(@PathVariable(name = "batch-name") final String batchName) {
        final String correctedBatchName = batchName.replaceAll(EMPTY_FIELD, SEPARATOR);
        return staffService.getAllStaffJsonResponse(correctedBatchName);
    }

    @PostMapping(value = "/update-name")
    public ResponseModel updateName(@RequestBody final ReplacementModel replacementModel) {
        return staffService.updateNames(replacementModel);
    }

    @GetMapping(value = "/get-batch-staff/{user-name}")
    public String getBatchForStaff(@PathVariable(name = "user-name") final String userName) {
        return staffService.getBatchForStaff(userName);
    }
}
