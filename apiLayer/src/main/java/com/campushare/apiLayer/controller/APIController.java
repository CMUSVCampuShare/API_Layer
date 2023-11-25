package com.campushare.apiLayer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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

    // @GetMapping("/users/{username}")
    // public User getUserByUserName(@PathVariable String username) {
    // return service.getUserByUsername(username);
    // }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // Implement validation and error handling as needed
        System.out.println(loginRequest);

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User authenticatedUser = service.authenticateUser(username, password);
        System.out.println(authenticatedUser);

        HttpHeaders responseHeaders = new HttpHeaders();

        if (authenticatedUser != null) {
            // Generate a JWT token
            // String token = Jwts.builder()
            // .setSubject(authenticatedUser.getUserId()) // from username changed to
            // authenticatedUser.getUserId()
            // // because we need userId for other services
            // .signWith(SignatureAlgorithm.HS512, "yourSecretKey") // replace with your
            // secret key
            // .compact();
            return ResponseEntity.ok().headers(responseHeaders).body(authenticatedUser.getUserId());
        } else {
            // Handle authentication failure
            return ResponseEntity.ok().headers(responseHeaders).body(null);
        }

    }
}