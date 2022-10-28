package com.api.test.service;

import com.api.test.UserDTO.UserDTO;
import com.api.test.UserRepository.UserRepository;
import com.api.test.domain.User;
import com.api.test.exceptions.DataIntegratyViolationException;
import com.api.test.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class UserServiceImplTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ModelMapper mapper;

    private User user;
    private UserDTO userDTO;
    private Optional<User> optionalUser;
    private UserServiceImpl userService;

    private void startUser() {
        user = new User(1, "Vini", "email", "123");
        userDTO = new UserDTO(1, "Vini", "email", "123");
        optionalUser = Optional.of(new User(1, "Vini", "email", "123"));
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startUser();
        this.userService = new UserServiceImpl(userRepositoryMock, mapper);
    }

    @Test
    void whenFindByIdThenReturnAnUserInstance() {
        when(userRepositoryMock.findById(anyInt())).thenReturn(optionalUser);

        User response = userService.findById(1);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(User.class, user.getClass());
        Assertions.assertEquals(1, response.getId());
        Assertions.assertEquals("Vini", response.getName());
        Assertions.assertEquals("email", response.getEmail());
        Assertions.assertEquals("123", response.getPassword());
    }

    @Test
    void whenFindByIdThenReturnAnObjectNotFoundExcpetion() {
        when(userRepositoryMock.findById(anyInt())).thenThrow(new ObjectNotFoundException("Objeto não encontrado"));
        try {
            userService.findById(1);
        } catch (Exception ex) {
            assertEquals(ObjectNotFoundException.class, ex.getClass());
            assertEquals("Objeto não encontrado", ex.getMessage());
        }
    }

    @Test
    void whenFindAllThenReturnAnListOfUsers() {
        when(userRepositoryMock.findAll()).thenReturn(List.of(user));

        List<User> response = userService.findAll();
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(User.class, response.get(0).getClass());

        assertEquals(1, response.get(0).getId());
        assertEquals("Vini", response.get(0).getName());
        assertEquals("email", response.get(0).getEmail());
        assertEquals("123", response.get(0).getPassword());
    }

    @Test
    void whenCreateThenReturnAnDataIntegrityViolationException() {
        lenient().when(userRepositoryMock.findByEmail(anyString())).thenReturn(optionalUser);

        try {
            optionalUser.get().setId(2);
            userService.create(userDTO);
        } catch (Exception ex) {
            assertEquals(DataIntegratyViolationException.class, ex.getClass());
            assertEquals("Email já cadastrado", ex.getMessage());
        }
    }

    @Test
    void whenUpdateThenReturnAnDataIntegrityViolationException() {
        assertThrows(DataIntegratyViolationException.class, () -> {
            userDTO.setId(2);
            when(userRepositoryMock.findByEmail(any())).thenReturn(optionalUser);
            userService.update(userDTO);
        });
    }
    @Test
    void whenSaveAnUserReturnOk(){
        when(userRepositoryMock.save(any())).thenReturn(user);
        User response = userService.update(userDTO);

        assertNotNull(response);
        assertEquals(User.class, response.getClass());  
    }

    @Test
    void whenDeleteAnUserByIdReturnUserNotFound() {
        when(userRepositoryMock.findById(anyInt())).thenReturn(optionalUser);
        doNothing().when(userRepositoryMock).deleteById(anyInt());

        userService.delete(1);
        verify(userRepositoryMock, times(1)).deleteById(anyInt());
    }

    @Test
    void deleteWithObjectNotFoundException() {
        when(userRepositoryMock.findById(anyInt())).thenThrow(new ObjectNotFoundException("Objeto não encontrado"));

        try {
            userService.delete(1);
        } catch (Exception ex) {
            assertEquals(ObjectNotFoundException.class, ex.getClass());
            assertEquals("Objeto não encontrado", ex.getMessage());
        }
    }
}