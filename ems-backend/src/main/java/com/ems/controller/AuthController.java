package com.ems.controller;

import com.ems.dto.LoginRequest;
import com.ems.dto.R;
import com.ems.dto.RegisterRequest;
import com.ems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R login(@RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = userService.login(request);
            return R.ok(result);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/register")
    public R register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }
}
