package com.destro.linkcalculator.service;

import com.destro.linkcalculator.dao.BatchesDao;
import com.destro.linkcalculator.model.ResponseModel;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BatchesService {

    @Autowired
    private BatchesDao batchesDao;

    private final Logger logger = LoggerFactory.getLogger(BatchesService.class);

    public ResponseModel getAllBatches() {
        final List<String> list = batchesDao.getAllBatches();
        if (Objects.nonNull(list)) {
            return new ResponseModel(200, new Gson().toJson(list));
        } else {
            return new ResponseModel(500, "Unable to process the request for get all staff");
        }
    }

    String getStartForBatch(final String batchName) {
        return batchesDao.getStartDateForBatch(batchName);
    }

    Integer getNoOfMembers(final String batchName) {
        return batchesDao.getNoOfMembers(batchName);
    }
}
