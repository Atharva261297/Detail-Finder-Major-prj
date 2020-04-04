package com.destro.linkcalculator.controller;

import com.destro.linkcalculator.model.ResponseModel;
import com.destro.linkcalculator.service.BatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/batch")
public class BatchController {

    @Autowired
    private BatchesService batchesService;

    @GetMapping(value = "/get-all-batches")
    public ResponseModel getAllBatches() {
        return batchesService.getAllBatches();
    }
}
