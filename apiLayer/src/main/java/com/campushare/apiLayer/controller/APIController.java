package com.campushare.apiLayer.controller;

import com.campushare.apiLayer.model.*;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
//import com.campushare.apiLayer.service.APIService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class APIController {

    // @GetMapping("/users/{username}")
    // public User getUserByUserName(@PathVariable String username) {
    // return service.getUserByUsername(username);
    // }

   /*  @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
 */
       /*  logic:
        1. receive username and password as login request from client
        2. find user by username via user service - 8081/users/username
        3. verify password against password sent in request
        4. create a jwt for the user to remain logged in and authenticated
        
 */

        // Implement validation and error handling as needed
      /*   System.out.println(loginRequest);
      
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
      
        //User authenticatedUser = service.authenticateUser(username, password);
        ResponseEntity<User> fetchuser = getUserByUsernameFromUserService(username);
        HttpHeaders responseHeaders = new HttpHeaders();
      
        if(fetchuser.getBody().getPassword() == password) {
      
         
            return ResponseEntity.ok().headers(responseHeaders).body(fetchuser.getBody().getUserId());
        } else {
            // Handle authentication failure
            return ResponseEntity.ok().headers(responseHeaders).body(null);
        }
      
      } */
    

      @PostMapping("/login")
      public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
          // Implement validation and error handling as needed

          String username = loginRequest.getUsername();
          String password = loginRequest.getPassword();

          ResponseEntity<User> fetchUserResponse = getUserByUsernameFromUserService(username);

          if (fetchUserResponse.getStatusCode().is2xxSuccessful()) {
              User fetchedUser = fetchUserResponse.getBody();

              // Verify password
              if (fetchedUser != null && fetchedUser.getPassword().equals(password)) {
                  // Create JWT
                  String jwt = createJwt(fetchedUser.getUserId());

                  HttpHeaders responseHeaders = new HttpHeaders();
                  responseHeaders.add("Authorization", "Bearer " + jwt);

                  return ResponseEntity.ok().headers(responseHeaders).body(fetchedUser.getUserId());
              }
          }

          // Handle authentication failure
          return ResponseEntity.status(401).body("Authentication failed");
      }


      private String createJwt(String userId) {
          // You need to provide your own secret key for signing the JWT
          String secretKey = "yourSecretKey";

          // Set the expiration time of the token (e.g., 1 hour from now)
          long expirationTimeMillis = System.currentTimeMillis() + 3600000; // 1 hour
          Date expirationDate = new Date(expirationTimeMillis);

          // Build the JWT
          return Jwts.builder()
                  .setSubject(userId)
                  .setExpiration(expirationDate)
                  .signWith(SignatureAlgorithm.HS512, secretKey)
                  .compact();
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
                new ParameterizedTypeReference<List<Post>>() {
                });

        List<Post> posts = response.getBody();
        System.out.println(posts);
        return response;
    }

    @PostMapping("/join")
    public ResponseEntity requestToJoin(@RequestParam String post, @RequestBody JoinRequest joinRequest){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8086/join";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JoinRequest> requestEntity = new HttpEntity<>(joinRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url + "?post=" + post, requestEntity, String.class);

        return ResponseEntity.ok(responseEntity.getBody());
    }

    @PostMapping("/request-food")
    public ResponseEntity requestFood(@RequestParam String post, @RequestBody FoodRequest foodRequest){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8086/request-food";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<FoodRequest> requestEntity = new HttpEntity<>(foodRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url + "?post=" + post, requestEntity, String.class);

        return ResponseEntity.ok(responseEntity.getBody());
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
                new ParameterizedTypeReference<User>() {});

        User user = response.getBody();
        System.out.println(user);
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

        Post editedPost = response.getBody();
        System.out.println(editedPost);
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

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment>  createComment(@PathVariable String postId, @RequestBody Comment comment) {
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
                new ParameterizedTypeReference<Comment>() {},
                uriVariables
        );

        Comment createdComment = response.getBody();
        System.out.println(createdComment);
        return response;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getAllCommentsByPostId(@PathVariable String postId){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/posts/{postId}/comments";
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("postId", postId);

        ResponseEntity<List<Comment>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comment>>() {},
                uriVariables
        );

        List<Comment> comments = response.getBody();
        System.out.println(comments);
        return response;
    }
}