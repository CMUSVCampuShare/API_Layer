package com.campushare.apiLayer.repository;

import com.campushare.apiLayer.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface APIRepository extends MongoRepository<User, String> {
    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);
}
