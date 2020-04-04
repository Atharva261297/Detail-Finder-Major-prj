package com.destro.linkcalculator.controller;

import com.destro.linkcalculator.exception.LinkCalculationException;
import com.destro.linkcalculator.model.ResponseModel;
import com.destro.linkcalculator.service.LinkService;
import com.destro.linkcalculator.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(value = "/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping(value = "/get-link/{user-name}/{date}")
    public ResponseModel getLink(@PathVariable(name = "user-name") final String userName,
                                 @PathVariable(name = "date") final String date) throws LinkCalculationException {
        final String correctedUserName = RequestUtil.getStringFromPreparedString(userName);
        return linkService.getLinkForStaffByName(correctedUserName,
                LocalDate.parse(date, DateTimeFormatter.ofPattern("d-M-yyyy")));
    }


    @GetMapping(value = "/get-link-all/{batch-name}/{date}")
    public ResponseModel getLinkAll(@PathVariable(name = "batch-name") final String batchName,
                                    @PathVariable(name = "date") final String date) {
        final String correctedBatchName = RequestUtil.getStringFromPreparedString(batchName);
        return linkService.getLinkForBatch(correctedBatchName,
                LocalDate.parse(date, DateTimeFormatter.ofPattern("d-M-yyyy")));
    }

}
