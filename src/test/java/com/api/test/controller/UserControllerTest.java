package com.api.test.controller;

import com.api.test.UserDTO.UserDTO;
import com.api.test.domain.User;
import com.api.test.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class UserControllerTest {

    private UserController controller;
    @Mock
    private ModelMapper mapper;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private RequestAttributes attrs;
    private User user = new User();
    private UserDTO userDTO = new UserDTO();

    private void startUser() {
        user = new User(1, "Vini", "email", "123");
        userDTO = new UserDTO(1, "Vini", "email", "123");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startUser();
        RequestContextHolder.setRequestAttributes(attrs);
        this.controller = new UserController(userService, mapper);
    }

    @Test
    void whenFindByIdThenReturnSucess() {
        when(mapper.map(any(), any())).thenReturn(userDTO);
        when(userService.findById(anyInt())).thenReturn(user);

        ResponseEntity<UserDTO> response = controller.findById(1);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(UserDTO.class, response.getBody().getClass());

        assertEquals(1, response.getBody().getId());
        assertEquals("Vini", response.getBody().getName());
        assertEquals("email", response.getBody().getEmail());
        assertEquals("123", response.getBody().getPassword());
    }

    @Test
    void whenFindAllThenReturnAListOfUserDTO() {
        when(mapper.map(any(), any())).thenReturn(userDTO);
        when(userService.findAll()).thenReturn(List.of(user));

        ResponseEntity<List<UserDTO>> response = controller.findAll();

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(ArrayList.class, response.getBody().getClass());
        assertEquals(UserDTO.class, response.getBody().get(0).getClass());

        assertEquals(1, response.getBody().get(0).getId());
        assertEquals("Vini", response.getBody().get(0).getName());
        assertEquals("email", response.getBody().get(0).getEmail());
        assertEquals("123", response.getBody().get(0).getPassword());
    }

    @Test
    void whenCreateThenReturnCreated() {
        when(userService.create(any())).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<UserDTO> response = controller.create(userDTO);

        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void update() {
        when(userService.update(any())).thenReturn(user);
        when(mapper.map(any(), any())).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = controller.update(1, userDTO);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(UserDTO.class, response.getBody().getClass());

        assertEquals(1, response.getBody().getId());
        assertEquals("Vini", response.getBody().getName());
        assertEquals("email", response.getBody().getEmail());
        assertEquals("123", response.getBody().getPassword());
    }

    @Test
    void whenDeleteThenReturnSuccess() {
        doNothing().when(userService).delete(anyInt());

        ResponseEntity<UserDTO> response = controller.delete(1);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        verify(userService, times(1)).delete(anyInt());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}