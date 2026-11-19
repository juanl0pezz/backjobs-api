package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.client.UserApiRepository;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import com.uap.proiv.jobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserApiRepository repository;

    @Autowired
    public UserServiceImpl(UserApiRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserApiResponse search(int page) {
        UserApiResponse apiResponse = repository.getUsers(page);
        if (apiResponse != null && apiResponse.getData() != null) {
            int nextJobId = 1;
            for (User currentUser : apiResponse.getData()) {
                currentUser.setJobId(nextJobId++);
            }
        }
        return apiResponse;
    }

    @Override
    public User searchById(int id) {
        User foundUser = repository.getUserById(id);
        if (foundUser != null) {
            foundUser.setJobId(1);
        }
        return foundUser;
    }

    @Override
    public void update(User user) {
        try {
            repository.updateUser(user);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo actualizar el usuario: " + ex.getMessage(), ex);
        }
    }
}
