package com.destro.linkcalculator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "authClient", url = "${url.auth}")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.POST, value = "/register")
    String register(@RequestHeader String project_auth, @RequestHeader String user_auth, @RequestBody String userData);

    @RequestMapping(method = RequestMethod.GET, value = "/getData")
    Map<String, String> login(@RequestHeader String project_auth, @RequestHeader String user_auth);

    @RequestMapping(method = RequestMethod.POST, value = "/updateData")
    String updateData(@RequestHeader String project_auth, @RequestHeader String user_auth, @RequestBody String userData);
}
