package com.campushare.apiLayer.controller;

import com.campushare.apiLayer.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.campushare.apiLayer.model.LoginRequest;
import com.campushare.apiLayer.model.User;
import com.campushare.apiLayer.service.APIService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
public class APIController {

    @Autowired
    private APIService service;

    /*  @GetMapping("/users/{userId}")
    public User getUserByUserId(@PathVariable String userId) {
        return service.getUserByUserId(userId);
    } */

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

    @GetMapping("/posts/active")
    public ResponseEntity<List<Post>> getActivePostsFromPostService() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/posts/active";

        ResponseEntity<List<Post>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Post>>() {
                });

        List<Post> posts = response.getBody();
        System.out.println(posts);
        return response;
    }

@GetMapping("/users/{userId}")
public ResponseEntity<User> getUserByIdFromUserService(@PathVariable String userId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromUriString("http://localhost:8081/users/{userId}")
                .buildAndExpand(userId)
                .toUriString();
                System.out.println(url);

        ResponseEntity<User> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<User>() {});

        User user = response.getBody();
        System.out.println(user);
        return response;
    }

     @GetMapping("/users")
     public ResponseEntity<List<User>> getAllUsersFromUserService() {
         RestTemplate restTemplate = new RestTemplate();
         String url = "http://localhost:8081/users";

         ResponseEntity<List<User>> response = restTemplate.exchange(
                 url,
                 HttpMethod.GET,
                 null,
                 new ParameterizedTypeReference<List<User>>() {
                 });

         List<User> user = response.getBody();
         System.out.println(user);
         return response;
     }

     @PostMapping("/users")
     public ResponseEntity<User> createuser(@RequestBody User user) {
         RestTemplate restTemplate = new RestTemplate();
         String url = "http://localhost:8081/users";

         HttpEntity<User> request = new HttpEntity<User>(new User(
                 user.getUserId(),
                 user.getUsername(),
                 user.getPassword(),
                 user.getRole()));

         ResponseEntity<User> response = restTemplate.exchange(
                 url,
                 HttpMethod.POST,
                 request,
                 new ParameterizedTypeReference<User>() {
                 });

         User createdUser = response.getBody();
         System.out.println(createdUser);
         return response;
     }

 /*    @GetMapping("/users/{username}")
    public ResponseEntity<List<User>> getUserByUsernameFromUserService() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/users/{username}";

        ResponseEntity<List<User>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {
                });

        List<User> user = response.getBody();
        System.out.println(user);
        return response;
    } */
    
}