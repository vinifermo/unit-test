package com.api.test.service;

import com.api.test.UserDTO.UserDTO;
import com.api.test.domain.User;

import java.util.List;

public interface UserService {

    User findById(Integer id);

    List<User> findAll();

    User create(UserDTO userDTO);

    User update(UserDTO userDTO0);

    void delete(Integer id);
}