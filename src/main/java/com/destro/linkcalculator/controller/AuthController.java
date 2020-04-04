package com.destro.linkcalculator.controller;

import com.destro.linkcalculator.client.AuthClient;
import com.destro.linkcalculator.model.ResponseModel;
import com.destro.linkcalculator.model.UserModel;
import com.destro.linkcalculator.service.StaffService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthClient client;

    @Value("${project_creds}")
    private String projectAuth;

    @Autowired
    private StaffService service;

    @PostMapping("/register")
    public ResponseModel register(@RequestBody final UserModel userModel, @RequestHeader final String pass) {
        final String userAuth = Base64.getEncoder().encodeToString(userModel.getId().getBytes()) + ":" + pass;
        client.register(projectAuth, userAuth, new Gson().toJson(userModel));
        return service.register(userModel);
    }

    @GetMapping("/login")
    public ResponseModel login(@RequestHeader final String userId, @RequestHeader final String pass) {
        final String userAuth = Base64.getEncoder().encodeToString(userId.getBytes()) + ":" + pass;
        final Map<String, String> map = client.login(projectAuth, userAuth);

        if (map.getOrDefault("code", "").equals("SUCCESS")) {
            return new ResponseModel(200, map.get("data"));
        }

        return new ResponseModel(401, map.getOrDefault("code", "Unknown error"));
    }
}
