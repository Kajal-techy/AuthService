package com.auth.authmodule.dao;

import com.auth.authmodule.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, Integer> {
    User findByEmail(String email);

}
