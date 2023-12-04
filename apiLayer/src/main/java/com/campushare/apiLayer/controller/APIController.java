package com.campushare.apiLayer.controller;

import com.campushare.apiLayer.model.*;
//import org.springframework.beans.factory.annotation.Autowired;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
//import com.campushare.apiLayer.service.APIService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import org.mindrot.jbcrypt.BCrypt;


import java.util.*;

@RestController
public class APIController {

    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String predefinedSalt = "$2a$10$abcdefghijklmnopqrstuu";
        String hashedPassword = BCrypt.hashpw(password, predefinedSalt);

        ResponseEntity<User> fetchUserResponse = getUserByUsernameFromUserService(username);

        if (fetchUserResponse.getStatusCode().is2xxSuccessful()) {
            User fetchedUser = fetchUserResponse.getBody();

            if (fetchedUser != null && hashedPassword.equals(fetchedUser.getPassword())) {
                String jwt = createJwt(fetchedUser.getUserId());

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.add("Authorization", "Bearer " + jwt);
                System.out.println(responseHeaders.get("Authorization"));

                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("jwt", jwt);
                responseBody.put("userId", fetchedUser.getUserId());

                return ResponseEntity.ok().headers(responseHeaders).body(responseBody);
            }
        }

        return ResponseEntity.status(401).body(null);

    }

    private String createJwt(String userId) {

        long expirationTimeMillis = System.currentTimeMillis() + 3600000;
        Date expirationDate = new Date(expirationTimeMillis);

        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(expirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            System.out.println("Token: " + token);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(removeBearerPrefix(token))
                    .getBody();


            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String removeBearerPrefix(String token) {
        return token.trim().replaceFirst("Bearer ", "");
    }

    // ***** Join, Notification, and Geolocation Service *****

    @PostMapping("/join")
    public ResponseEntity requestToJoin(@RequestHeader("Authorization") String token, @RequestParam String post, @RequestBody JoinRequest joinRequest) {
        if (isValidToken(token)) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8086/join";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<JoinRequest> requestEntity = new HttpEntity<>(joinRequest, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url + "?post=" + post, requestEntity, String.class);

            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/request-food")
    public ResponseEntity requestFood(@RequestHeader("Authorization") String token, @RequestParam String post, @RequestBody FoodRequest foodRequest) {
        if (isValidToken(token)) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8086/request-food";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<FoodRequest> requestEntity = new HttpEntity<>(foodRequest, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url + "?post=" + post, requestEntity, String.class);

            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationRecord>> getNotifications(@RequestHeader("Authorization") String token, @RequestParam String userID) {
        if (isValidToken(token)) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8088/notifications?userID=" + userID;
            ResponseEntity<NotificationRecord[]> responseEntity = restTemplate.getForEntity(url, NotificationRecord[].class);

            List<NotificationRecord> notifications = Arrays.asList(responseEntity.getBody());

            return ResponseEntity.ok(notifications);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @DeleteMapping("/notifications/{notificationId}")
    public ResponseEntity deleteNotificationRecord(@RequestHeader("Authorization") String token, @PathVariable String notificationId) {
        if (isValidToken(token)) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8088/notifications/" + notificationId;
            restTemplate.delete(url);
            return ResponseEntity.ok("Notification record successfully deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // ***** Join, Notification, and Geolocation Service *****

    // ***** User Service *****

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
                new ParameterizedTypeReference<User>() {
                });

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

    @GetMapping("/users/findBy/{username}")
    public ResponseEntity<User> getUserByUsernameFromUserService(@PathVariable String username) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromUriString("http://localhost:8081/users/findBy/{username}")
                .buildAndExpand(username)
                .toUriString();
        System.out.println(url);

        ResponseEntity<User> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<User>() {
                });

        User user = response.getBody();
        System.out.println(user);
        return response;
    }

    // ***** User Service *****

    // ***** Post Service *****

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestHeader("Authorization") String token, @RequestBody Post post) {
        if (isValidToken(token)) {
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
                    new ParameterizedTypeReference<Post>() {
                    }
            );

            Post createdPost = response.getBody();
            System.out.println(createdPost);
            return response;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/posts/active")
    public ResponseEntity<List<Post>> getActivePostsFromPostService(@RequestHeader("Authorization") String token) {
        if (isValidToken(token)) {
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
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> getPost(@RequestHeader("Authorization") String token, @PathVariable String postId) {
        if (isValidToken(token)) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8082/posts/{postId}";

            ResponseEntity<Post> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Post>() {
                    }
            );

            Post post = response.getBody();
            System.out.println(post);
            return response;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> editPost(@RequestHeader("Authorization") String token, @PathVariable String postId, @RequestBody Post post) {
        if (isValidToken(token)) {
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
                    new ParameterizedTypeReference<Post>() {
                    },
                    uriVariables
            );

            Post editedPost = response.getBody();
            System.out.println(editedPost);
            return response;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Post> deletePost(@RequestHeader("Authorization") String token, @PathVariable String postId) {
        if (isValidToken(token)) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8082/posts/{postId}";
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("postId", postId);

            ResponseEntity<Post> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null,
                    new ParameterizedTypeReference<Post>() {
                    },
                    uriVariables
            );

            return response;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> createComment(@RequestHeader("Authorization") String token, @PathVariable String postId, @RequestBody Comment comment) {
        if (isValidToken(token)) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8082/posts/{postId}/comments";
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("postId", postId);

            HttpEntity<Comment> request =
                    new HttpEntity<Comment>(new Comment(
                            comment.getCommentId(),
                            comment.getPostId(),
                            comment.getComment()));

            ResponseEntity<Comment> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Comment>() {
                    },
                    uriVariables
            );

            Comment createdComment = response.getBody();
            System.out.println(createdComment);
            return response;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getAllCommentsByPostId(@RequestHeader("Authorization") String token, @PathVariable String postId) {
        if (isValidToken(token)) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8082/posts/{postId}/comments";
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("postId", postId);

            ResponseEntity<List<Comment>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Comment>>() {
                    },
                    uriVariables
            );

            List<Comment> comments = response.getBody();
            System.out.println(comments);
            return response;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // ***** Post Service *****

    // ***** Recommendation Service *****

    @GetMapping("/recommendations/{userId}/top-posts")
    public ResponseEntity<List<RecommendationPost>> getRecommendations(@PathVariable String userId, @RequestHeader("Authorization") String token) {
        if (isValidToken(token)) {
            RestTemplate restTemplate = new RestTemplate();
            String recommendationServiceUrl = "http://localhost:8083/recommendations/{userId}/top-posts";

            ResponseEntity<List<RecommendationPost>> response = restTemplate.exchange(
                    recommendationServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<RecommendationPost>>() {},
                    userId
            );

            System.out.println(response.getBody());
            return response;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }


    // ***** Recommendation Service *****

    // ***** Ride Lifecycle Management and Payment Service *****

    // ***** Ride Lifecycle Management and Payment Service *****
}