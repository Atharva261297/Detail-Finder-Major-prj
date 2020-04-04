package com.destro.linkcalculator.service;

import com.destro.linkcalculator.dao.IssueDao;
import com.destro.linkcalculator.model.IssueModel;
import com.destro.linkcalculator.model.ResponseModel;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueService {

    @Autowired
    private IssueDao issueDao;

    private final Logger logger = LoggerFactory.getLogger(IssueService.class);

    public ResponseModel registerRaisedIssue(final IssueModel issueModel) {
        if (issueDao.addIssue(issueModel)) {
            return new ResponseModel(200, "Successfully registered new issue. We will contact you as soon as possible");
        } else {
            return new ResponseModel(500, "Unable to register the issue.");
        }
    }

    public ResponseModel getAllIssues(final String name) {
        return new ResponseModel(200, new Gson().toJson(issueDao.getAllIssuesForName(name)));
    }
}
