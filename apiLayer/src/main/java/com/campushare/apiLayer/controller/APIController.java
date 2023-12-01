package com.campushare.apiLayer.controller;

import com.campushare.apiLayer.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.campushare.apiLayer.model.LoginRequest;
import com.campushare.apiLayer.model.User;
import com.campushare.apiLayer.service.APIService;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/posts";

        HttpEntity<Post> request =
                new HttpEntity<Post>(new Post(
                        post.getPostId(),
                        post.getUserId(),
                        post.getTitle(),
                        post.getFrom(),
                        post.getTo(),
                        post.getDetails(),
                        post.getType(),
                        post.getNoOfSeats(),
                        post.getStatus(),
                        post.getTimestamp(),
                        post.getComments()));

        ResponseEntity<Post> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Post>() {}
        );

        Post createdPost = response.getBody();
        System.out.println(createdPost);
        return response;
    }

    @GetMapping("/posts/active")
    public ResponseEntity<List<Post>> getActivePostsFromPostService() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/posts/active";

        ResponseEntity<List<Post>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Post>>() {}
        );

        List<Post> posts = response.getBody();
        System.out.println(posts);
        return response;
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable String postId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/posts/{postId}";

        ResponseEntity<Post> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Post>() {}
        );

        Post post = response.getBody();
        System.out.println(post);
        return response;
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> editPost(@PathVariable String postId, @RequestBody Post post) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/posts/{postId}";
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("postId", postId);

        HttpEntity<Post> request =
                new HttpEntity<Post>(new Post(
                        post.getPostId(),
                        post.getUserId(),
                        post.getTitle(),
                        post.getFrom(),
                        post.getTo(),
                        post.getDetails(),
                        post.getType(),
                        post.getNoOfSeats(),
                        post.getStatus(),
                        post.getTimestamp(),
                        post.getComments()));

        ResponseEntity<Post> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<Post>() {},
                uriVariables
        );

        Post createdPost = response.getBody();
        System.out.println(createdPost);
        return response;
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Post> deletePost(@PathVariable String postId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/posts/{postId}";
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("postId", postId);

        ResponseEntity<Post> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<Post>() {},
                uriVariables
        );

        return response;
    }
}