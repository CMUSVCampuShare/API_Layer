/* package com.campushare.apiLayer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.campushare.apiLayer.model.User;
import com.campushare.apiLayer.repository.APIRepository;

@Service
public class APIService {

    @Autowired
    private APIRepository repository;

    public User getUserByUserId(String userId) {
        return repository.findById(userId).orElse(null);
    }

    public User getUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    public User authenticateUser(String username, String password) {
        return repository.findByUsernameAndPassword(username, password);
    }
}
 */