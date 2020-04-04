package com.destro.linkcalculator.controller;

import com.destro.linkcalculator.model.IssueModel;
import com.destro.linkcalculator.model.ResponseModel;
import com.destro.linkcalculator.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping(value = "/raise-issue")
    public ResponseModel raiseIssue(@RequestBody final IssueModel issue) {
        return issueService.registerRaisedIssue(issue);
    }

    @GetMapping(value = "/get-all-issues/{user-name}")
    public ResponseModel getAllIssues(@PathVariable(name = "user-name") final String name) {
        return issueService.getAllIssues(name);
    }
}
