package com.auth.authmodule.service;

import com.auth.authmodule.dao.UserDao;
import com.auth.authmodule.model.User;
import com.auth.authmodule.request.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return (List<User>) userDao.findAll();
    }

    @Override
    public User updateUserByEmail(String email, UserRequest userRequest) {
        User fetchedUser = userDao.findByEmail(email);
        String firstName = userRequest.getFirstName() != null ? userRequest.getFirstName() : fetchedUser.getFirstName();
        String lastName = userRequest.getLastName() != null ? userRequest.getLastName() : fetchedUser.getLastName();

        if (fetchedUser != null) {
            return userDao.save(User.builder().id(fetchedUser.getId())
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(fetchedUser.getEmail())
                    .password(fetchedUser.getPassword())
                    .build());
        }
        return User.builder().build();
    }

    @Override
    public User save(User user) {
        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDao.save(newUser);
    }
}
