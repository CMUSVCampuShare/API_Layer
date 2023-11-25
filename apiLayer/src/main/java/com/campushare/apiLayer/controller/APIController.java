package com.campushare.apiLayer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.campushare.apiLayer.model.LoginRequest;
import com.campushare.apiLayer.model.User;
import com.campushare.apiLayer.service.APIService;

@RestController
public class APIController {

    @Autowired
    private APIService service;

    @GetMapping("/users/{userId}")
    public User getUserByUserId(@PathVariable String userId) {
        return service.getUserByUserId(userId);
    }

    @GetMapping("/users/{username}")
    public User getUserByUserName(@PathVariable String username) {
        return service.getUserByUsername(username);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        // Implement validation and error handling as needed
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User authenticatedUser = service.authenticateUser(username, password);

        if (authenticatedUser != null) {
            // Generate a JWT token
            String token = Jwts.builder()
                    .setSubject(username)
                    .signWith(SignatureAlgorithm.HS512, "yourSecretKey") // replace with your secret key
                    .compact();
            return token;
        } else {
            // Handle authentication failure
            return "Invalid credentials";
        }

    }
}