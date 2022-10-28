package com.api.test.service;

import com.api.test.UserDTO.UserDTO;
import com.api.test.UserRepository.UserRepository;
import com.api.test.domain.User;
import com.api.test.exceptions.DataIntegratyViolationException;
import com.api.test.exceptions.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    public User findById(Integer id) {
        Optional<User> user = userRepository.findById(id);

        return user.orElseThrow(() -> new ObjectNotFoundException("User não encontrado"));
    }

    public List<User> findAll() {

        return userRepository.findAll();
    }

    public User create(UserDTO userDTO) {
        findByEmail(userDTO);
        return userRepository.save(mapper.map(userDTO, User.class));
    }

    public User update(UserDTO userDTO) {
        findByEmail(userDTO);

        return userRepository.save(mapper.map(userDTO, User.class));
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        userRepository.deleteById(id);
    }

    private void findByEmail(UserDTO userDTO) {
        Optional<User> user = userRepository.findByEmail(userDTO.getEmail());
        if (user.isPresent() && !user.get().getId().equals(userDTO.getId())) {
            throw new DataIntegratyViolationException("Email já cadastrado");
        }
    }
}