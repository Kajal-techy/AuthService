package com.auth.authmodule.service;

import com.auth.authmodule.model.User;
import com.auth.authmodule.request.UserRequest;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User updateUserByEmail(String email, UserRequest userRequest);

    User save(User user);

}
